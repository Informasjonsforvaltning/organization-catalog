package no.digdir.organizationcatalog.adapter

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.digdir.organizationcatalog.model.PublicationDelivery
import no.digdir.organizationcatalog.model.TransportOrganization
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.net.HttpURLConnection
import java.net.URI

private val logger = LoggerFactory.getLogger(TransportOrganizationAdapter::class.java)

@Component
class TransportOrganizationAdapter(
    @Value("\${application.enturDataUrl}") val enturDataUrl: String,
    @Value("\${application.enturHeaderKey}") val enturHeaderKey: String,
    @Value("\${application.enturHeaderValue}") val enturHeaderValue: String,
) {
    fun downloadTransportDataList(): List<TransportOrganization> {
        logger.info("Downloading trans data list")

        URI(enturDataUrl)
            .toURL()
            .openConnection()
            .run {
                this as HttpURLConnection
                this.setRequestProperty(enturHeaderKey, enturHeaderValue)

                if (responseCode != HttpStatus.OK.value()) {
                    logger.error("Download of transport data failed with code $responseCode")
                    return emptyList()
                }

                try {
                    val xmlMapper = XmlMapper().registerKotlinModule()

                    val transList =
                        inputStream.bufferedReader().use { reader ->
                            val xmlResponse = reader.readText()
                            val rootJsonNode = xmlMapper.readTree(xmlResponse)
                            val jsonMapper = jacksonObjectMapper()

                            val organisations =
                                jsonMapper.treeToValue(rootJsonNode, PublicationDelivery::class.java)?.let {
                                    (
                                        it.dataObjects
                                            ?.resourceFrame
                                            ?.organisations
                                            ?.authorities ?: emptyList()
                                    ) +
                                        (
                                            it.dataObjects
                                                ?.resourceFrame
                                                ?.organisations
                                                ?.operators ?: emptyList()
                                        )
                                }

                            organisations ?: emptyList()
                        }

                    transList.forEach { logger.info(it.toString()) }

                    return transList.distinctBy { listOf(it.companyNumber) }
                } catch (ex: Exception) {
                    logger.error("Error parsing downloaded data data : ${ex.message}")
                    return emptyList()
                }
            }
    }
}
