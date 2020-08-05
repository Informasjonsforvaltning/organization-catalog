package no.brreg.informasjonsforvaltning.organizationcatalogue.controller

import no.brreg.informasjonsforvaltning.organizationcatalogue.configuration.AppProperties
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.api.OrganizationCatalogueApi
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.model.Organization
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.ExternalUrls
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.JenaType
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.acceptHeaderToJenaType
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.jenaResponse
import no.brreg.informasjonsforvaltning.organizationcatalogue.security.EndpointPermissions
import no.brreg.informasjonsforvaltning.organizationcatalogue.service.OrganizationCatalogueService
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import javax.servlet.http.HttpServletRequest
import javax.validation.ConstraintViolationException

private val LOGGER = LoggerFactory.getLogger(OrganizationsApiImpl::class.java)

@CrossOrigin
@Controller
open class OrganizationsApiImpl(
    private val catalogueService: OrganizationCatalogueService,
    private val appProperties: AppProperties,
    private val endpointPermissions: EndpointPermissions
) : OrganizationCatalogueApi {

    @RequestMapping(value = ["/ping"], method = [GET], produces = ["text/plain"])
    fun ping(): ResponseEntity<String> =
        ResponseEntity.ok("pong")

    @RequestMapping(value = ["/ready"], method = [GET])
    fun ready(): ResponseEntity<Void> =
        ResponseEntity.ok().build()

    override fun updateOrganization(httpServletRequest: HttpServletRequest?, jwt: Jwt?, organizationId: String, organization: Organization): ResponseEntity<Organization> =
        if (endpointPermissions.hasAdminPermission(jwt)) {
            try {
                LOGGER.info("update organization $organizationId")
                catalogueService
                    .updateEntry(organizationId, organization)
                    ?.let { updated -> ResponseEntity(updated, HttpStatus.OK) }
                    ?: ResponseEntity(HttpStatus.NOT_FOUND)
            } catch (exception: Exception) {
                LOGGER.info("error updating organization $organizationId")
                when (exception) {
                    is ConstraintViolationException -> ResponseEntity<Organization>(HttpStatus.BAD_REQUEST)
                    is DuplicateKeyException -> ResponseEntity(HttpStatus.CONFLICT)
                    else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
                }
            }
        } else ResponseEntity(HttpStatus.FORBIDDEN)

    override fun getOrganizationById(httpServletRequest: HttpServletRequest, jwt: Jwt?, organizationId: String): ResponseEntity<Any> {
        LOGGER.info("get organization $organizationId")
        val jenaType = acceptHeaderToJenaType(httpServletRequest.getHeader("Accept"))
        val organization = catalogueService.getByOrgnr(organizationId)

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

    override fun getDelegatedOrganizations(httpServletRequest: HttpServletRequest, jwt: Jwt?): ResponseEntity<Any> {
        LOGGER.info("get organizations with delegation permissions")
        val jenaType = acceptHeaderToJenaType(httpServletRequest.getHeader("Accept"))
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

    override fun getOrganizations(httpServletRequest: HttpServletRequest, jwt: Jwt?, name: String?, organizationId: String?): ResponseEntity<Any> {
        LOGGER.info("get organizations id: $organizationId and name: $name")
        val jenaType = acceptHeaderToJenaType(httpServletRequest.getHeader("Accept"))
        val organizations = catalogueService.getOrganizations(name, organizationId)

        val urls = ExternalUrls(
            organizationCatalogue = appProperties.organizationCatalogueUrl,
            municipality = appProperties.municipalityUrl
        )

        return when {
            jenaType == JenaType.NOT_ACCEPTABLE -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            jenaType == JenaType.NOT_JENA -> ResponseEntity(organizations, HttpStatus.OK)
            else -> ResponseEntity(organizations.jenaResponse(jenaType, urls), HttpStatus.OK)
        }
    }

    override fun getOrgPath(httpServletRequest: HttpServletRequest, jwt: Jwt?, org: String): ResponseEntity<String> {
        LOGGER.info("get orgPath for $org")
        return ResponseEntity(catalogueService.getOrgPath(org), HttpStatus.OK)
    }
}
