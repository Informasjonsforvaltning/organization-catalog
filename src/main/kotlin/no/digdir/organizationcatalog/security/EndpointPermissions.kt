package no.digdir.organizationcatalog.security;

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class EndpointPermissions{

    fun hasAdminPermission(jwt: Jwt): Boolean {
        val authorities: String? = jwt.claims["authorities"] as? String

        return authorities?.contains("system:root:admin") ?: false
    }

}
