package no.brreg.organizationcatalogue.controller

import javax.servlet.http.HttpServletRequest
import no.brreg.organizationcatalogue.generated.model.Organization
import no.brreg.organizationcatalogue.jena.JenaType
import no.brreg.organizationcatalogue.jena.acceptHeaderToJenaType
import no.brreg.organizationcatalogue.jena.jenaResponse
import no.brreg.organizationcatalogue.service.OrganizationCatalogueService
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.ConstraintViolationException

private val LOGGER = LoggerFactory.getLogger(OrganizationsApiImpl::class.java)

@Controller
open class OrganizationsApiImpl (
    private val catalogueService: OrganizationCatalogueService
): no.brreg.organizationcatalogue.generated.api.OrganizationsApi {

    @RequestMapping(value = ["/ping"], method = [GET], produces = ["text/plain"])
    fun ping(): ResponseEntity<String> =
        ResponseEntity.ok("pong")

    @RequestMapping(value = ["/ready"], method = [GET])
    fun ready(): ResponseEntity<Void> =
        ResponseEntity.ok().build()

    override fun updateOrganization(httpServletRequest: HttpServletRequest?, organizationId: String, organization: Organization): ResponseEntity<Organization> =
        try {
            catalogueService
                .updateEntry(organizationId, organization)
                ?.let { updated -> ResponseEntity(updated, HttpStatus.OK) }
                ?: ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (exception: Exception) {
            when(exception) {
                is ConstraintViolationException -> ResponseEntity(HttpStatus.BAD_REQUEST)
                is DuplicateKeyException -> ResponseEntity(HttpStatus.CONFLICT)
                else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }

    override fun getOrganizationById(httpServletRequest: HttpServletRequest, organizationId: String): ResponseEntity<Any> {
        val jenaType = acceptHeaderToJenaType(httpServletRequest.getHeader("Accept"))
        val organization = catalogueService.getByOrgnr(organizationId)

        return when {
            organization == null -> ResponseEntity(HttpStatus.NOT_FOUND)
            jenaType == JenaType.NOT_ACCEPTABLE -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            jenaType == JenaType.NOT_JENA -> ResponseEntity(organization, HttpStatus.OK)
            else -> ResponseEntity(organization.jenaResponse(jenaType), HttpStatus.OK)
        }
    }

    override fun getOrganizations(httpServletRequest: HttpServletRequest, name: String?, organizationId: String?): ResponseEntity<Any> {
        val jenaType = acceptHeaderToJenaType(httpServletRequest.getHeader("Accept"))
        val organizations = catalogueService.getOrganizations(name, organizationId)

        return when {
            jenaType == JenaType.NOT_ACCEPTABLE -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            jenaType == JenaType.NOT_JENA -> ResponseEntity(organizations, HttpStatus.OK)
            else -> ResponseEntity(organizations.jenaResponse(jenaType), HttpStatus.OK)
        }
    }
}
