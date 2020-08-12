package no.brreg.informasjonsforvaltning.organizationcatalogue.service

import no.brreg.informasjonsforvaltning.organizationcatalogue.adapter.EnhetsregisteretAdapter
import no.brreg.informasjonsforvaltning.organizationcatalogue.configuration.AppProperties
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.model.Organization
import no.brreg.informasjonsforvaltning.organizationcatalogue.mapping.mapForCreation
import no.brreg.informasjonsforvaltning.organizationcatalogue.mapping.mapToGenerated
import no.brreg.informasjonsforvaltning.organizationcatalogue.mapping.updateValues
import no.brreg.informasjonsforvaltning.organizationcatalogue.repository.OrganizationCatalogueRepository
import org.springframework.data.repository.findByIdOrNull
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
            ?: createFromEnhetsregisteret(orgId)

    fun getOrganizations(name: String?, organizationId: String?): List<Organization> =
        when {
            name != null && organizationId != null  -> searchForOrganizationsByNameAndOrgId(name, organizationId)
            organizationId != null -> searchForOrganizationsByOrgId(organizationId)
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

    private fun searchForOrganizationsByOrgId(organizationId: String) =
        repository
            .findByOrganizationIdLike(organizationId)
            .map { it.mapToGenerated(appProperties.enhetsregisteretUrl) }

    private fun searchForOrganizationsByName(name: String) =
        repository
            .findByNameLike(name)
            .map { it.mapToGenerated(appProperties.enhetsregisteretUrl) }

    private fun searchForOrganizationsByNameAndOrgId(name: String, organizationId: String) =
        repository
            .findByNameLikeAndOrganizationIdLike(name, organizationId)
            .map { it.mapToGenerated(appProperties.enhetsregisteretUrl) }

    private fun createFromEnhetsregisteret(orgId: String): Organization? {
        enhetsregisteretAdapter.getOrganizationAndParents(orgId)
            .map { it.mapForCreation() }
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

    fun getOrganizationsByIdList(idList: List<String>): List<Organization> =
        repository
            .findAllById(idList)
            .map { it.mapToGenerated(appProperties.enhetsregisteretUrl) }

    fun getOrgPath(orgId: String): String =
        repository
            .findByIdOrNull(orgId)
            ?.orgPath
            ?: "${appProperties.defaultOrgPath}$orgId"
}
