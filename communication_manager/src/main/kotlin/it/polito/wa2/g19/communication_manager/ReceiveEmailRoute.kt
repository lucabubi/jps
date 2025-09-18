package it.polito.wa2.g19.communication_manager

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.google.mail.GoogleMailEndpoint
import org.springframework.stereotype.Component
import org.apache.camel.EndpointInject
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.URI
import java.nio.charset.StandardCharsets
import com.google.gson.Gson
import mu.KotlinLogging

@Component
class ReceiveEmailRoute : RouteBuilder() {

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
                if (res.statusCode() == 200)
                    logger.info("Email received correctly and message entity created")
            }
    }

    fun sendPost(from: String, subject: String, body: String): HttpResponse<String>{
        val client = HttpClient.newHttpClient()
        val data = mapOf(
            "sender" to from,
            "subject" to subject,
            "body" to body,
            "channel" to "EMAIL"
        )
        val json = Gson().toJson(data)
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://host.docker.internal:8080/API/messages/"))
            .header("Content-Type", "application/json; utf-8")
            .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response
    }
}