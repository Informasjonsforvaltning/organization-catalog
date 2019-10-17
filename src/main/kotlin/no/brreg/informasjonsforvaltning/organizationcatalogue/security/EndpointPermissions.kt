package no.brreg.informasjonsforvaltning.organizationcatalogue.security;

import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class EndpointPermissions{
    private fun authentication(): Authentication {
        return SecurityContextHolder.getContext().authentication
    }

    fun hasAdminPermission(): Boolean {
        val requiredAuthority = SimpleGrantedAuthority("admin")

        return authentication().authorities.contains(requiredAuthority)
    }
}