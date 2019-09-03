package no.orgcat.mapping

import no.orgcat.generated.model.PrefLabel
import no.orgcat.generated.model.Organization
import no.orgcat.model.OrganizationDB

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
    mapped.uriMunicipalityNumber = uriMunicipalityNumber
    mapped.uriIndustryCode = uriIndustryCode
    mapped.uriSectorCode = uriSectorCode
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
    mapped.uriMunicipalityNumber = uriMunicipalityNumber
    mapped.issued = issued
    mapped.uriIndustryCode = uriIndustryCode
    mapped.uriSectorCode = uriSectorCode
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
        uriMunicipalityNumber = org.uriMunicipalityNumber ?: uriMunicipalityNumber
        issued = org.issued ?: issued
        uriIndustryCode = org.uriIndustryCode ?: uriIndustryCode
        uriSectorCode = org.uriSectorCode ?: uriSectorCode
        prefLabel = prefLabel.update(org.prefLabel)
    }

private fun PrefLabel.update(newValues: PrefLabel?): PrefLabel {
    nb = newValues?.nb ?: nb
    nn = newValues?.nn ?: nn
    en = newValues?.en ?: en

    return this
}
