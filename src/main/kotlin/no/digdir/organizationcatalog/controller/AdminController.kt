package no.digdir.organizationcatalog.controller

import no.digdir.organizationcatalog.security.EndpointPermissions
import no.digdir.organizationcatalog.service.OrganizationCatalogService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
            catalogService.updateSTAT()
            ResponseEntity(HttpStatus.OK)
        } else ResponseEntity(HttpStatus.FORBIDDEN)

    @PostMapping("/update/fylk", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateFYLK(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Any> =
        if (endpointPermissions.hasAdminPermission(jwt)) {
            catalogService.updateFYLK()
            ResponseEntity(HttpStatus.OK)
        } else ResponseEntity(HttpStatus.FORBIDDEN)

    @PostMapping("/update/komm", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateKOMM(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Any> =
        if (endpointPermissions.hasAdminPermission(jwt)) {
            catalogService.updateKOMM()
            ResponseEntity(HttpStatus.OK)
        } else ResponseEntity(HttpStatus.FORBIDDEN)

}
