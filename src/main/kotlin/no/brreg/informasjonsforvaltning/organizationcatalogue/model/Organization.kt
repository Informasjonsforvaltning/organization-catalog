package no.brreg.informasjonsforvaltning.organizationcatalogue.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Organization (
    val organizationId: String? = null,
    val norwegianRegistry: String? = null,
    val internationalRegistry: String? = null,
    val name: String? = null,
    val orgType: String? = null,
    val orgPath: String? = null,
    val subOrganizationOf: String? = null,
    val issued: LocalDate? = null,
    val municipalityNumber: String? = null,
    val industryCode: String? = null,
    val sectorCode: String? = null,
    val prefLabel: PrefLabel? = null,
    val orgStatus: OrgStatus? = null,
    val homepage: String? = null,
    val allowDelegatedRegistration: Boolean? = null
)
