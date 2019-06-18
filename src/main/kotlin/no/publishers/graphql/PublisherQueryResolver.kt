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

    fun getPublisherByNameLike(name: String) =
        publisherDao.getPublisherByName(name)

    fun createPublisher(input: CreatePublisher) =
        mutation.publisherCreate(input)
}
