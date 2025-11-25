package no.digdir.organizationcatalog.repository

import no.digdir.organizationcatalog.model.TransportModelDB
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TransportModelRepository : MongoRepository<TransportModelDB, String> {

}
