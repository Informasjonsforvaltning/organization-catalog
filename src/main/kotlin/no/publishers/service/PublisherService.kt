package no.publishers.service

import no.publishers.generated.model.Publisher
import no.publishers.mapping.mapForCreation
import no.publishers.mapping.mapToGenerated
import no.publishers.mapping.updateValues
import no.publishers.repository.PublisherRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PublisherService (
    private val publisherRepository: PublisherRepository
) {
    fun getById(id: String): Publisher? =
        publisherRepository
            .findByIdOrNull(id)
            ?.mapToGenerated()

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

    fun updatePublisher(id: String, publisher: Publisher): Publisher? =
        publisherRepository
            .findByIdOrNull(id)
            ?.updateValues(publisher)
            ?.let { publisherRepository.save(it) }
            ?.mapToGenerated()

    fun getOne(): Publisher? =
        publisherRepository
            .findAll()
            .first()
            ?.mapToGenerated()
}