package no.brreg.informasjonsforvaltning.organizationcatalogue.mapping

import no.brreg.informasjonsforvaltning.organizationcatalogue.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        orgStatus = orgStatus,
        allowDelegatedRegistration = allowDelegatedRegistration,
    )

fun EnhetsregisteretOrganization.mapForCreation(): OrganizationDB =
    OrganizationDB(
        name = navn,
        organizationId = organisasjonsnummer,
        orgType = organisasjonsform?.kode,
        orgPath = orgPath,
        subOrganizationOf = overordnetEnhet,
        municipalityNumber = forretningsadresse?.kommunenummer ?: postadresse?.kommunenummer,
        issued = registreringsdatoEnhetsregisteret?.let { LocalDate.parse(it) },
        industryCode = naeringskode1?.kode,
        sectorCode = institusjonellSektorkode?.kode,
        orgStatus = orgStatusFromDeleteDate(),
        prefLabel = prefLabelFromName()
    )

fun OrganizationDB.updateValues(org: Organization): OrganizationDB =
    copy(
        name = org.name ?: name,
        internationalRegistry = org.internationalRegistry ?: internationalRegistry,
        orgType = org.orgType ?: orgType,
        orgPath = org.orgPath ?: orgPath,
        subOrganizationOf = org.subOrganizationOf ?: subOrganizationOf,
        municipalityNumber = org.municipalityNumber ?: municipalityNumber,
        issued = org.issued ?: issued,
        industryCode = org.industryCode ?: industryCode,
        sectorCode = org.sectorCode ?: sectorCode,
        prefLabel = prefLabel?.update(org.prefLabel) ?: PrefLabel().update(org.prefLabel),
        allowDelegatedRegistration = org.allowDelegatedRegistration ?: allowDelegatedRegistration
    )

fun OrganizationDB.updateWithEnhetsregisteretValues(org: EnhetsregisteretOrganization): OrganizationDB {
    val prefLabelShouldBeUpdated = when {
        org.navn.isNullOrBlank() -> false
        prefLabel.isNullOrEmpty() -> true
        name != org.navn -> true
        else -> false
    }

    return copy(
        name = org.navn ?: name,
        orgType = org.organisasjonsform?.kode,
        orgPath = org.orgPath,
        subOrganizationOf = org.overordnetEnhet,
        municipalityNumber = org.forretningsadresse?.kommunenummer ?: org.postadresse?.kommunenummer,
        issued = org.registreringsdatoEnhetsregisteret?.let { LocalDate.parse(it) },
        industryCode = org.naeringskode1?.kode,
        sectorCode = org.institusjonellSektorkode?.kode,
        orgStatus = org.orgStatusFromDeleteDate(),
        prefLabel = if (prefLabelShouldBeUpdated) org.prefLabelFromName() else prefLabel
    )
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

private fun EnhetsregisteretOrganization.orgStatusFromDeleteDate(): OrgStatus {
    val today = LocalDate.now()
    val deleteData: LocalDate? = slettedato?.let {
        LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    return when {
        deleteData == null -> OrgStatus.NORMAL
        deleteData.isAfter(today) -> OrgStatus.NORMAL
        else -> OrgStatus.LIQUIDATED
    }
}
