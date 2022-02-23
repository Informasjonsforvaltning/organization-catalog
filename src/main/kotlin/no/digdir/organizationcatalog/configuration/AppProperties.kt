package no.digdir.organizationcatalog.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("application")
data class AppProperties(
    val enhetsregisteretUrl: String,
    val enhetsregisteretHtmlUrl: String,
    val enhetsregisteretProxyUrl: String,
    val organizationCatalogHost: String,
    val municipalityUrl: String,
    val organizationCatalogUrl: String = organizationCatalogHost + "/organizations/",
    val organizationDomainsUrl: String = organizationCatalogHost + "/domains/",
    val testOrganizations: Set<String> = emptySet(),
    val defaultOrgPath: String) {
}
