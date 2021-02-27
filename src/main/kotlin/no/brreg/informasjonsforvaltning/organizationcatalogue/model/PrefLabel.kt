package no.brreg.informasjonsforvaltning.organizationcatalogue.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class PrefLabel(
    val nb: String? = null,
    val nn: String? = null,
    val en: String? = null
)
