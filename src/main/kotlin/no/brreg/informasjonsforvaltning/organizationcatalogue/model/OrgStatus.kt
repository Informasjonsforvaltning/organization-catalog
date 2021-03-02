package no.brreg.informasjonsforvaltning.organizationcatalogue.model

enum class OrgStatus(val label: PrefLabel) {
    NORMAL(PrefLabel(nb = "Normal aktivitet", en = "Normal activity", nn = "Normal aktivitet")),
    LIQUIDATED(PrefLabel(nb = "Avviklet", en = "Liquidated", nn = "Avvikla"))
}
