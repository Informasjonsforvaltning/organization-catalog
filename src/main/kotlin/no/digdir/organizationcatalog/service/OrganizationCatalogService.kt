package no.digdir.organizationcatalog.service

import no.digdir.organizationcatalog.adapter.EnhetsregisteretAdapter
import no.digdir.organizationcatalog.adapter.TransportOrganizationAdapter
import no.digdir.organizationcatalog.configuration.AppProperties
import no.digdir.organizationcatalog.mapping.getOrgPathBase
import no.digdir.organizationcatalog.mapping.mapForCreation
import no.digdir.organizationcatalog.mapping.mapToGenerated
import no.digdir.organizationcatalog.mapping.updateValues
import no.digdir.organizationcatalog.mapping.updateWithEnhetsregisteretValues
import no.digdir.organizationcatalog.mapping.updateOrCreateTransportData

import no.digdir.organizationcatalog.model.EnhetsregisteretOrganization
import no.digdir.organizationcatalog.model.EnhetsregisteretType
import no.digdir.organizationcatalog.model.Organization
import no.digdir.organizationcatalog.model.OrganizationDB
import no.digdir.organizationcatalog.model.TransportOrganization
import no.digdir.organizationcatalog.model.TransportOrganizationDB
import no.digdir.organizationcatalog.repository.OrganizationCatalogRepository
import no.digdir.organizationcatalog.repository.TransportDataRepository
import no.digdir.organizationcatalog.utils.isOrganizationNumber
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.Locale

private val LOGGER = LoggerFactory.getLogger(OrganizationCatalogService::class.java)

@Service
class OrganizationCatalogService(
    private val repository: OrganizationCatalogRepository,
    private val transportDataRepository: TransportDataRepository,
    private val enhetsregisteretAdapter: EnhetsregisteretAdapter,
    private val transportOrganizationAdapter: TransportOrganizationAdapter,
    private val appProperties: AppProperties,
) {
    fun getByOrgnr(orgId: String): Organization? =
        repository
            .findByIdOrNull(orgId)
            ?.mapToGenerated(appProperties.enhetsregisteretUrl)
            ?: updateEntryFromEnhetsregisteret(orgId)

    fun getOrganizations(
        name: String?,
        orgIds: List<String>?,
        orgPath: String?,
        includeSubordinate: Boolean,
    ): List<Organization> {
        val organizations =
            when {
                name != null && orgIds != null -> searchForOrganizationsByNameAndIds(name, orgIds)
                orgIds != null -> searchForOrganizationsByIds(orgIds)
                name != null -> searchForOrganizationsByName(name)
                else -> getCatalog()
            }

        return organizations
            .filter { if (orgPath != null) it.orgPath?.startsWith(orgPath) ?: false else true }
            .filter { if (includeSubordinate) true else !it.subordinate }
    }

    fun getOrganizationsWithDelegationPermissions(): List<Organization> =
        repository
            .findByAllowDelegatedRegistration(true)
            .map { it.mapToGenerated(appProperties.enhetsregisteretUrl) }

    private fun getCatalog() =
        repository
            .findAll()
            .map { it.mapToGenerated(appProperties.enhetsregisteretUrl) }

    private fun searchForOrganizationsByIds(orgs: List<String>) =
        repository
            .findAllById(orgs)
            .map { it.mapToGenerated(appProperties.enhetsregisteretUrl) }

    private fun searchForOrganizationsByName(name: String) =
        repository
            .findByNameLike(name.uppercase(Locale.getDefault()))
            .map { it.mapToGenerated(appProperties.enhetsregisteretUrl) }

    private fun searchForOrganizationsByNameAndIds(
        name: String,
        orgs: List<String>,
    ) = repository
        .findByNameLike(name)
        .filter { orgs.contains(it.organizationId) }
        .map { it.mapToGenerated(appProperties.enhetsregisteretUrl) }

    fun updateEntryFromEnhetsregisteret(orgId: String): Organization? {
        enhetsregisteretAdapter
            .getOrganizationAndParents(orgId)
            .map { it.updateExistingOrMapForCreation() }
            .run { repository.saveAll(this) }

        return repository
            .findByIdOrNull(orgId)
            ?.mapToGenerated(appProperties.enhetsregisteretUrl)
    }

    private fun EnhetsregisteretOrganization.updateExistingOrMapForCreation(): OrganizationDB {
        val transportOrganization: TransportOrganizationDB? =
            transportDataRepository.findByIdOrNull(organisasjonsnummer)
        return repository
            .findByIdOrNull(organisasjonsnummer)
            ?.updateWithEnhetsregisteretValues(this, transportOrganization)
            ?: mapForCreation()
    }

    fun updateEntry(
        orgId: String,
        org: Organization,
    ): Organization? =
        repository
            .findByIdOrNull(orgId)
            ?.updateValues(org)
            ?.let { repository.save(it) }
            ?.mapToGenerated(appProperties.enhetsregisteretUrl)

    fun getOrgPath(orgId: String): String =
        if (orgId.isOrganizationNumber()) {
            getByOrgnr(orgId)
                ?.orgPath
                ?: "${appProperties.defaultOrgPath}$orgId"
        } else {
            "${appProperties.defaultOrgPath}$orgId"
        }

    private fun EnhetsregisteretOrganization.addOrgPath(): EnhetsregisteretOrganization {
        val orgPathBase =
            if (overordnetEnhet == null) {
                getOrgPathBase(organisasjonsform?.kode)
            } else {
                getByOrgnr(overordnetEnhet)?.orgPath
            }

        return copy(orgPath = "$orgPathBase/$organisasjonsnummer")
    }

    //TODO delete after testing
    fun getTransportDataList() = transportOrganizationAdapter.downloadTransportDataList()

    //TODO delete after testing
    fun getTransportDataRaw() = transportOrganizationAdapter.downloadTransportData()

    @Scheduled(cron = "0 30 18 5 * ?")
    fun updateTransportData(): Unit =
        transportOrganizationAdapter.downloadTransportDataList()
            .forEach { updateTransportData(it) }

    fun updateTransportData(transportOrganization: TransportOrganization): TransportOrganizationDB? =
        transportOrganization.companyNumber?.let {
            val transportOrganizationDB = transportDataRepository.findByIdOrNull(it)
            transportOrganization.updateOrCreateTransportData(
                transportOrganizationDB ?: TransportOrganizationDB(organizationId = it)
            )
        }?.run {
                transportDataRepository.save(this)
            }


    @Scheduled(cron = "0 30 20 5 * ?")
    fun updateAllEntriesFromEnhetsregisteret() {
        repository
            .findAll()
            .forEach { updateEntryFromEnhetsregisteret(it.organizationId) }
    }

    @Scheduled(cron = "0 30 2 * * SUN")
    fun updateSTAT() {
        LOGGER.debug("updating STAT organizations from Enhetsregisteret")
        val stateOrgs =
            enhetsregisteretAdapter
                .getOrganizationsFromEnhetsregisteretByType(EnhetsregisteretType.STAT)
                .map { it.addOrgPath() }
                .map { it.updateExistingOrMapForCreation() }

        stateOrgs.run { repository.saveAll(this) }

        stateOrgs
            .asSequence()
            .map { it.organizationId }
            .map { enhetsregisteretAdapter.getOrganizationsFromEnhetsregisteretByParent(it) }
            .flatten()
            .map { it.addOrgPath() }
            .map { it.updateExistingOrMapForCreation() }
            .toList()
            .run { repository.saveAll(this) }
    }

    @Scheduled(cron = "0 30 3 * * SUN")
    fun updateFYLK() {
        LOGGER.debug("updating FYLK organizations from Enhetsregisteret")
        enhetsregisteretAdapter
            .getOrganizationsFromEnhetsregisteretByType(EnhetsregisteretType.FYLK)
            .map { it.addOrgPath() }
            .map { it.updateExistingOrMapForCreation() }
            .run { repository.saveAll(this) }
    }

    @Scheduled(cron = "0 30 4 * * SUN")
    fun updateKOMM() {
        LOGGER.debug("updating KOMM organizations from Enhetsregisteret")
        enhetsregisteretAdapter
            .getOrganizationsFromEnhetsregisteretByType(EnhetsregisteretType.KOMM)
            .map { it.addOrgPath() }
            .map { it.updateExistingOrMapForCreation() }
            .run { repository.saveAll(this) }
    }
}
