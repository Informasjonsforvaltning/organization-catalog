package no.orgcat.controller

import javax.servlet.http.HttpServletRequest
import no.orgcat.generated.model.Organization
import no.orgcat.jena.MissingAcceptHeaderException
import no.orgcat.jena.jenaResponse
import no.orgcat.service.OrganizationCatalogueService
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
): no.orgcat.generated.api.OrganizationsApi {

    @RequestMapping(value = ["/ping"], method = [GET], produces = ["text/plain"])
    fun ping(): ResponseEntity<String> =
        ResponseEntity.ok("pong")

    @RequestMapping(value = ["/ready"], method = [GET])
    fun ready(): ResponseEntity<Void> =
        ResponseEntity.ok().build()

    override fun createOrganization(httpServletRequest: HttpServletRequest, publisher: Organization): ResponseEntity<Void> =
        try {
            HttpHeaders()
                .apply {
                    location = ServletUriComponentsBuilder
                        .fromCurrentServletMapping()
                        .path("/publishers/{id}")
                        .build()
                        .expand(catalogueService.createEntry(publisher).id)
                        .toUri() }
                .let { ResponseEntity(it, HttpStatus.CREATED) }
        } catch (exception: Exception) {
            LOGGER.error("createPublisher failed:", exception)
            when(exception) {
                is ConstraintViolationException -> ResponseEntity(HttpStatus.BAD_REQUEST)
                is DuplicateKeyException -> ResponseEntity(HttpStatus.CONFLICT)
                else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }

    override fun updateOrganization(httpServletRequest: HttpServletRequest?, id: String, organization: Organization): ResponseEntity<Organization> =
        try {
            catalogueService
                .updateEntry(id, organization)
                ?.let { updated -> ResponseEntity(updated, HttpStatus.OK) }
                ?: ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (exception: Exception) {
            LOGGER.error("updatePublisher failed:", exception)
            when(exception) {
                is ConstraintViolationException -> ResponseEntity(HttpStatus.BAD_REQUEST)
                is DuplicateKeyException -> ResponseEntity(HttpStatus.CONFLICT)
                else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }

    override fun getOrganizationById(httpServletRequest: HttpServletRequest, id: String): ResponseEntity<String> =
        try {
            catalogueService
                .getById(id)
                ?.let { org -> org.jenaResponse(httpServletRequest.getHeader("Accept")) }
                ?.let { response -> ResponseEntity(response, HttpStatus.OK) }
                ?: ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (exception: Exception) {
            LOGGER.error("getPublisherById failed:", exception)
            when(exception) {
                is MissingAcceptHeaderException -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
                else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }

    override fun getOrganizations(httpServletRequest: HttpServletRequest, name: String?, organizationId: String?): ResponseEntity<String> =
        try {
            catalogueService
                .getOrganizations(name, organizationId)
                .let { orgs -> orgs.jenaResponse(httpServletRequest.getHeader("Accept")) }
                .let { response -> ResponseEntity(response, HttpStatus.OK) }
        } catch (exception: Exception) {
            LOGGER.error("getPublishers failed:", exception)
            when(exception) {
                is MissingAcceptHeaderException -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
                else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
}
