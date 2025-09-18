package it.polito.wa2.g19.crm2

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
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
            it.requestMatchers(HttpMethod.GET, "/API/customers/**").hasAnyRole("operator", "customer", "professional")
            it.requestMatchers(HttpMethod.POST, "/API/customers/**").hasRole("operator")
            it.requestMatchers(HttpMethod.PUT, "/API/customers/**").hasRole("operator")
            it.requestMatchers(HttpMethod.GET, "/API/professionals/**").hasAnyRole("operator", "customer", "professional")
            it.requestMatchers(HttpMethod.POST, "/API/professionals/**").hasRole("operator")
            it.requestMatchers(HttpMethod.PUT, "/API/professionals/**").hasRole("operator")
            it.requestMatchers(HttpMethod.GET, "/API/joboffers/**").hasAnyRole("operator", "customer", "professional")
            it.requestMatchers(HttpMethod.POST, "/API/joboffers/**").hasRole("operator")
            it.requestMatchers(HttpMethod.PUT, "/API/joboffers/**").hasRole("operator")
            it.requestMatchers(HttpMethod.GET, "/API/messages/**").hasAnyRole("operator", "customer", "professional")
            it.requestMatchers(HttpMethod.POST, "/API/messages/**").hasRole("operator")
            it.requestMatchers(HttpMethod.PUT, "/API/messages/**").hasRole("operator")
            it.requestMatchers(HttpMethod.GET, "/API/contacts/**").hasAnyRole("operator", "customer", "professional")
            it.requestMatchers(HttpMethod.POST, "/API/contacts/**").hasRole("operator")
            it.requestMatchers(HttpMethod.PUT, "/API/contacts/**").hasRole("operator")

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
