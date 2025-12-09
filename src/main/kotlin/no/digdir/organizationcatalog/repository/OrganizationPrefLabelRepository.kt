package no.digdir.organizationcatalog.repository

import no.digdir.organizationcatalog.model.OrganizationPrefLabel
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface OrganizationPrefLabelRepository : MongoRepository<OrganizationPrefLabel, String>
