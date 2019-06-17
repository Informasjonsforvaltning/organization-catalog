package no.publishers.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import no.publishers.dao.PublisherDao
import org.springframework.stereotype.Component

@Component
class PublisherQueryResolver (
    private val publisherDao: PublisherDao,
    private val mutation: Mutation
) : GraphQLQueryResolver {

    fun getPublisherdb(id: String) =
        publisherDao.getPublisherdbById(id)

    fun getPublisherdbByNameLike(name: String) =
        publisherDao.getPublisherdbByName(name)

    fun createPublisher(input: CreatePublisher) =
        mutation.publisherCreate(input)
}
