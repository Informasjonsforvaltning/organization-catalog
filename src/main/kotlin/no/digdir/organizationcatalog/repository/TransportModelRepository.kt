package no.digdir.organizationcatalog.repository

import no.digdir.organizationcatalog.model.TransportOrganizationDB
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TransportModelRepository : MongoRepository<TransportOrganizationDB, String> {

}
