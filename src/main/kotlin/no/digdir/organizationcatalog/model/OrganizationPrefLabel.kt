package no.digdir.organizationcatalog.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "org_pref_labels")
data class OrganizationPrefLabel(
    @Id
    @Column(name = "organization_id")
    val organizationId: String = "",
    @Column(name = "nb")
    val nb: String? = null,
    @Column(name = "nn")
    val nn: String? = null,
    @Column(name = "en")
    val en: String? = null,
) {
    val value: PrefLabel
        get() = PrefLabel(nb = nb, nn = nn, en = en)
}

fun OrganizationPrefLabel(
    organizationId: String,
    value: PrefLabel,
) = OrganizationPrefLabel(
    organizationId = organizationId,
    nb = value.nb,
    nn = value.nn,
    en = value.en,
)
