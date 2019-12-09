package no.brreg.informasjonsforvaltning.organizationcatalogue.utils

import java.io.BufferedReader
import java.net.URL
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.MongoClient
import org.springframework.http.HttpStatus
import java.io.OutputStreamWriter
import com.mongodb.MongoClientURI
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.ApiTestContainer.Companion.mongoContainer
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import java.net.HttpURLConnection

fun apiGet(endpoint: String, acceptHeader: String?): Map<String,Any> {

    return try{
        val connection = URL(getApiAddress(endpoint)).openConnection() as HttpURLConnection
        if(acceptHeader != null) connection.setRequestProperty("Accept", acceptHeader)
        connection.connect()

        if(isOK(connection.responseCode)) {
            val responseBody = connection.inputStream.bufferedReader().use(BufferedReader::readText)
            mapOf(
                "body"   to responseBody,
                "header" to connection.headerFields.toString(),
                "status" to connection.responseCode)
        } else {
            mapOf(
                "status" to connection.responseCode,
                "header" to " ",
                "body"   to " "
            )
        }
    } catch (e: Exception){
        mapOf(
            "status" to e.toString(),
            "header" to " ",
            "body"   to " "
        )
    }
}

fun apiAuthorizedRequest(endpoint : String, body: String?, token: String?, method: String): Map<String, Any> {
    val connection  = URL(getApiAddress(endpoint)).openConnection() as HttpURLConnection
    connection.requestMethod = method
    connection.setRequestProperty("Content-type", "application/json")
    connection.setRequestProperty("Accept", "application/json")

    if(!token.isNullOrEmpty()) {connection.setRequestProperty("Authorization", "Bearer $token")}

    return try {
        connection.doOutput = true
        connection.connect();

        if(body != null) {
            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(body)
            writer.close()
        }

        if(isOK(connection.responseCode)){
            mapOf(
                "body"   to connection.inputStream.bufferedReader().use(BufferedReader :: readText),
                "header" to connection.headerFields.toString(),
                "status" to connection.responseCode
            )
        } else {
            mapOf(
                "status" to connection.responseCode,
                "header" to " ",
                "body" to " "
            )
        }
    } catch (e: Exception) {
        mapOf(
            "status" to e.toString(),
            "header" to " ",
            "body"   to " "
        )
    }
}

private fun isOK(response: Int?): Boolean =
    if(response == null) false
    else HttpStatus.resolve(response)?.is2xxSuccessful == true

fun populateDB(){
    val uri= "mongodb://${MONGO_USER}:${MONGO_PASSWORD}@localhost:${mongoContainer.getMappedPort(MONGO_PORT)}/organization-catalogue?authSource=admin&authMechanism=SCRAM-SHA-1"
    val pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()))

    val mongoClient = MongoClient(MongoClientURI(uri))
    val mongoDatabase = mongoClient.getDatabase("organization-catalogue").withCodecRegistry(pojoCodecRegistry)

    val orgCollection = mongoDatabase.getCollection("organizations")
    orgCollection.insertMany(organizationsDBPopulation())

    val domainCollection = mongoDatabase.getCollection("domains")
    domainCollection.insertMany(domainsDBPopulation())

    mongoClient.close()
}

data class JenaAndHeader(
    val acceptHeader: String,
    val jenaType: String
)
