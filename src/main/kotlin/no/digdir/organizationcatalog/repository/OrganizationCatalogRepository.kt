package no.digdir.organizationcatalog.repository

import no.digdir.organizationcatalog.model.OrganizationDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrganizationCatalogRepository : JpaRepository<OrganizationDB, String> {
    fun findByNameContainingIgnoreCase(name: String): List<OrganizationDB>

    fun findByAllowDelegatedRegistration(allowed: Boolean): List<OrganizationDB>
}
