package no.digdir.organizationcatalog.utils

fun String.isOrganizationNumber(): Boolean {
    val regex = Regex("""^[0-9]{9}$""")
    return regex.containsMatchIn(this)
}
