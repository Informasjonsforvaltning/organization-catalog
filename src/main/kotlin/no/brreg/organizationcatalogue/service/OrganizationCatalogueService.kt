package no.brreg.organizationcatalogue.service

import no.brreg.organizationcatalogue.adapter.EnhetsregisteretAdapter
import no.brreg.organizationcatalogue.configuration.ProfileConditionalValues
import no.brreg.organizationcatalogue.generated.model.Organization
import no.brreg.organizationcatalogue.mapping.mapForCreation
import no.brreg.organizationcatalogue.mapping.mapToGenerated
import no.brreg.organizationcatalogue.mapping.updateValues
import no.brreg.organizationcatalogue.repository.OrganizationCatalogueRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class OrganizationCatalogueService (
    private val repository: OrganizationCatalogueRepository,
    private val enhetsregisteretAdapter: EnhetsregisteretAdapter,
    private val profileConditionalValues: ProfileConditionalValues
) {

    fun getByOrgnr(orgId: String): Organization? =
        repository
            .findByIdOrNull(orgId)
            ?.mapToGenerated(profileConditionalValues.enhetsregisteretUrl())
            ?: createFromEnhetsregisteret(orgId)

    fun getOrganizations(name: String?, organizationId: String?): List<Organization> =
        when {
            organizationId != null -> searchForOrganizationsByOrgId(organizationId)
            name != null -> searchForOrganizationsByName(name)
            else -> getCatalogue()
        }

    private fun getCatalogue() =
        repository
            .findAll()
            .map { it.mapToGenerated(profileConditionalValues.enhetsregisteretUrl()) }

    private fun searchForOrganizationsByOrgId(organizationId: String) =
        repository
            .findByOrganizationIdLike(organizationId)
            .map { it.mapToGenerated(profileConditionalValues.enhetsregisteretUrl()) }

    private fun searchForOrganizationsByName(name: String) =
        repository
            .findByNameLike(name)
            .map { it.mapToGenerated(profileConditionalValues.enhetsregisteretUrl()) }

    private fun createFromEnhetsregisteret(orgId: String): Organization? =
        enhetsregisteretAdapter
            .getOrganization(orgId)
            ?.mapForCreation()
            ?.let { repository.save(it) }
            ?.mapToGenerated(profileConditionalValues.enhetsregisteretUrl())

    fun updateEntry(orgId: String, org: Organization): Organization? =
        repository
            .findByIdOrNull(orgId)
            ?.updateValues(org)
            ?.let { repository.save(it) }
            ?.mapToGenerated(profileConditionalValues.enhetsregisteretUrl())

    fun getOrganizationsByIdList(idList: List<String>): List<Organization> =
        repository
            .findAllById(idList)
            .map { it.mapToGenerated(profileConditionalValues.enhetsregisteretUrl()) }
}