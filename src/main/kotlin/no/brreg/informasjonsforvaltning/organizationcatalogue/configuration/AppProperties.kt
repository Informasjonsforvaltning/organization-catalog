package no.brreg.informasjonsforvaltning.organizationcatalogue.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("application")
data class AppProperties(
    val enhetsregisteretUrl: String,
    val enhetsregisteretHtmlUrl: String,
    val enhetsregisteretProxyUrl: String,
    val organizationCatalogueHost: String,
    val municipalityUrl: String,
    val organizationCatalogueUrl: String = organizationCatalogueHost + "/organizations/",
    val organizationDomainsUrl: String = organizationCatalogueHost + "/domains/",
    val testOrganizations: Set<String> = emptySet(),
    val defaultOrgPath: String) {
}
