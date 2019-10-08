package no.brreg.organizationcatalogue.repository

import no.brreg.organizationcatalogue.model.OrganizationDB
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface OrganizationCatalogueRepository : MongoRepository<OrganizationDB, String> {
    fun findByNameLike(name: String): List<OrganizationDB>
    fun findByOrganizationIdLike(organizationId: String): List<OrganizationDB>
}