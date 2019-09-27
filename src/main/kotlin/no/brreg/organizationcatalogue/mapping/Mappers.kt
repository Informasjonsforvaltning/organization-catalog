package no.brreg.organizationcatalogue.mapping

import no.brreg.organizationcatalogue.generated.model.PrefLabel
import no.brreg.organizationcatalogue.generated.model.Organization
import no.brreg.organizationcatalogue.model.OrganizationDB

fun OrganizationDB.mapToGenerated(): Organization {
    val mapped = Organization()

    mapped.id = id.toHexString()
    mapped.name = name
    mapped.uri = uri
    mapped.organizationId = organizationId
    mapped.orgType = orgType
    mapped.orgPath = orgPath
    mapped.subOrganizationOf = subOrganizationOf
    mapped.issued = issued
    mapped.municipalityNumber = municipalityNumber
    mapped.industryCode = industryCode
    mapped.sectorCode = sectorCode
    mapped.prefLabel = prefLabel

    return mapped
}

fun Organization.mapForCreation(): OrganizationDB {
    val mapped = OrganizationDB()

    mapped.name = name
    mapped.uri = uri
    mapped.organizationId = organizationId
    mapped.orgType = orgType
    mapped.orgPath = orgPath
    mapped.subOrganizationOf = subOrganizationOf
    mapped.municipalityNumber = municipalityNumber
    mapped.issued = issued
    mapped.industryCode = industryCode
    mapped.sectorCode = sectorCode
    mapped.prefLabel = prefLabel ?: PrefLabel()

    return mapped
}

fun OrganizationDB.updateValues(org: Organization): OrganizationDB =
    apply {
        name = org.name ?: name
        uri = org.uri ?: uri
        organizationId = org.organizationId ?: organizationId
        orgType = org.orgType ?: orgType
        orgPath = org.orgPath ?: orgPath
        subOrganizationOf = org.subOrganizationOf ?: subOrganizationOf
        municipalityNumber = org.municipalityNumber ?: municipalityNumber
        issued = org.issued ?: issued
        industryCode = org.industryCode ?: industryCode
        sectorCode = org.sectorCode ?: sectorCode
        prefLabel = prefLabel.update(org.prefLabel)
    }

private fun PrefLabel.update(newValues: PrefLabel?): PrefLabel {
    nb = newValues?.nb ?: nb
    nn = newValues?.nn ?: nn
    en = newValues?.en ?: en

    return this
}
