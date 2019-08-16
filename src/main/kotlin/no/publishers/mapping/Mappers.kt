package no.publishers.mapping

import no.publishers.generated.model.Code
import no.publishers.generated.model.PrefLabel
import no.publishers.generated.model.Publisher
import no.publishers.model.PublisherDB

fun PublisherDB.mapToGenerated(): Publisher {
    val mapped = Publisher()

    mapped.id = id.toHexString()
    mapped.name = name
    mapped.uri = uri
    mapped.organizationId = organizationId
    mapped.orgForm = orgForm
    mapped.orgPath = orgPath
    mapped.orgParent = orgParent
    mapped.municipalityNumber = municipalityNumber
    mapped.industryCode = industryCode
    mapped.sectorCode = sectorCode
    mapped.prefLabel = prefLabel

    return mapped
}

fun Publisher.mapForCreation(): PublisherDB {
    val mapped = PublisherDB()

    mapped.name = name
    mapped.uri = uri
    mapped.organizationId = organizationId
    mapped.orgForm = orgForm
    mapped.orgPath = orgPath
    mapped.orgParent = orgParent
    mapped.municipalityNumber = municipalityNumber
    mapped.industryCode = industryCode ?: Code().apply { prefLabel = PrefLabel() }
    mapped.sectorCode = sectorCode ?: Code().apply { prefLabel = PrefLabel() }
    mapped.prefLabel = prefLabel ?: PrefLabel()

    return mapped
}

fun PublisherDB.updateValues(publisher: Publisher): PublisherDB =
    apply {
        name = publisher.name ?: name
        uri = publisher.uri ?: uri
        organizationId = publisher.organizationId ?: organizationId
        orgForm = publisher.orgForm ?: orgForm
        orgPath = publisher.orgPath ?: orgPath
        orgParent = publisher.orgParent ?: orgParent
        municipalityNumber = publisher.municipalityNumber ?: municipalityNumber
        industryCode = industryCode.update(publisher.industryCode)
        sectorCode = sectorCode.update(publisher.sectorCode)
        prefLabel = prefLabel.update(publisher.prefLabel)
    }

private fun PrefLabel.update(newValues: PrefLabel?): PrefLabel {
    nb = newValues?.nb ?: nb
    nn = newValues?.nn ?: nn
    en = newValues?.en ?: en

    return this
}

private fun Code.update(newValues: Code?): Code {
    uri = newValues?.uri ?: uri
    code = newValues?.code ?: code
    prefLabel = prefLabel.update(newValues?.prefLabel)

    return this
}
