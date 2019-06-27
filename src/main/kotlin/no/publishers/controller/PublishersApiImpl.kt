package no.publishers.controller

import io.swagger.annotations.ApiParam
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import no.publishers.generated.model.Publisher
import no.publishers.service.PublisherService
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.ConstraintViolationException

private val LOGGER = LoggerFactory.getLogger(PublishersApiImpl::class.java)

@Controller
open class PublishersApiImpl (
    private val publisherService: PublisherService
): no.publishers.generated.api.PublishersApi {

    @RequestMapping(value = ["/ping"], method = [GET], produces = ["text/plain"])
    fun ping(): ResponseEntity<String> =
        ResponseEntity.ok("pong")

    @RequestMapping(value = ["/ready"], method = [GET])
    fun ready(): ResponseEntity<Void> =
        ResponseEntity.ok().build()

    override fun createPublisher(
        httpServletRequest: HttpServletRequest,
        @ApiParam(required = true) @Valid @RequestBody publisher: Publisher
    ): ResponseEntity<Void> =
        try {
            HttpHeaders()
                .apply {
                    location = ServletUriComponentsBuilder
                        .fromCurrentServletMapping()
                        .path("/publishers/{id}")
                        .build()
                        .expand(publisherService.createPublisher(publisher).id)
                        .toUri() }
                .let { ResponseEntity(it, HttpStatus.CREATED) }
        } catch (exception: Exception) {
            LOGGER.error("createPublisher failed:", exception)
            when(exception) {
                is ConstraintViolationException -> ResponseEntity(HttpStatus.BAD_REQUEST)
                is DuplicateKeyException -> ResponseEntity(HttpStatus.CONFLICT)
                else  -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }

    override fun updatePublisher(
        httpServletRequest: HttpServletRequest,
        @ApiParam(value = "id", required = true) @PathVariable("id") id: String,
        @ApiParam(required = true) @Valid @RequestBody publisher: Publisher
    ): ResponseEntity<Publisher> =
        try {
            publisherService
                .updatePublisher(id, publisher)
                ?.let { updated -> ResponseEntity(updated, HttpStatus.OK) }
                ?: ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (exception: Exception) {
            LOGGER.error("updatePublisher failed:", exception)
            when(exception) {
                is ConstraintViolationException -> ResponseEntity(HttpStatus.BAD_REQUEST)
                is DuplicateKeyException -> ResponseEntity(HttpStatus.CONFLICT)
                else  -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }

    override fun getPublisherById(
        httpServletRequest: HttpServletRequest,
        @ApiParam(value = "id", required = true) @PathVariable("id") id: String
    ): ResponseEntity<Publisher> =
        try {
            publisherService
                .getById(id)
                ?.let { publisher -> ResponseEntity(publisher, HttpStatus.OK) }
                ?: ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (exception: Exception) {
            LOGGER.error("getPublisherById failed:", exception)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

    override fun getPublishers(
        httpServletRequest: HttpServletRequest,
        @ApiParam(value = "A query string to match a publisher name") @Valid @RequestParam(value = "name", required = false) name: String?,
        @ApiParam(value = "If you want to filter by organizationId") @Valid @RequestParam(value = "organizationId", required = false) organizationId: String?
    ): ResponseEntity<List<Publisher>> =
        try {
            publisherService
                .getPublishers(name, organizationId)
                .let { ResponseEntity(it, HttpStatus.OK) }
        } catch (exception: Exception) {
            LOGGER.error("getPublishers failed:", exception)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
}
