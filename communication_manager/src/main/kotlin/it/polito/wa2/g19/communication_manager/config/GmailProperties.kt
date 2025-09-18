package it.polito.wa2.g19.communication_manager.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "gmail")
class GmailProperties {
    lateinit var clientId: String
    lateinit var clientSecret: String
    lateinit var refreshToken: String
}