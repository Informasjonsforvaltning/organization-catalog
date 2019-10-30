package no.brreg.informasjonsforvaltning.organizationcatalogue.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("application")
data class AppProperties(
    val enhetsregisteretUrl: String,
    val organizationCatalogueUrl: String,
    val municipalityUrl: String,
    val organizationDomainsUrl: String) {
}
