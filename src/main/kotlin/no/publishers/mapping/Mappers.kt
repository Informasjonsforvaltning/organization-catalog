package no.publishers.mapping

import no.publishers.generated.model.PrefLabel
import no.publishers.generated.model.Publisher
import no.publishers.graphql.CreatePublisher
import no.publishers.model.PublisherDB

fun PublisherDB.mapToGenerated(): Publisher {
    val mapped = Publisher()

    mapped.id = this.id.toHexString()
    mapped.name = this.name
    mapped.orgPath = this.orgPath
    mapped.uri = this.uri
    mapped.organizationId = this.organizationId

    val prefLabel = PrefLabel()
    prefLabel.nb = this.prefLabel
    mapped.prefLabel = prefLabel

    return mapped
}

fun CreatePublisher.mapForPersistence(): PublisherDB {
    val mapped = PublisherDB()

    mapped.name = this.name
    mapped.orgPath = this.orgPath
    mapped.uri = this.uri
    mapped.organizationId = this.organizationId
    mapped.prefLabel = this.prefLabel

    return mapped
}