package no.publishers.dao

import no.publishers.model.PublisherDB
import no.publishers.repository.PublisherRepository
import org.springframework.stereotype.Component

@Component
class PublisherDao(
    private val publisherRepository: PublisherRepository
) {
    fun getPublisherdbById(id: String): PublisherDB? =
        publisherRepository.findById(id).orElse(null)

    fun getPublisherdbByName(name: String) =
        publisherRepository.findByNameLike(name)

    fun createPublisher(publisher: PublisherDB): PublisherDB {
        return publisherRepository.save(publisher)
    }
}