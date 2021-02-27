package no.brreg.informasjonsforvaltning.organizationcatalogue.service

import no.brreg.informasjonsforvaltning.organizationcatalogue.adapter.EnhetsregisteretAdapter
import no.brreg.informasjonsforvaltning.organizationcatalogue.configuration.AppProperties
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.Organization
import no.brreg.informasjonsforvaltning.organizationcatalogue.mapping.mapForCreation
import no.brreg.informasjonsforvaltning.organizationcatalogue.mapping.mapToGenerated
import no.brreg.informasjonsforvaltning.organizationcatalogue.mapping.updateValues
import no.brreg.informasjonsforvaltning.organizationcatalogue.mapping.updateWithEnhetsregisteretValues
import no.brreg.informasjonsforvaltning.organizationcatalogue.repository.OrganizationCatalogueRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class OrganizationCatalogueService(
    private val repository: OrganizationCatalogueRepository,
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
            else -> getCatalogue()
        }

    fun getOrganizationsWithDelegationPermissions(): List<Organization> =
        repository
            .findByAllowDelegatedRegistration(true)
            .map { it.mapToGenerated(appProperties.enhetsregisteretUrl) }

    private fun getCatalogue() =
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
        repository
            .findByIdOrNull(orgId)
            ?.orgPath
            ?: "${appProperties.defaultOrgPath}$orgId"

    @Scheduled(cron = "0 30 20 5 * ?")
    fun updateAllEntriesFromEnhetsregisteret() {
        repository.findAll()
            .forEach { updateEntryFromEnhetsregisteret(it.organizationId) }
    }

}
