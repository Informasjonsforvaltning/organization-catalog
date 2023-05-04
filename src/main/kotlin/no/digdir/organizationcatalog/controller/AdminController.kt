package no.digdir.organizationcatalog.controller

import no.digdir.organizationcatalog.security.EndpointPermissions
import no.digdir.organizationcatalog.service.OrganizationCatalogService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val LOGGER = LoggerFactory.getLogger(AdminController::class.java)

@CrossOrigin
@RestController
@RequestMapping("/admin")
open class AdminController(
    private val catalogService: OrganizationCatalogService,
    private val endpointPermissions: EndpointPermissions
) {

    @PostMapping("/update/stat", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateSTAT(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Any> =
        if (endpointPermissions.hasAdminPermission(jwt)) {
            LOGGER.debug("updating STAT organizations from Enhetsregisteret")
            catalogService.updateSTAT()
            ResponseEntity(HttpStatus.OK)
        } else ResponseEntity(HttpStatus.FORBIDDEN)

}
