package no.publishers.service

import no.publishers.generated.model.Publisher
import no.publishers.graphql.CreatePublisher
import no.publishers.graphql.PublisherQueryResolver
import no.publishers.mapping.mapToGenerated
import no.publishers.model.PublisherDB
import org.springframework.stereotype.Service
import java.util.*

@Service
class PublisherService (
    private val publisherQueryResolver: PublisherQueryResolver
) {
    fun getById(id: String): Optional<Publisher> =
        publisherQueryResolver
            .getPublisher(id)
            .map { it.mapToGenerated() }

    fun getPublishers(name: String?, organizationId: String?): List<Publisher> =
        when {
            organizationId != null -> getPublishersByOrgId(organizationId)
            name != null -> getPublishersByName(name)
            else -> getAllPublishers()
        }

    private fun getAllPublishers() =
        publisherQueryResolver
            .getPublishers()
            .map { it.mapToGenerated() }

    private fun getPublishersByOrgId(organizationId: String) =
        publisherQueryResolver
            .getPublishersByOrganizationIdLike(organizationId)
            .map { it.mapToGenerated() }

    private fun getPublishersByName(name: String) =
        publisherQueryResolver
            .getPublishersByNameLike(name)
            .map { it.mapToGenerated() }

    fun createPublisher(publisher: CreatePublisher): String =
        publisherQueryResolver
            .createPublisher(publisher)
            .id
            .toHexString()

}