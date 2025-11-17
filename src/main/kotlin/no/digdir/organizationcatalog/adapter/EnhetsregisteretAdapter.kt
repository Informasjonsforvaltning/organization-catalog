package no.digdir.organizationcatalog.adapter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import no.digdir.organizationcatalog.configuration.AppProperties
import no.digdir.organizationcatalog.mapping.createOrgPath
import no.digdir.organizationcatalog.mapping.cutOrgPathForParents
import no.digdir.organizationcatalog.model.EnhetsregisteretEmbeddedWrapperDTO
import no.digdir.organizationcatalog.model.EnhetsregisteretOrganization
import no.digdir.organizationcatalog.model.EnhetsregisteretType
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URI

private val LOGGER = LoggerFactory.getLogger(EnhetsregisteretAdapter::class.java)

@Component
class EnhetsregisteretAdapter(
    private val appProperties: AppProperties,
) {
    fun getOrganizationAndParents(organizationId: String): List<EnhetsregisteretOrganization> {
        LOGGER.info("Downloading data regarding '$organizationId' from Enhetsregisteret")
        return downloadAndParseOrganization(organizationId)
            ?.downloadParentOrgsAndCreateOrgPath()
            ?: emptyList()
    }

    private fun downloadAndParseOrganization(organizationId: String): EnhetsregisteretOrganization? =
        runBlocking {
            val organizationPromise = async { getOrganizationFromEnhetsregisteret(organizationId) }
            val subordinateOrganizationPromise = async { getOrganizationFromEnhetsregisteret(organizationId, true) }

            organizationPromise.await() ?: subordinateOrganizationPromise.await()
        }

    private fun EnhetsregisteretOrganization.downloadParentOrgsAndCreateOrgPath(): List<EnhetsregisteretOrganization> {
        val orgList: MutableList<EnhetsregisteretOrganization> = mutableListOf(this)
        val idSet: MutableSet<String> = mutableSetOf(organisasjonsnummer)
        val isTestOrganization = isTestEnvironment() && appProperties.testOrganizations.contains(organisasjonsnummer)

        var topParentOrgForm: String? = organisasjonsform?.kode
        var parentOrganizationId: String? = overordnetEnhet

        while (parentOrganizationId != null) {
            idSet.add(parentOrganizationId)
            val parent = getOrganizationFromEnhetsregisteret(parentOrganizationId)
            if (parent != null) {
                topParentOrgForm = parent.organisasjonsform?.kode
                parentOrganizationId = parent.overordnetEnhet
                orgList.add(parent)
            } else {
                parentOrganizationId = null
            }
        }

        val completeOrgPath = createOrgPath(isTestOrganization, idSet, topParentOrgForm)

        return orgList.map {
            it.copy(orgPath = cutOrgPathForParents(completeOrgPath, it.organisasjonsnummer))
        }
    }

    private fun getOrganizationsFromEnhetsregisteret(path: String): EnhetsregisteretEmbeddedWrapperDTO? =
        URI("${appProperties.enhetsregisteretProxyUrl}$path")
            .toURL()
            .openConnection()
            .run {
                this as HttpURLConnection

                if (responseCode != HttpStatus.OK.value()) {
                    LOGGER.error("Download of organizations from path $path failed with code $responseCode")
                    return null
                }

                val jsonBody = inputStream.bufferedReader().use(BufferedReader::readText)

                return try {
                    jacksonObjectMapper().readValue(jsonBody)
                } catch (t: Throwable) {
                    LOGGER.warn("Unable to parse response from path ${appProperties.enhetsregisteretProxyUrl}$path")
                    null
                }
            }

    private fun getOrganizationFromEnhetsregisteret(
        organizationId: String,
        isSubordinate: Boolean = false,
    ): EnhetsregisteretOrganization? {
        try {
            Integer.parseInt(organizationId)
        } catch (e: NumberFormatException) {
            LOGGER.warn("Invalid organization identifier for Enhetsregisteret '$organizationId'")
            return null
        }

        URI("${appProperties.enhetsregisteretProxyUrl}/${if (isSubordinate) "underenheter" else "enheter"}/$organizationId")
            .toURL()
            .openConnection()
            .run {
                this as HttpURLConnection

                if (responseCode != HttpStatus.OK.value()) {
                    LOGGER.warn(
                        "Organization (${if (isSubordinate) "underenhet" else "enhet"}) with id '$organizationId' not found in Enhetsregisteret",
                    )
                    return null
                }

                val jsonBody = inputStream.bufferedReader().use(BufferedReader::readText)

                return try {
                    jacksonObjectMapper()
                        .readValue<EnhetsregisteretOrganization?>(jsonBody)
                        ?.copy(underenhet = isSubordinate)
                } catch (t: Throwable) {
                    LOGGER.warn("Unable to parse response from Enhetsregisteret for '$organizationId'")
                    null
                }
            }
    }

    fun getOrganizationsFromEnhetsregisteretByType(orgType: EnhetsregisteretType): List<EnhetsregisteretOrganization> =
        getOrganizationsFromEnhetsregisteret("/enheter?organisasjonsform=$orgType&size=10000")
            ?._embedded
            ?.enheter
            ?: emptyList()

    fun getOrganizationsFromEnhetsregisteretByParent(orgnr: String): List<EnhetsregisteretOrganization> =
        getOrganizationsFromEnhetsregisteret("/enheter?overordnetEnhet=$orgnr&size=10000")
            ?._embedded
            ?.enheter
            ?: emptyList()

    private fun isTestEnvironment(): Boolean =
        setOf("localhost", "staging", "demo")
            .any { it in appProperties.organizationCatalogUrl }
}
