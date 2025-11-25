package no.digdir.organizationcatalog.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application")
data class AppProperties(
    val transDataUrl: String,
    val enhetsregisteretUrl: String,
    val enhetsregisteretHtmlUrl: String,
    val enhetsregisteretProxyUrl: String,
    val municipalityUrl: String,
    val organizationCatalogUrl: String,
    val testOrganizations: Set<String> = emptySet(),
    val defaultOrgPath: String,
)
