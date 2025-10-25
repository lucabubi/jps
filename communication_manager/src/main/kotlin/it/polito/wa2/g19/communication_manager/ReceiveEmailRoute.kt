package it.polito.wa2.g19.communication_manager

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.google.mail.GoogleMailEndpoint
import org.springframework.stereotype.Component
import org.apache.camel.EndpointInject
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClientResponseException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired

data class CrmResponse(val statusCode: Int, val body: String?)

@Component
class ReceiveEmailRoute : RouteBuilder() {

    @Autowired
    lateinit var webClient: WebClient

    @EndpointInject("google-mail:messages/get")
    lateinit var googleMailEndpoint: GoogleMailEndpoint

    private val logger = KotlinLogging.logger {}

    override fun configure() {
        from("google-mail-stream:0?markAsRead=true&scopes=https://mail.google.com&labels=INBOX")
            .process { exchange ->
                val regex = "<(.*?)>".toRegex()
                val id = exchange.getIn().getHeader("CamelGoogleMailId").toString()
                val message = googleMailEndpoint.client.users().messages().get("me", id).execute()
                val subject = message.payload.headers
                    .find { it.name.equals("subject", true) }?.value ?: ""
                val from = message.payload.headers
                    .find { it.name.equals("from", true) }?.value ?: ""
                val sender = regex.find(from)?.value?.removePrefix("<")?.removeSuffix(">") ?: ""

                val res = sendPost(sender, subject, message.snippet)

                if (res.statusCode in 200..299) {
                    logger.info("Email received correctly and message entity created. Status: ${res.statusCode}")
                } else {
                    logger.error("Error in creating message entity for received email: ${res.statusCode} - ${res.body}")
                    logger.error("Email details: from=$sender, subject=$subject")
                }
            }
    }

    fun sendPost(from: String, subject: String, body: String): CrmResponse {
        val uri = "/API/messages/"

        val data = mapOf(
            "sender" to from,
            "subject" to subject,
            "body" to body,
            "channel" to "EMAIL"
        )

        try {
            val responseBody = webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(data)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()

            return CrmResponse(200, responseBody)

        } catch (e: WebClientResponseException) {
            logger.error("HTTP error from CRM/API-GW: ${e.statusCode} - ${e.responseBodyAsString}")
            return CrmResponse(e.statusCode.value(), e.responseBodyAsString)
        } catch (e: Exception) {
            logger.error("Generic WebClient error: ${e.message}", e)
            return CrmResponse(500, e.message)
        }
    }
}
