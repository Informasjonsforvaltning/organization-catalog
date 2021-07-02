package no.brreg.informasjonsforvaltning.organizationcatalogue.controller

import no.brreg.informasjonsforvaltning.organizationcatalogue.configuration.AppProperties
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.Organization
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.ExternalUrls
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.JenaType
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.acceptHeaderToJenaType
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.jenaResponse
import no.brreg.informasjonsforvaltning.organizationcatalogue.security.EndpointPermissions
import no.brreg.informasjonsforvaltning.organizationcatalogue.service.OrganizationCatalogueService
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import javax.validation.ConstraintViolationException

private val LOGGER = LoggerFactory.getLogger(OrganizationsController::class.java)

@CrossOrigin
@RestController
@RequestMapping("/organizations")
open class OrganizationsController(
    private val catalogueService: OrganizationCatalogueService,
    private val appProperties: AppProperties,
    private val endpointPermissions: EndpointPermissions
) {

    @PutMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateOrganization(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: String,
        @RequestBody organization: Organization
    ): ResponseEntity<Organization> =
        if (endpointPermissions.hasAdminPermission(jwt)) {
            try {
                LOGGER.debug("update organization $id")
                catalogueService
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
        } else ResponseEntity(HttpStatus.FORBIDDEN)

    @GetMapping(
        "/{id}",
        produces = ["application/json", "application/xml", "application/ld+json", "application/rdf+json", "application/rdf+xml", "text/turtle"]
    )
    fun getOrganizationById(
        @RequestHeader(HttpHeaders.ACCEPT) accept: String?,
        @PathVariable id: String
    ): ResponseEntity<Any> {
        LOGGER.debug("get organization $id")
        val jenaType = acceptHeaderToJenaType(accept)
        val organization = catalogueService.getByOrgnr(id)

        val urls = ExternalUrls(
            organizationCatalogue = appProperties.organizationCatalogueUrl,
            municipality = appProperties.municipalityUrl
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
        produces = ["application/json", "application/xml", "application/ld+json", "application/rdf+json", "application/rdf+xml", "text/turtle"]
    )
    fun getDelegatedOrganizations(@RequestHeader(HttpHeaders.ACCEPT) accept: String?): ResponseEntity<Any> {
        LOGGER.debug("get organizations with delegation permissions")
        val jenaType = acceptHeaderToJenaType(accept)
        val organizations = catalogueService.getOrganizationsWithDelegationPermissions()

        val urls = ExternalUrls(
            organizationCatalogue = appProperties.organizationCatalogueUrl,
            municipality = appProperties.municipalityUrl
        )

        return when {
            organizations.isEmpty() -> ResponseEntity(HttpStatus.NOT_FOUND)
            jenaType == JenaType.NOT_ACCEPTABLE -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            jenaType == JenaType.NOT_JENA -> ResponseEntity(organizations, HttpStatus.OK)
            else -> ResponseEntity(organizations.jenaResponse(jenaType, urls), HttpStatus.OK)
        }
    }

    @GetMapping(produces = ["application/json", "application/xml", "application/ld+json", "application/rdf+json", "application/rdf+xml", "text/turtle"])
    fun getOrganizations(
        @RequestHeader(HttpHeaders.ACCEPT) accept: String?,
        @RequestParam name: String?,
        @RequestParam organizationId: List<String>?
    ): ResponseEntity<Any> {
        when {
            organizationId == null && name == null -> LOGGER.debug("get all organizations")
            organizationId == null -> LOGGER.debug("get organizations filtered by name: $name")
            name == null -> LOGGER.debug("get organizations filtered by ids: $organizationId")
            else -> LOGGER.debug("get organizations filtered by ids: $organizationId and name: $name")
        }
        val jenaType = acceptHeaderToJenaType(accept)
        val organizations = catalogueService.getOrganizations(name, organizationId)

        val urls = ExternalUrls(
            organizationCatalogue = appProperties.organizationCatalogueUrl,
            municipality = appProperties.municipalityUrl
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
        @PathVariable id: String
    ): ResponseEntity<Organization> =
        if (endpointPermissions.hasAdminPermission(jwt)) {
            LOGGER.debug("update organization with id $id with data from Enhetsregisteret")
            catalogueService.updateEntryFromEnhetsregisteret(id)
                ?.let { updated -> ResponseEntity(updated, HttpStatus.OK) }
                ?: ResponseEntity(HttpStatus.NOT_FOUND)
        } else ResponseEntity(HttpStatus.FORBIDDEN)


    @GetMapping("/orgpath/{org}", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getOrgPath(@PathVariable org: String): ResponseEntity<String> {
        LOGGER.debug("get orgPath for $org")
        return ResponseEntity(catalogueService.getOrgPath(org), HttpStatus.OK)
    }
}
