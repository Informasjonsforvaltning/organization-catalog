package no.publishers.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import no.publishers.dao.PublisherDao
import org.springframework.stereotype.Component

@Component
class PublisherQueryResolver (
    private val publisherDao: PublisherDao,
    private val mutation: Mutation
) : GraphQLQueryResolver {

    fun getPublisher(id: String) =
        publisherDao.getPublisherById(id)

    fun getPublishers() =
        publisherDao.getPublishers()

    fun getPublishersByOrganizationIdLike(organizationId: String) =
        publisherDao.getPublishersByOrganizationId(organizationId)

    fun getPublishersByNameLike(name: String) =
        publisherDao.getPublishersByName(name)

    fun createPublisher(input: CreatePublisher) =
        mutation.publisherCreate(input)
}
