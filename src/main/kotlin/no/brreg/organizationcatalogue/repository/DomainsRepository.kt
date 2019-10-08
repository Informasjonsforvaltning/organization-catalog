package no.brreg.organizationcatalogue.repository

import no.brreg.organizationcatalogue.model.DomainDB
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface DomainsRepository : MongoRepository<DomainDB, String>