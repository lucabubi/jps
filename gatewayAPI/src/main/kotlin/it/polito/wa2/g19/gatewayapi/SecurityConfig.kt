package it.polito.wa2.g19.gatewayapi

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SecurityConfig(val crr: ClientRegistrationRepository) {

    // Configura il logout per reindirizzare a una pagina dopo la logout
    fun oidcLogoutSuccessHandler() = OidcClientInitiatedLogoutSuccessHandler(crr)
        .also { it.setPostLogoutRedirectUri("http://localhost:3000/") }

    // Configura il successo del login
    fun oauth2AuthenticationSuccessHandler(): AuthenticationSuccessHandler {
        return AuthenticationSuccessHandler { _, response, _ ->
            // Reindirizza a una pagina del frontend dopo il login
            response.sendRedirect("http://localhost:3000/") // Modifica con la tua URL di destinazione
        }
    }

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .authorizeRequests { it ->
                it.requestMatchers("/", "login", "logout").permitAll()  // Endpoint pubblici
                it.requestMatchers("/secure").authenticated()          // Endpoint protetti da login
                it.requestMatchers("/user-interface").permitAll()     // Il frontend è accessibile senza login
                it.anyRequest().permitAll()                             // Altre risorse accessibili pubblicamente
            }
            .oauth2Login { oauth2Login ->
                oauth2Login.successHandler(oauth2AuthenticationSuccessHandler()) // Aggiungi il success handler
            }
            .logout { logout ->
                logout.logoutSuccessHandler(oidcLogoutSuccessHandler()) // Gestisci il logout
                logout.permitAll()  // Make sure the logout URL is publicly accessible
            }
            .build()
    }
}

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000") // Modifica con il tuo dominio frontend
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)  // Consente di inviare cookie di autenticazione
    }
}



