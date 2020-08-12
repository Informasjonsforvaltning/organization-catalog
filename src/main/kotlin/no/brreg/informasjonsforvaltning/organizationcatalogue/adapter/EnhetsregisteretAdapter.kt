package no.brreg.informasjonsforvaltning.organizationcatalogue.adapter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.brreg.informasjonsforvaltning.organizationcatalogue.configuration.AppProperties
import no.brreg.informasjonsforvaltning.organizationcatalogue.mapping.createOrgPath
import no.brreg.informasjonsforvaltning.organizationcatalogue.mapping.cutOrgPathForParents
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.EnhetsregisteretOrganization
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.aggregation.VariableOperators
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

private val LOGGER = LoggerFactory.getLogger(EnhetsregisteretAdapter::class.java)

@Component
class EnhetsregisteretAdapter(private val appProperties: AppProperties) {

    fun getOrganizationAndParents(organizationId: String): List<EnhetsregisteretOrganization> {
        LOGGER.info("Downloading data regarding '$organizationId' from Enhetsregisteret")
        return downloadAndParseOrganization(organizationId)
            ?.downloadParentOrgsAndCreateOrgPath()
            ?: emptyList()
    }

    private fun downloadAndParseOrganization(organizationId: String): EnhetsregisteretOrganization? {
        val connection = URL(appProperties.enhetsregisteretProxyUrl + organizationId).openConnection() as HttpURLConnection

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

    private fun EnhetsregisteretOrganization.downloadParentOrgsAndCreateOrgPath(): List<EnhetsregisteretOrganization> {
        val orgList: MutableList<EnhetsregisteretOrganization> = mutableListOf(this)
        val idSet: MutableSet<String> = mutableSetOf(organisasjonsnummer)
        val isTestOrganization = isTestEnvironment() && appProperties.testOrganizations.contains(organisasjonsnummer)

        var topParentOrgForm: String? = organisasjonsform?.kode
        var parentOrganizationId: String? = overordnetEnhet

        while (parentOrganizationId != null) {
            idSet.add(parentOrganizationId)
            val parent = downloadAndParseOrganization(parentOrganizationId)
            if (parent != null) {
                topParentOrgForm = parent.organisasjonsform?.kode
                parentOrganizationId = parent.overordnetEnhet
                orgList.add(parent)
            } else parentOrganizationId = null
        }

        val completeOrgPath = createOrgPath(isTestOrganization, idSet, topParentOrgForm)

        return orgList.map {
            it.copy(orgPath = cutOrgPathForParents(completeOrgPath, it.organisasjonsnummer))
        }
    }

    private fun isTestEnvironment(): Boolean =
        setOf("localhost", "staging", "demo")
            .any { it in appProperties.organizationCatalogueHost }
}
