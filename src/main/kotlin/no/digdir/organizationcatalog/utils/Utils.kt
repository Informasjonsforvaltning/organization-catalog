package no.digdir.organizationcatalog.utils

import no.digdir.organizationcatalog.model.PrefLabel
import java.util.Locale

fun String.isOrganizationNumber(): Boolean {
    val regex = Regex("""^[0-9]{9}$""")
    return regex.containsMatchIn(this)
}

fun String.prefLabelFromName(): PrefLabel =
    PrefLabel(
        nb =
            this
                .lowercase(Locale.getDefault())
                .replaceFirstChar { it.titlecase(Locale.getDefault()) },
    )
