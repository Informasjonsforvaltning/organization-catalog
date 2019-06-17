package no.publishers.service

import no.publishers.generated.model.Publisher
import no.publishers.graphql.CreatePublisher
import no.publishers.graphql.PublisherQueryResolver
import no.publishers.mapping.mapToGenerated
import no.publishers.model.PublisherDB
import org.springframework.stereotype.Service

@Service
class PublisherService (
    private val publisherQueryResolver: PublisherQueryResolver
) {
    fun getById(id: String): Publisher? =
        publisherQueryResolver
            .getPublisherdb(id)
            ?.mapToGenerated()

    fun getByName(name: String): List<Publisher> =
        publisherQueryResolver
            .getPublisherdbByNameLike(name)
            .map { it.mapToGenerated() }

    fun createPublisher(publisher: CreatePublisher): PublisherDB =
        publisherQueryResolver
            .createPublisher(publisher)

}