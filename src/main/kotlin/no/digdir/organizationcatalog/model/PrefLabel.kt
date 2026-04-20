package no.digdir.organizationcatalog.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class PrefLabel(
    val nb: String? = null,
    val nn: String? = null,
    val en: String? = null,
)

fun PrefLabel.toEmbedded() = EmbeddedPrefLabel(nb = nb, nn = nn, en = en)

fun EmbeddedPrefLabel.toPrefLabel() = PrefLabel(nb = nb, nn = nn, en = en)
