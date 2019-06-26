package no.publishers.service

import no.publishers.generated.model.Publisher
import no.publishers.mapping.mapForCreation
import no.publishers.mapping.mapToGenerated
import no.publishers.repository.PublisherRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PublisherService (
    private val publisherRepository: PublisherRepository
) {
    fun getById(id: String): Optional<Publisher> =
        publisherRepository
            .findById(id)
            .map { it.mapToGenerated() }

    fun getPublishers(name: String?, organizationId: String?): List<Publisher> =
        when {
            organizationId != null -> getPublishersByOrgId(organizationId)
            name != null -> getPublishersByName(name)
            else -> getAllPublishers()
        }

    private fun getAllPublishers() =
        publisherRepository
            .findAll()
            .map { it.mapToGenerated() }

    private fun getPublishersByOrgId(organizationId: String) =
        publisherRepository
            .findByOrganizationIdLike(organizationId)
            .map { it.mapToGenerated() }

    private fun getPublishersByName(name: String) =
        publisherRepository
            .findByNameLike(name)
            .map { it.mapToGenerated() }

    fun createPublisher(publisher: Publisher): Publisher =
        publisherRepository
            .save(publisher.mapForCreation())
            .mapToGenerated()

}