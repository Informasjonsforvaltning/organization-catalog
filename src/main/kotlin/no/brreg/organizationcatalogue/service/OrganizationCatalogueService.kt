package no.brreg.organizationcatalogue.service

import no.brreg.organizationcatalogue.generated.model.Organization
import no.brreg.organizationcatalogue.mapping.mapForCreation
import no.brreg.organizationcatalogue.mapping.mapToGenerated
import no.brreg.organizationcatalogue.mapping.updateValues
import no.brreg.organizationcatalogue.repository.OrganizationCatalogueRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class OrganizationCatalogueService (
    private val repository: OrganizationCatalogueRepository
) {

    fun getByOrgnr(orgId: String): Organization? =
        repository
            .findByOrganizationId(orgId)
            ?.mapToGenerated()

    fun getOrganizations(name: String?, organizationId: String?): List<Organization> =
        when {
            organizationId != null -> getOrganizationsByOrgId(organizationId)
            name != null -> getOrganizationsByName(name)
            else -> getCatalogue()
        }

    private fun getCatalogue() =
        repository
            .findAll()
            .map { it.mapToGenerated() }

    private fun getOrganizationsByOrgId(organizationId: String) =
        repository
            .findByOrganizationIdLike(organizationId)
            .map { it.mapToGenerated() }

    private fun getOrganizationsByName(name: String) =
        repository
            .findByNameLike(name)
            .map { it.mapToGenerated() }

    fun createEntry(org: Organization): Organization =
        repository
            .save(org.mapForCreation())
            .mapToGenerated()

    fun updateEntry(orgId: String, org: Organization): Organization? =
        repository
            .findByOrganizationId(orgId)
            ?.updateValues(org)
            ?.let { repository.save(it) }
            ?.mapToGenerated()
}