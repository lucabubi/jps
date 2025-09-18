package it.polito.wa2.g19.communication_manager

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private lateinit var jwkSetUri: String

    @Bean
    fun jwtDecoder(): NimbusJwtDecoder {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build()
    }

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val grantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles")
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_")

        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter)
        return jwtAuthenticationConverter
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        return http.authorizeHttpRequests {
            it.requestMatchers("/API/emails/**").hasRole("operator")
            it.anyRequest().permitAll()
        }
            .oauth2ResourceServer {
                it.jwt { jwtConfigurer ->
                    jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter())
                }
            }
            .sessionManagement { it.sessionCreationPolicy( SessionCreationPolicy.STATELESS) }
            .csrf { it.disable() }
            .cors { it.disable() }
            .build()
    }
}
