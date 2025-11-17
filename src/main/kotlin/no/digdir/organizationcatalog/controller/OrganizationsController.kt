package no.digdir.organizationcatalog.controller

import jakarta.validation.ConstraintViolationException
import no.digdir.organizationcatalog.configuration.AppProperties
import no.digdir.organizationcatalog.jena.ExternalUrls
import no.digdir.organizationcatalog.jena.JenaType
import no.digdir.organizationcatalog.jena.acceptHeaderToJenaType
import no.digdir.organizationcatalog.jena.jenaResponse
import no.digdir.organizationcatalog.model.Organization
import no.digdir.organizationcatalog.security.EndpointPermissions
import no.digdir.organizationcatalog.service.OrganizationCatalogService
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val LOGGER = LoggerFactory.getLogger(OrganizationsController::class.java)

@CrossOrigin
@RestController
@RequestMapping("/organizations")
open class OrganizationsController(
    private val catalogService: OrganizationCatalogService,
    private val appProperties: AppProperties,
    private val endpointPermissions: EndpointPermissions,
) {
    @PutMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateOrganization(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: String,
        @RequestBody organization: Organization,
    ): ResponseEntity<Organization> =
        if (endpointPermissions.hasAdminPermission(jwt)) {
            try {
                LOGGER.debug("update organization $id")
                catalogService
                    .updateEntry(id, organization)
                    ?.let { updated -> ResponseEntity(updated, HttpStatus.OK) }
                    ?: ResponseEntity(HttpStatus.NOT_FOUND)
            } catch (exception: Exception) {
                LOGGER.error("error updating organization $id", exception)
                when (exception) {
                    is ConstraintViolationException -> ResponseEntity<Organization>(HttpStatus.BAD_REQUEST)
                    is DuplicateKeyException -> ResponseEntity(HttpStatus.CONFLICT)
                    else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
                }
            }
        } else {
            ResponseEntity(HttpStatus.FORBIDDEN)
        }

    @GetMapping(
        "/{id}",
        produces = [
            "application/json",
            "application/xml",
            "application/ld+json",
            "application/rdf+json",
            "application/rdf+xml",
            "text/turtle",
        ],
    )
    fun getOrganizationById(
        @RequestHeader(HttpHeaders.ACCEPT) accept: String?,
        @PathVariable id: String,
    ): ResponseEntity<Any> {
        LOGGER.debug("get organization $id")
        val jenaType = acceptHeaderToJenaType(accept)
        val organization = catalogService.getByOrgnr(id)

        val urls =
            ExternalUrls(
                organizationCatalog = appProperties.organizationCatalogUrl,
                municipality = appProperties.municipalityUrl,
            )

        return when {
            organization == null -> ResponseEntity(HttpStatus.NOT_FOUND)
            jenaType == JenaType.NOT_ACCEPTABLE -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            jenaType == JenaType.NOT_JENA -> ResponseEntity(organization, HttpStatus.OK)
            else -> ResponseEntity(organization.jenaResponse(jenaType, urls), HttpStatus.OK)
        }
    }

    @GetMapping(
        "/delegated",
        produces = [
            "application/json",
            "application/xml",
            "application/ld+json",
            "application/rdf+json",
            "application/rdf+xml",
            "text/turtle",
        ],
    )
    fun getDelegatedOrganizations(
        @RequestHeader(HttpHeaders.ACCEPT) accept: String?,
    ): ResponseEntity<Any> {
        LOGGER.debug("get organizations with delegation permissions")
        val jenaType = acceptHeaderToJenaType(accept)
        val organizations = catalogService.getOrganizationsWithDelegationPermissions()

        val urls =
            ExternalUrls(
                organizationCatalog = appProperties.organizationCatalogUrl,
                municipality = appProperties.municipalityUrl,
            )

        return when {
            organizations.isEmpty() -> ResponseEntity(HttpStatus.NOT_FOUND)
            jenaType == JenaType.NOT_ACCEPTABLE -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            jenaType == JenaType.NOT_JENA -> ResponseEntity(organizations, HttpStatus.OK)
            else -> ResponseEntity(organizations.jenaResponse(jenaType, urls), HttpStatus.OK)
        }
    }

    @GetMapping(
        produces = [
            "application/json",
            "application/xml",
            "application/ld+json",
            "application/rdf+json",
            "application/rdf+xml",
            "text/turtle",
        ],
    )
    fun getOrganizations(
        @RequestHeader(HttpHeaders.ACCEPT) accept: String?,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) orgPath: String?,
        @RequestParam(required = false) organizationId: List<String>?,
        @RequestParam(name = "includesubordinate", required = false) includeSubordinate: Boolean = true,
    ): ResponseEntity<Any> {
        LOGGER.debug("get organizations")
        val jenaType = acceptHeaderToJenaType(accept)
        val organizations = catalogService.getOrganizations(name, organizationId, orgPath, includeSubordinate)

        val urls =
            ExternalUrls(
                organizationCatalog = appProperties.organizationCatalogUrl,
                municipality = appProperties.municipalityUrl,
            )

        return when (jenaType) {
            JenaType.NOT_ACCEPTABLE -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            JenaType.NOT_JENA -> ResponseEntity(organizations, HttpStatus.OK)
            else -> ResponseEntity(organizations.jenaResponse(jenaType, urls), HttpStatus.OK)
        }
    }

    @PostMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateFromEnhetsregisteret(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: String,
    ): ResponseEntity<Organization> =
        if (endpointPermissions.hasAdminPermission(jwt)) {
            LOGGER.debug("update organization with id $id with data from Enhetsregisteret")
            catalogService
                .updateEntryFromEnhetsregisteret(id)
                ?.let { updated -> ResponseEntity(updated, HttpStatus.OK) }
                ?: ResponseEntity(HttpStatus.NOT_FOUND)
        } else {
            ResponseEntity(HttpStatus.FORBIDDEN)
        }

    @GetMapping("/orgpath/{org}", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getOrgPath(
        @PathVariable org: String,
    ): ResponseEntity<String> {
        LOGGER.debug("get orgPath for $org")
        return ResponseEntity(catalogService.getOrgPath(org), HttpStatus.OK)
    }
}
