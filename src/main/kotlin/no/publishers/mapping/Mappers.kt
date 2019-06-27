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

fun PublisherDB.updateValues(publisher: Publisher): PublisherDB =
    apply {
        name = publisher.name ?: name
        orgPath = publisher.orgPath ?: orgPath
        uri = publisher.uri ?: uri
        organizationId = publisher.organizationId ?: organizationId
        prefLabel.nb = publisher.prefLabel?.nb ?: prefLabel.nb
        prefLabel.nn = publisher.prefLabel?.nn ?: prefLabel.nn
        prefLabel.en = publisher.prefLabel?.en ?: prefLabel.en
    }
