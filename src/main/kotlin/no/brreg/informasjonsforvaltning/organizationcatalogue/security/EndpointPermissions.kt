package no.brreg.informasjonsforvaltning.organizationcatalogue.security;

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class EndpointPermissions{

    fun hasAdminPermission(jwt: Jwt?): Boolean {
        if (jwt == null) return false

        val authorities: String? = jwt.claims["authorities"] as? String

        return authorities?.contains("system:root:admin") ?: false
    }

}
