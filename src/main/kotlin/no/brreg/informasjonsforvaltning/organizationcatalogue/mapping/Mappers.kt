package no.brreg.informasjonsforvaltning.organizationcatalogue.mapping

import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.model.Domain
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.model.Organization
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.model.PrefLabel
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.DomainDB
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.EnhetsregisteretOrganization
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.OrganizationDB
import java.time.LocalDate

fun OrganizationDB.mapToGenerated(enhetsregisteretUrl: String): Organization {

    val mapped = Organization()

    mapped.name = name
    mapped.norwegianRegistry = enhetsregisteretUrl + organizationId
    mapped.internationalRegistry = internationalRegistry
    mapped.organizationId = organizationId
    mapped.orgType = orgType
    mapped.orgPath = orgPath
    mapped.subOrganizationOf = subOrganizationOf
    mapped.issued = issued
    mapped.municipalityNumber = municipalityNumber
    mapped.industryCode = industryCode
    mapped.sectorCode = sectorCode
    mapped.prefLabel = prefLabel
    mapped.domains = domains?.toList() ?: emptyList()

    return mapped
}

fun EnhetsregisteretOrganization.mapForCreation(): OrganizationDB {
    val mapped = OrganizationDB()

    mapped.name = navn
    mapped.organizationId = organisasjonsnummer
    mapped.orgType = organisasjonsform?.kode
    mapped.orgPath = orgPath
    mapped.subOrganizationOf = overordnetEnhet
    mapped.municipalityNumber = forretningsadresse?.kommunenummer ?: postadresse?.kommunenummer
    mapped.issued = registreringsdatoEnhetsregisteret?.let { LocalDate.parse(it) }
    mapped.industryCode = naeringskode1?.kode
    mapped.sectorCode = institusjonellSektorkode?.kode

    return mapped
}

fun OrganizationDB.updateValues(org: Organization): OrganizationDB =
    apply {
        name = org.name ?: name
        internationalRegistry = org.internationalRegistry ?: internationalRegistry
        orgType = org.orgType ?: orgType
        orgPath = org.orgPath ?: orgPath
        subOrganizationOf = org.subOrganizationOf ?: subOrganizationOf
        municipalityNumber = org.municipalityNumber ?: municipalityNumber
        issued = org.issued ?: issued
        industryCode = org.industryCode ?: industryCode
        sectorCode = org.sectorCode ?: sectorCode
        prefLabel = prefLabel?.update(org.prefLabel) ?: PrefLabel().update(org.prefLabel)
    }

private fun PrefLabel.update(newValues: PrefLabel?): PrefLabel {
    nb = newValues?.nb ?: nb
    nn = newValues?.nn ?: nn
    en = newValues?.en ?: en

    return this
}

fun DomainDB.mapToGenerated(): Domain {
    val mapped = Domain()

    mapped.name = name
    mapped.organizations = organizations.toList()

    return mapped
}
