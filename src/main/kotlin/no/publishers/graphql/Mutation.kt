package no.publishers.graphql

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import no.publishers.dao.PublisherDao
import no.publishers.mapping.mapForPersistence
import org.springframework.stereotype.Component

@Component
class Mutation(
    private val publisherDao: PublisherDao
): GraphQLMutationResolver {
    fun publisherCreate(input: CreatePublisher) =
        publisherDao.createPublisher(input.mapForPersistence())
}