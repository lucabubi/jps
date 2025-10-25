package it.polito.wa2.g19.communication_manager.config

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import java.nio.charset.StandardCharsets
import java.util.*

@Configuration
class WebClientConfig(
    @param:Value("\${cm.api.base-url:http://host.docker.internal:8080}") private val baseUrl: String,
    @param:Value("\${cm.oauth2.registration-id:keycloak}") private val registrationId: String
) {
    val logger = KotlinLogging.logger {}

    @Bean
    fun authorizedClientManager(
        clientRegistrationRepository: ClientRegistrationRepository,
        authorizedClientService: OAuth2AuthorizedClientService
    ): OAuth2AuthorizedClientManager {
        val authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials()
            .build()

        val authorizedClientManager = AuthorizedClientServiceOAuth2AuthorizedClientManager(
            clientRegistrationRepository,
            authorizedClientService
        )
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)

        return authorizedClientManager
    }

    @Bean
    fun webClient(authorizedClientManager: OAuth2AuthorizedClientManager): WebClient {
        val oauth = ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)
        oauth.setDefaultClientRegistrationId(registrationId)
        logger.info("WebClient configured with OAuth2 client credentials (registrationId='${registrationId}', baseUrl='${baseUrl}')")

        return WebClient.builder()
            .baseUrl(baseUrl)
            .filter(oauth)
            .filter(jwtDebugFilter())
            .build()
    }

    private fun jwtDebugFilter(): ExchangeFilterFunction = ExchangeFilterFunction { request, next ->
        try {
            val authHeader = request.headers()[HttpHeaders.AUTHORIZATION]?.firstOrNull()
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                val jwt = authHeader.removePrefix("Bearer ")
                val parts = jwt.split('.')
                if (parts.size >= 2) {
                    val payloadJson = String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8)
                    if (logger.isDebugEnabled) {
                        logger.debug { "Outgoing JWT payload: $payloadJson" }
                    } else {
                        logger.info { "Outgoing JWT present (aud: ${extractField(payloadJson, "aud")} | azp: ${extractField(payloadJson, "azp")})" }
                    }
                } else {
                    logger.warn { "Authorization header present but token format is unexpected" }
                }
            } else {
                logger.warn { "No Authorization header on outgoing request to ${request.url()}" }
            }
        } catch (e: Exception) {
            logger.warn(e) { "Failed to decode outgoing JWT for request to ${request.url()}" }
        }
        next.exchange(request)
    }

    private fun extractField(json: String, field: String): Any? {
        return try {
            val pattern = Regex(""""$field"\s*:\s*("[^"]*"|\[[^]*]|\{[^}]*}|[^,}\n\r]*)""")
            val m = pattern.find(json)
            m?.groupValues?.getOrNull(1)
        } catch (_: Exception) { null }
    }
}
