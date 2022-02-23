package no.digdir.organizationcatalog.model

import java.time.LocalDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.NotBlank

@Document(collection = "organizations")
data class OrganizationDB (
    @Id
    val organizationId: String,
    @field:NotBlank val name: String,
    val internationalRegistry: String? = null,
    val orgType: String? = null,
    val orgPath: String? = null,
    val subOrganizationOf: String? = null,
    val issued: LocalDate? = null,
    val municipalityNumber: String? = null,
    val industryCode: String? = null,
    val sectorCode: String? = null,
    val prefLabel: PrefLabel? = null,
    val domains: Set<String>? = null,
    val orgStatus: OrgStatus? = null,
    val homepage: String? = null,
    val allowDelegatedRegistration: Boolean? = null
)
