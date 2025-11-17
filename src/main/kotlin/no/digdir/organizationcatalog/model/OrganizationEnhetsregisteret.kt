package no.digdir.organizationcatalog.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class EnhetsregisteretOrganization(
    val organisasjonsnummer: String,
    val navn: String,
    val overordnetEnhet: String? = null,
    val registreringsdatoEnhetsregisteret: String? = null,
    val orgPath: String? = null,
    val slettedato: String? = null,
    val hjemmeside: String? = null,
    val postadresse: EnhetsregisteretAddress? = null,
    val forretningsadresse: EnhetsregisteretAddress? = null,
    val organisasjonsform: EnhetsregisteretCode? = null,
    val naeringskode1: EnhetsregisteretCode? = null,
    val institusjonellSektorkode: EnhetsregisteretCode? = null,
    val underenhet: Boolean = false,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EnhetsregisteretEmbeddedWrapperDTO(
    val _embedded: EnhetsregisteretLists?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EnhetsregisteretLists(
    val enheter: List<EnhetsregisteretOrganization> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EnhetsregisteretCode(
    val kode: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EnhetsregisteretAddress(
    val kommunenummer: String?,
)

enum class EnhetsregisteretType {
    STAT,
    FYLK,
    KOMM,
}
