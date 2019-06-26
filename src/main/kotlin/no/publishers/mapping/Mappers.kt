package no.publishers.mapping

import no.publishers.generated.model.PrefLabel
import no.publishers.generated.model.Publisher
import no.publishers.model.PublisherDB

fun PublisherDB.mapToGenerated(): Publisher {
    val mapped = Publisher()

    mapped.id = id.toHexString()
    mapped.name = name
    mapped.orgPath = orgPath
    mapped.uri = uri
    mapped.organizationId = organizationId
    mapped.prefLabel = prefLabel

    return mapped
}

fun Publisher.mapForCreation(): PublisherDB {
    val mapped = PublisherDB()

    mapped.name = name
    mapped.orgPath = orgPath
    mapped.uri = uri
    mapped.organizationId = organizationId
    mapped.prefLabel = prefLabel ?: PrefLabel()

    return mapped
}