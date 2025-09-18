package it.polito.wa2.g19.gatewayapi

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class HomeController {

    @GetMapping("", "/")
    fun home(): Map<String, Any?> {
        return mapOf("name" to "home", "date" to LocalDateTime.now())
    }

    @GetMapping("", "/secure")
    fun secure(): Map<String, Any?> {
        val authentication = SecurityContextHolder.getContext().authentication

        return mapOf("name" to "secure", "date" to LocalDateTime.now(), "principal" to authentication.principal)
    }

    @CrossOrigin(origins = ["http://host.docker.internal:3000"])
    @GetMapping("/me")
    fun me(
        @CookieValue(name = "XSRF-TOKEN", required = false)
        xsrf: String?,
        authentication: Authentication?
    ): Map<String, Any?> {
        val principal: OidcUser? = authentication?.principal as? OidcUser

        val preferredUsername = principal?.getClaim<String>("preferred_username") ?: ""
        val fullName = ( principal?.getClaim<String>("given_name") + " " + principal?.getClaim<String>("family_name") )
        val roles = principal?.getClaim<List<String>>("roles") ?: listOf()

        return mapOf(
            "name" to preferredUsername,
            "fullName" to fullName,
            "roles" to roles,
            "loginUrl" to "/oauth2/authorization/oidc-app-client",
            "logoutUrl" to "/logout",
            "principal" to principal,
            "xsrfToken" to xsrf,
        )
    }
}