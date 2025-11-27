package no.digdir.organizationcatalog.mapping

import no.digdir.organizationcatalog.model.EnhetsregisteretOrganization
import no.digdir.organizationcatalog.model.OrgStatus
import no.digdir.organizationcatalog.model.Organization
import no.digdir.organizationcatalog.model.OrganizationDB
import no.digdir.organizationcatalog.model.PrefLabel
import no.digdir.organizationcatalog.model.TransportOrganization
import no.digdir.organizationcatalog.model.TransportOrganizationDB
import no.digdir.organizationcatalog.model.toDB
import no.digdir.organizationcatalog.utils.prefLabelFromName
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.text.isNullOrEmpty

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
        homepage = homepage,
        allowDelegatedRegistration = allowDelegatedRegistration,
        subordinate = subordinate,
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
        homepage = hjemmeside,
        orgStatus = orgStatusFromDeleteDate(),
        prefLabel = navn.prefLabelFromName(),
        subordinate = underenhet,
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
        homepage = org.homepage ?: homepage,
        prefLabel = prefLabel?.update(org.prefLabel) ?: PrefLabel().update(org.prefLabel),
        allowDelegatedRegistration = org.allowDelegatedRegistration ?: allowDelegatedRegistration,
        subordinate = org.subordinate,
    )

fun OrganizationDB.updateWithEnhetsregisteretValues(org: EnhetsregisteretOrganization, transportOrg: TransportOrganizationDB? = null): OrganizationDB {
    val prefLabelShouldBeUpdated =
        when {
            org.navn.isBlank() -> false
            prefLabel.isNullOrEmpty() -> true
            name != org.navn -> true
            else -> false
        }

    return copy(
        name = org.navn,
        orgType = org.organisasjonsform?.kode,
        orgPath = org.orgPath,
        subOrganizationOf = org.overordnetEnhet,
        municipalityNumber = org.forretningsadresse?.kommunenummer ?: org.postadresse?.kommunenummer,
        issued = org.registreringsdatoEnhetsregisteret?.let { LocalDate.parse(it) },
        industryCode = org.naeringskode1?.kode,
        sectorCode = org.institusjonellSektorkode?.kode,
        orgStatus = org.orgStatusFromDeleteDate(),
        homepage = org.hjemmeside,
        prefLabel = if (prefLabelShouldBeUpdated) transportOrg?.prefLabel ?: org.navn.prefLabelFromName() else prefLabel,
        subordinate = org.underenhet,
    )
}

fun TransportOrganization.updateOrCreateTransportData(
    existingData: TransportOrganizationDB,
): TransportOrganizationDB {
    val shouldUpdatePrefLabel: Boolean =
        when {
            tradingName.isNullOrEmpty() -> false
            existingData.prefLabel == null -> true
            existingData.prefLabel.nb.isNullOrEmpty() -> true
            existingData.prefLabel.nb != tradingName -> true
            else -> false
        }

    return this.toDB().copy(
        prefLabel = if (shouldUpdatePrefLabel) tradingName?.prefLabelFromName() else existingData.prefLabel
    )
}

private fun PrefLabel?.isNullOrEmpty(): Boolean =
    when {
        this == null -> true
        !en.isNullOrBlank() -> false
        !nb.isNullOrBlank() -> false
        !nn.isNullOrBlank() -> false
        else -> true
    }

private fun PrefLabel.update(newValues: PrefLabel?): PrefLabel =
    copy(
        nb = newValues?.nb ?: nb,
        nn = newValues?.nn ?: nn,
        en = newValues?.en ?: en,
    )

private fun EnhetsregisteretOrganization.orgStatusFromDeleteDate(): OrgStatus {
    val today = LocalDate.now()
    val deleteData: LocalDate? =
        slettedato?.let {
            LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }

    return when {
        deleteData == null -> OrgStatus.NORMAL
        deleteData.isAfter(today) -> OrgStatus.NORMAL
        else -> OrgStatus.LIQUIDATED
    }
}
