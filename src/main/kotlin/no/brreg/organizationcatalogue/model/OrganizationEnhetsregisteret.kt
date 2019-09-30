package no.brreg.organizationcatalogue.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class EnhetsregisteretOrganization (
    val organisasjonsnummer: String,
    val navn: String? = null,
    val overordnetEnhet: String? = null,
    val registreringsdatoEnhetsregisteret: String? = null,
    val orgPath: String? = null,
    val postadresse: EnhetsregisteretAddress? = null,
    val forretningsadresse: EnhetsregisteretAddress? = null,
    val organisasjonsform: EnhetsregisteretCode? = null,
    val naeringskode1: EnhetsregisteretCode? = null,
    val institusjonellSektorkode: EnhetsregisteretCode? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EnhetsregisteretCode (
    val kode: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EnhetsregisteretAddress (
    val kommunenummer: String?
)
