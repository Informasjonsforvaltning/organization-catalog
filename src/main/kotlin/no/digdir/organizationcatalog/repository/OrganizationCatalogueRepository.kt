package no.digdir.organizationcatalog.repository

import no.digdir.organizationcatalog.model.OrganizationDB
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface OrganizationCatalogueRepository : MongoRepository<OrganizationDB, String> {
    fun findByNameLike(name: String): List<OrganizationDB>
    fun findByAllowDelegatedRegistration(allowed: Boolean): List<OrganizationDB>
}
