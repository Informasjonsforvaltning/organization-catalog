package no.publishers.dao

import no.publishers.model.PublisherDB
import no.publishers.repository.PublisherRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class PublisherDao(
    private val publisherRepository: PublisherRepository
) {
    fun getPublisherById(id: String): Optional<PublisherDB> =
        publisherRepository.findById(id)

    fun getPublishers() =
        publisherRepository.findAll()

    fun getPublishersByOrganizationId(organizationId: String) =
        publisherRepository.findByOrganizationIdLike(organizationId)

    fun getPublishersByName(name: String) =
        publisherRepository.findByNameLike(name)

    fun createPublisher(publisher: PublisherDB): PublisherDB {
        return publisherRepository.save(publisher)
    }
}