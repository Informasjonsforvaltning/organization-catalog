package no.digdir.organizationcatalog.mapping


fun createOrgPath(isTest: Boolean, orgIdSet: Set<String>, topOrgForm: String?): String {
    val orgPathBase = if (isTest) "/ANNET" else getOrgPathBase(topOrgForm)
    val idString = orgIdSet
        .reversed()
        .joinToString("/")
    return "$orgPathBase/$idString"
}

fun cutOrgPathForParents(orgPath: String, orgNmbr: String) =
    "${orgPath.split(orgNmbr)[0]}$orgNmbr"

fun getOrgPathBase(topOrgForm: String?): String =
    when (topOrgForm) {
        "STAT" -> "/STAT"
        "FYLK" -> "/FYLKE"
        "KOMM" -> "/KOMMUNE"
        "IKS" -> "/ANNET"
        else -> "/PRIVAT"
    }
