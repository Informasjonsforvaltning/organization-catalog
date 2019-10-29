package no.brreg.informasjonsforvaltning.organizationcatalogue.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("application")
data class AppProperties(
    val enhetsregisteretUrl: String,
    val organizationCatalogueHost: String,
    val municipalityUrl: String,
    val organizationCatalogueUrl: String = organizationCatalogueHost + "/organizations/",
    val organizationDomainsUrl: String = organizationCatalogueHost + "/domains") {
}
