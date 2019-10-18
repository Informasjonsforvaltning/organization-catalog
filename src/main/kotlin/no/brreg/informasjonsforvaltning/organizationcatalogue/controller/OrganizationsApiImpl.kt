package no.brreg.informasjonsforvaltning.organizationcatalogue.controller

import no.brreg.informasjonsforvaltning.organizationcatalogue.security.EndpointPermissions
import no.brreg.informasjonsforvaltning.organizationcatalogue.configuration.ProfileConditionalValues
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.api.OrganizationsApi
import javax.servlet.http.HttpServletRequest
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.model.Organization
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.ExternalUrls
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.JenaType
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.acceptHeaderToJenaType
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.domainsJenaResponse
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.jenaResponse
import no.brreg.informasjonsforvaltning.organizationcatalogue.service.OrganizationCatalogueService
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import javax.validation.ConstraintViolationException

private val LOGGER = LoggerFactory.getLogger(OrganizationsApiImpl::class.java)

@Controller
open class OrganizationsApiImpl (
    private val catalogueService: OrganizationCatalogueService,
    private val profileConditionalValues: ProfileConditionalValues,
    private val endpointPermissions: EndpointPermissions
): OrganizationsApi {

    @RequestMapping(value = ["/ping"], method = [GET], produces = ["text/plain"])
    fun ping(): ResponseEntity<String> =
        ResponseEntity.ok("pong")

    @RequestMapping(value = ["/ready"], method = [GET])
    fun ready(): ResponseEntity<Void> =
        ResponseEntity.ok().build()

    override fun updateOrganization(httpServletRequest: HttpServletRequest?, organizationId: String, organization: Organization): ResponseEntity<Organization> =
        if (endpointPermissions.hasAdminPermission()) {
            try {
                catalogueService
                    .updateEntry(organizationId, organization)
                    ?.let { updated -> ResponseEntity(updated, HttpStatus.OK) }
                    ?: ResponseEntity(HttpStatus.NOT_FOUND)
            } catch (exception: Exception) {
                when (exception) {
                    is ConstraintViolationException -> ResponseEntity<Organization>(HttpStatus.BAD_REQUEST)
                    is DuplicateKeyException -> ResponseEntity(HttpStatus.CONFLICT)
                    else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
                }
            }
        } else ResponseEntity(HttpStatus.FORBIDDEN)

    override fun getOrganizationById(httpServletRequest: HttpServletRequest, organizationId: String): ResponseEntity<Any> {
        val jenaType = acceptHeaderToJenaType(httpServletRequest.getHeader("Accept"))
        val organization = catalogueService.getByOrgnr(organizationId)

        val urls = ExternalUrls(
            organizationCatalogue = profileConditionalValues.organizationCatalogueUrl(),
            municipality = profileConditionalValues.municipalityUrl()
        )

        return when {
            organization == null -> ResponseEntity(HttpStatus.NOT_FOUND)
            jenaType == JenaType.NOT_ACCEPTABLE -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            jenaType == JenaType.NOT_JENA -> ResponseEntity(organization, HttpStatus.OK)
            else -> ResponseEntity(organization.jenaResponse(jenaType, urls), HttpStatus.OK)
        }
    }

    override fun getOrganizationDomains(httpServletRequest: HttpServletRequest, organizationId: String): ResponseEntity<Any> {
        val jenaType = acceptHeaderToJenaType(httpServletRequest.getHeader("Accept"))
        val organization = catalogueService.getByOrgnr(organizationId)

        val urls = ExternalUrls(
            organizationDomains = profileConditionalValues.organizationDomainsUrl()
        )

        return when {
            jenaType == JenaType.NOT_ACCEPTABLE -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            organization == null -> ResponseEntity(HttpStatus.NOT_FOUND)
            jenaType == JenaType.NOT_JENA -> ResponseEntity(organization.domains, HttpStatus.OK)
            else -> ResponseEntity(organization.domainsJenaResponse(jenaType, urls), HttpStatus.OK)
        }
    }

    override fun getOrganizations(httpServletRequest: HttpServletRequest, name: String?, organizationId: String?): ResponseEntity<Any> {
        val jenaType = acceptHeaderToJenaType(httpServletRequest.getHeader("Accept"))
        val organizations = catalogueService.getOrganizations(name, organizationId)

        val urls = ExternalUrls(
            organizationCatalogue = profileConditionalValues.organizationCatalogueUrl(),
            municipality = profileConditionalValues.municipalityUrl()
        )

        return when {
            jenaType == JenaType.NOT_ACCEPTABLE -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            jenaType == JenaType.NOT_JENA -> ResponseEntity(organizations, HttpStatus.OK)
            else -> ResponseEntity(organizations.jenaResponse(jenaType, urls), HttpStatus.OK)
        }
    }
}
