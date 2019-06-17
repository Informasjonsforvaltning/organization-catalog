package no.publishers.repository

import no.publishers.model.PublisherDB
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PublisherRepository : MongoRepository<PublisherDB, String> {
    fun findByNameLike(name: String): List<PublisherDB>
}