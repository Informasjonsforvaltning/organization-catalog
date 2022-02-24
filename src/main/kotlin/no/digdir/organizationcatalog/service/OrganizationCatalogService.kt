package no.digdir.organizationcatalog.service

import no.digdir.organizationcatalog.adapter.EnhetsregisteretAdapter
import no.digdir.organizationcatalog.configuration.AppProperties
import no.digdir.organizationcatalog.model.Organization
import no.digdir.organizationcatalog.mapping.mapForCreation
import no.digdir.organizationcatalog.mapping.mapToGenerated
import no.digdir.organizationcatalog.mapping.updateValues
import no.digdir.organizationcatalog.mapping.updateWithEnhetsregisteretValues
import no.digdir.organizationcatalog.repository.OrganizationCatalogRepository
import no.digdir.organizationcatalog.utils.isOrganizationNumber
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class OrganizationCatalogService(
    private val repository: OrganizationCatalogRepository,
    private val enhetsregisteretAdapter: EnhetsregisteretAdapter,
    private val appProperties: AppProperties
) {

    fun getByOrgnr(orgId: String): Organization? =
        repository
            .findByIdOrNull(orgId)
            ?.mapToGenerated(appProperties.enhetsregisteretUrl)
            ?: updateEntryFromEnhetsregisteret(orgId)

    fun getOrganizations(name: String?, orgs: List<String>?): List<Organization> =
        when {
            name != null && orgs != null  -> searchForOrganizationsByNameAndIds(name, orgs)
            orgs != null -> searchForOrganizationsByIds(orgs)
            name != null -> searchForOrganizationsByName(name)
            else -> getCatalog()
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
            .findByNameLike(name.toUpperCase())
            .map { it.mapToGenerated(appProperties.enhetsregisteretUrl) }

    private fun searchForOrganizationsByNameAndIds(name: String, orgs: List<String>) =
        repository
            .findByNameLike(name)
            .filter { orgs.contains(it.organizationId) }
            .map { it.mapToGenerated(appProperties.enhetsregisteretUrl) }

    fun updateEntryFromEnhetsregisteret(orgId: String): Organization? {
        enhetsregisteretAdapter.getOrganizationAndParents(orgId)
            .map { updated ->
                repository.findByIdOrNull(updated.organisasjonsnummer)
                    ?.updateWithEnhetsregisteretValues(updated)
                    ?: updated.mapForCreation() }
            .run { repository.saveAll(this) }

        return repository.findByIdOrNull(orgId)
            ?.mapToGenerated(appProperties.enhetsregisteretUrl)
    }

    fun updateEntry(orgId: String, org: Organization): Organization? =
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
        } else "${appProperties.defaultOrgPath}$orgId"

    @Scheduled(cron = "0 30 20 5 * ?")
    fun updateAllEntriesFromEnhetsregisteret() {
        repository.findAll()
            .forEach { updateEntryFromEnhetsregisteret(it.organizationId) }
    }

}
