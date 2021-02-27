package no.brreg.informasjonsforvaltning.organizationcatalogue.mapping

import no.brreg.informasjonsforvaltning.organizationcatalogue.model.Organization
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.PrefLabel
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.EnhetsregisteretOrganization
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.OrganizationDB
import java.time.LocalDate

fun OrganizationDB.mapToGenerated(enhetsregisteretUrl: String): Organization =
    Organization(
        name = name,
        norwegianRegistry = enhetsregisteretUrl + organizationId,
        internationalRegistry = internationalRegistry,
        organizationId = organizationId,
        orgType = orgType,
        orgPath = orgPath,
        subOrganizationOf = subOrganizationOf,
        issued = issued,
        municipalityNumber = municipalityNumber,
        industryCode = industryCode,
        sectorCode = sectorCode,
        prefLabel = prefLabel,
        allowDelegatedRegistration = allowDelegatedRegistration,
    )

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
    mapped.prefLabel = prefLabelFromName()

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
        allowDelegatedRegistration = org.allowDelegatedRegistration ?: allowDelegatedRegistration
    }

fun OrganizationDB.updateWithEnhetsregisteretValues(org: EnhetsregisteretOrganization): OrganizationDB {
    val prefLabelShouldBeUpdated = when {
        org.navn.isNullOrBlank() -> false
        prefLabel.isNullOrEmpty() -> true
        name != org.navn -> true
        else -> false
    }

    return apply {
        name = org.navn ?: name
        orgType = org.organisasjonsform?.kode
        orgPath = org.orgPath
        subOrganizationOf = org.overordnetEnhet
        municipalityNumber = org.forretningsadresse?.kommunenummer ?: org.postadresse?.kommunenummer
        issued = org.registreringsdatoEnhetsregisteret?.let { LocalDate.parse(it) }
        industryCode = org.naeringskode1?.kode
        sectorCode = org.institusjonellSektorkode?.kode
        prefLabel = if (prefLabelShouldBeUpdated) org.prefLabelFromName() else prefLabel
    }
}

private fun EnhetsregisteretOrganization.prefLabelFromName(): PrefLabel =
    PrefLabel(nb = navn?.toLowerCase()?.capitalize())

private fun PrefLabel?.isNullOrEmpty(): Boolean =
    when {
        this == null -> true
        en != null && en.isNotBlank() -> false
        nb != null && nb.isNotBlank() -> false
        nn != null && nn.isNotBlank() -> false
        else -> true
    }

private fun PrefLabel.update(newValues: PrefLabel?): PrefLabel =
    copy(
        nb = newValues?.nb ?: nb,
        nn = newValues?.nn ?: nn,
        en = newValues?.en ?: en
    )
