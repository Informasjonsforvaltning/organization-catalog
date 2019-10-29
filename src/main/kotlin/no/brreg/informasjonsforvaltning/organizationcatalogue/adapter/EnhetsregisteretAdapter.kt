package no.brreg.informasjonsforvaltning.organizationcatalogue.adapter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.brreg.informasjonsforvaltning.organizationcatalogue.configuration.ProfileConditionalValues
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.EnhetsregisteretOrganization
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

private val LOGGER = LoggerFactory.getLogger(EnhetsregisteretAdapter::class.java)

@Component
class EnhetsregisteretAdapter(private val profileConditionalValues: ProfileConditionalValues) {

    fun getOrganization(organizationId: String): EnhetsregisteretOrganization? {
        LOGGER.info("Downloading data regarding '$organizationId' from Enhetsregisteret")
        return downloadAndParseOrganization(organizationId)
            ?.withOrgPath()
    }

    private fun downloadAndParseOrganization(organizationId: String): EnhetsregisteretOrganization? {
        val connection = URL(profileConditionalValues.enhetsregisteretUrl() + organizationId).openConnection() as HttpURLConnection

        if (connection.responseCode != HttpStatus.OK.value()) {
            LOGGER.error("Organization with id '$organizationId' not found in Enhetsregisteret")
            return null
        } else {
            val jsonBody = connection
                .inputStream
                .bufferedReader()
                .use(BufferedReader::readText)

            return try {
                jacksonObjectMapper().readValue(jsonBody)
            } catch (t: Throwable) {
                LOGGER.error("Unable to parse response from Enhetsregisteret for '$organizationId'")
                null
            }
        }
    }

    private fun EnhetsregisteretOrganization.withOrgPath(): EnhetsregisteretOrganization {
        val idSet: MutableSet<String> = mutableSetOf(organisasjonsnummer)

        var topParentOrgForm: String? = organisasjonsform?.kode
        var parentOrganizationId: String? = overordnetEnhet

        while (parentOrganizationId != null) {
            idSet.add(parentOrganizationId)
            val parent = downloadAndParseOrganization(parentOrganizationId)
            topParentOrgForm = parent?.organisasjonsform?.kode
            parentOrganizationId = parent?.overordnetEnhet
        }

        val orgPathBase = getOrgPathBase(topParentOrgForm)

        val idString = idSet
            .reversed()
            .joinToString("/")

        return copy(orgPath = "$orgPathBase/$idString")
    }

    private fun getOrgPathBase(topOrgForm: String?): String =
        when (topOrgForm) {
            "STAT" -> "STAT"
            "FYLK" -> "FYLKE"
            "KOMM" -> "KOMMUNE"
            "IKS" -> "ANNET"
            else -> "PRIVAT"
        }
}
