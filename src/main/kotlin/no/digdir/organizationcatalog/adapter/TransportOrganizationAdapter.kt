package no.digdir.organizationcatalog.adapter

import no.digdir.organizationcatalog.model.TransportOrganization
import no.digdir.organizationcatalog.utils.transformToTransportDataList
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
    fun downloadTransportDataList(): List<TransportOrganization> =
        downloadTransportDataRaw()
            ?.let { it.transformToTransportDataList() }
            ?.distinctBy { it.companyNumber } ?: emptyList()

    fun downloadTransportDataRaw(): String? =
        URI(enturDataUrl)
            .toURL()
            .openConnection()
            .run {
                this as HttpURLConnection
                this.setRequestProperty(enturHeaderKey, enturHeaderValue)

                if (responseCode != HttpStatus.OK.value()) {
                    logger.error("Download of transport data failed with code $responseCode")
                    return@run null
                }

                try {
                    inputStream.bufferedReader().use { reader ->
                        reader.readText()
                    }
                } catch (ex: Exception) {
                    logger.error("Error reading downloaded data : ${ex.message}")
                    null
                }
            }
}
