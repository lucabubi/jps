package it.polito.wa2.g19.communication_manager

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Message
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.UserCredentials
import it.polito.wa2.g19.communication_manager.config.GmailProperties
import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.util.*
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Component
class EmailRoute(private val gmailProperties: GmailProperties) : RouteBuilder() {
    private val gmailService: Gmail = createGmailService()
    private val logger = mu.KotlinLogging.logger {}

    override fun configure() {
        from("direct:sendEmail")
            .process { exchange ->
                val to = exchange.getIn().getHeader("to", String::class.java)
                val subject = exchange.getIn().getHeader("subject", String::class.java)
                val body = exchange.getIn().getBody(String::class.java)
                val email = createEmail(to, subject, body)
                val message = createMessageWithEmail(email)
                try {
                    gmailService.users().messages().send("me", message).execute()
                    logger.info("Email sent to $to")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

    @Throws(Exception::class)
    private fun createGmailService(): Gmail {
        val credentials = UserCredentials.newBuilder()
            .setClientId(gmailProperties.clientId)
            .setClientSecret(gmailProperties.clientSecret)
            .setRefreshToken(gmailProperties.refreshToken)
            .build()
        val accessToken = credentials.refreshAccessToken()
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
        val googleCredentials = GoogleCredentials.create(accessToken)
        val scopedCredentials = googleCredentials.createScoped(listOf("https://www.googleapis.com/auth/gmail.send"))

        return Gmail.Builder(httpTransport, jsonFactory, HttpCredentialsAdapter(scopedCredentials))
            .setApplicationName("Camel Gmail Send Email Example")
            .build()
    }

    @Throws(Exception::class)
    private fun createEmail(to: String, subject: String, bodyText: String): MimeMessage {
        val props = Properties()
        val session = Session.getDefaultInstance(props, null)
        val email = MimeMessage(session)
        email.addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(to))
        email.subject = subject
        email.setText(bodyText)

        return email
    }

    @Throws(Exception::class)
    private fun createMessageWithEmail(emailContent: MimeMessage): Message {
        val buffer = ByteArrayOutputStream()
        emailContent.writeTo(buffer)
        val bytes = buffer.toByteArray()
        val encodedEmail = Base64.getEncoder().encodeToString(bytes)
        return Message().apply { raw = encodedEmail }
    }
}