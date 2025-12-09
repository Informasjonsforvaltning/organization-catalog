package no.digdir.organizationcatalog.utils

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import no.digdir.organizationcatalog.utils.ApiTestContext.Companion.mongoContainer
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import org.springframework.http.HttpStatus
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URI

fun apiGet(
    endpoint: String,
    port: Int,
    acceptHeader: String?,
    otherHeaders: List<Pair<String, String>> = emptyList(),
): Map<String, Any> =
    try {
        val connection = URI(getApiAddress(port, endpoint)).toURL().openConnection() as HttpURLConnection
        acceptHeader?.let { connection.addRequestProperty("Accept", it) }
        otherHeaders.forEach { connection.addRequestProperty(it.first, it.second) }
        connection.connect()

        if (isOK(connection.responseCode)) {
            val responseBody = connection.inputStream.bufferedReader().use(BufferedReader::readText)
            mapOf(
                "body" to responseBody,
                "header" to connection.headerFields.toString(),
                "status" to connection.responseCode,
            )
        } else {
            mapOf(
                "status" to connection.responseCode,
                "header" to " ",
                "body" to " ",
            )
        }
    } catch (e: Exception) {
        mapOf(
            "status" to e.toString(),
            "header" to " ",
            "body" to " ",
        )
    }

fun apiAuthorizedRequest(
    endpoint: String,
    port: Int,
    body: String?,
    token: String?,
    method: String,
): Map<String, Any> =
    try {
        val connection = URI("http://localhost:$port$endpoint").toURL().openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.setRequestProperty("Content-type", "application/json")
        connection.setRequestProperty("Accept", "application/json")
        if (!token.isNullOrEmpty()) connection.setRequestProperty("Authorization", "Bearer $token")

        connection.doOutput = true
        connection.connect()

        if (body != null) {
            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(body)
            writer.close()
        }

        if (isOK(connection.responseCode)) {
            val responseBody = connection.inputStream.bufferedReader().use(BufferedReader::readText)
            mapOf(
                "body" to responseBody,
                "header" to connection.headerFields.toString(),
                "status" to connection.responseCode,
            )
        } else {
            mapOf(
                "status" to connection.responseCode,
                "header" to " ",
                "body" to " ",
            )
        }
    } catch (e: Exception) {
        mapOf(
            "status" to e.toString(),
            "header" to " ",
            "body" to " ",
        )
    }

private fun isOK(response: Int?): Boolean =
    if (response == null) {
        false
    } else {
        HttpStatus.resolve(response)?.is2xxSuccessful == true
    }

fun resetDB() {
    val connectionString =
        ConnectionString(
            "mongodb://${MONGO_USER}:${MONGO_PASSWORD}@localhost:${mongoContainer.getMappedPort(
                MONGO_PORT,
            )}/organization-catalog?authSource=admin&authMechanism=SCRAM-SHA-1",
        )
    val pojoCodecRegistry =
        fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()))

    val client: MongoClient = MongoClients.create(connectionString)
    val mongoDatabase = client.getDatabase("organizationCatalog").withCodecRegistry(pojoCodecRegistry)

    val orgCollection = mongoDatabase.getCollection("organizations")
    orgCollection.deleteMany(org.bson.Document())
    orgCollection.insertMany(organizationsDBPopulation())

    val prefLabelCollection = mongoDatabase.getCollection("orgPrefLabel")
    prefLabelCollection.deleteMany(org.bson.Document())

    client.close()
}

data class JenaAndHeader(
    val acceptHeader: String,
    val jenaType: String,
)
