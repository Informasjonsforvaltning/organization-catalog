package no.digdir.organizationcatalog.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

@Embeddable
data class EmbeddedPrefLabel(
    @Column(name = "pref_label_nb")
    val nb: String? = null,
    @Column(name = "pref_label_nn")
    val nn: String? = null,
    @Column(name = "pref_label_en")
    val en: String? = null,
)

@Entity
@Table(name = "organizations")
data class OrganizationDB(
    @Id
    @Column(name = "organization_id")
    val organizationId: String = "",
    @field:NotBlank
    @Column(name = "name", nullable = false)
    val name: String = "",
    @Column(name = "international_registry")
    val internationalRegistry: String? = null,
    @Column(name = "org_type")
    val orgType: String? = null,
    @Column(name = "org_path")
    val orgPath: String? = null,
    @Column(name = "sub_organization_of")
    val subOrganizationOf: String? = null,
    @Column(name = "issued")
    val issued: LocalDate? = null,
    @Column(name = "municipality_number")
    val municipalityNumber: String? = null,
    @Column(name = "industry_code")
    val industryCode: String? = null,
    @Column(name = "sector_code")
    val sectorCode: String? = null,
    @Embedded
    val prefLabel: EmbeddedPrefLabel? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "org_status")
    val orgStatus: OrgStatus? = null,
    @Column(name = "homepage")
    val homepage: String? = null,
    @Column(name = "subordinate", nullable = false)
    val subordinate: Boolean = false,
)
