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
    fun getById(id: String): Organization? =
        repository
            .findByIdOrNull(id)
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

    fun updateEntry(id: String, org: Organization): Organization? =
        repository
            .findByIdOrNull(id)
            ?.updateValues(org)
            ?.let { repository.save(it) }
            ?.mapToGenerated()
}