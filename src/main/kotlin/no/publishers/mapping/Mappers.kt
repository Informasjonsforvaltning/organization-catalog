package no.publishers.mapping

import no.publishers.generated.model.PrefLabel
import no.publishers.generated.model.Publisher
import no.publishers.model.PublisherDB

fun PublisherDB.mapToGenerated(): Publisher {
    val mapped = Publisher()

    mapped.id = id.toHexString()
    mapped.name = name
    mapped.uri = uri
    mapped.organizationId = organizationId
    mapped.orgType = orgType
    mapped.orgPath = orgPath
    mapped.subOrganizationOf = subOrganizationOf
    mapped.issued = issued
    mapped.uriMunicipalityNumber = uriMunicipalityNumber
    mapped.uriIndustryCode = uriIndustryCode
    mapped.uriSectorCode = uriSectorCode
    mapped.prefLabel = prefLabel

    return mapped
}

fun Publisher.mapForCreation(): PublisherDB {
    val mapped = PublisherDB()

    mapped.name = name
    mapped.uri = uri
    mapped.organizationId = organizationId
    mapped.orgType = orgType
    mapped.orgPath = orgPath
    mapped.subOrganizationOf = subOrganizationOf
    mapped.uriMunicipalityNumber = uriMunicipalityNumber
    mapped.issued = issued
    mapped.uriIndustryCode = uriIndustryCode
    mapped.uriSectorCode = uriSectorCode
    mapped.prefLabel = prefLabel ?: PrefLabel()

    return mapped
}

fun PublisherDB.updateValues(publisher: Publisher): PublisherDB =
    apply {
        name = publisher.name ?: name
        uri = publisher.uri ?: uri
        organizationId = publisher.organizationId ?: organizationId
        orgType = publisher.orgType ?: orgType
        orgPath = publisher.orgPath ?: orgPath
        subOrganizationOf = publisher.subOrganizationOf ?: subOrganizationOf
        uriMunicipalityNumber = publisher.uriMunicipalityNumber ?: uriMunicipalityNumber
        issued = publisher.issued ?: issued
        uriIndustryCode = uriIndustryCode ?: uriIndustryCode
        uriSectorCode = uriSectorCode ?: uriSectorCode
        prefLabel = prefLabel.update(publisher.prefLabel)
    }

private fun PrefLabel.update(newValues: PrefLabel?): PrefLabel {
    nb = newValues?.nb ?: nb
    nn = newValues?.nn ?: nn
    en = newValues?.en ?: en

    return this
}
