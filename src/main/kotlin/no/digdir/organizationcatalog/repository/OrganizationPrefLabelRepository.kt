package no.digdir.organizationcatalog.repository

import no.digdir.organizationcatalog.model.OrganizationPrefLabel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrganizationPrefLabelRepository : JpaRepository<OrganizationPrefLabel, String>
