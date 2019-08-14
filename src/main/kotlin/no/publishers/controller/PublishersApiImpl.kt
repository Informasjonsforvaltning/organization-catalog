package no.publishers.controller

import javax.servlet.http.HttpServletRequest
import no.publishers.generated.model.Publisher
import no.publishers.jena.createModel
import no.publishers.jena.createListModel
import no.publishers.jena.createResponseString
import no.publishers.service.PublisherService
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

/*
    override fun testJena(
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<String> {
        val publishers = publisherService.getOne()

        val publisherModel = publishers.createModelList()

        return when(httpServletRequest.getHeader("Accept")){
            "text/turtle" -> ResponseEntity(publisherModel.createResponseString("TURTLE"), HttpStatus.OK)
            "application/rdf+xml" -> ResponseEntity(publisherModel.createResponseString("RDF/XML"), HttpStatus.OK)
            else -> ResponseEntity(publisherModel.createResponseString("JSON-LD"), HttpStatus.OK)
        }
    }
*/

    override fun createPublisher(httpServletRequest: HttpServletRequest, publisher: Publisher): ResponseEntity<Void> =
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
                else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }

    override fun updatePublisher(httpServletRequest: HttpServletRequest, id: String, publisher: Publisher): ResponseEntity<Publisher> =
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
                else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }

    override fun getPublisherById(httpServletRequest: HttpServletRequest, id: String): ResponseEntity<String> =
        httpServletRequest
            .getHeader("Accept")
            .acceptHeaderToJenaType()
            ?.let { jenaType ->
                try {
                    publisherService
                        .getById(id)
                        ?.let { publisher -> publisher.createModel() }
                        ?.let { model -> model.createResponseString(jenaType) }
                        ?.let { response -> ResponseEntity(response, HttpStatus.OK) }
                        ?: ResponseEntity(HttpStatus.NOT_FOUND)
                } catch (exception: Exception) {
                    LOGGER.error("getPublisherById failed:", exception)
                    ResponseEntity("", HttpStatus.INTERNAL_SERVER_ERROR)
                } }
            ?: ResponseEntity(HttpStatus.NOT_ACCEPTABLE)

    override fun getPublishers(httpServletRequest: HttpServletRequest, name: String?, organizationId: String?): ResponseEntity<String> =
        httpServletRequest
            .getHeader("Accept")
            .acceptHeaderToJenaType()
            ?.let { jenaType ->
                try {
                    publisherService
                        .getPublishers(name, organizationId)
                        .let { publishers -> publishers.createListModel() }
                        .let { model -> model.createResponseString(jenaType) }
                        .let { response -> ResponseEntity(response, HttpStatus.OK) }
                } catch (exception: Exception) {
                    LOGGER.error("getPublishers failed:", exception)
                    ResponseEntity("", HttpStatus.INTERNAL_SERVER_ERROR)
                } }
            ?: ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
}


private fun String.acceptHeaderToJenaType(): String? =
    when (this) {
        "text/turtle" -> "TURTLE"
        "application/rdf+xml" -> "RDF/XML"
        "application/ld+json" -> "JSON-LD"
        else -> null
    }