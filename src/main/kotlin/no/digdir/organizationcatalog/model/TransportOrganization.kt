package no.digdir.organizationcatalog.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import no.digdir.organizationcatalog.utils.prefLabelFromName

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class TransportOrganization(
    @JsonProperty("id")
    val id: String? = null,
    @JsonProperty("CompanyNumber")
    val companyNumber: String? = null,
    @JsonProperty("TradingName")
    val tradingName: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class PublicationDelivery(
    @JsonProperty("dataObjects")
    val dataObjects: DataObjects? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DataObjects(
    @JsonProperty("ResourceFrame")
    val resourceFrame: ResourceFrame? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ResourceFrame(
    @JsonProperty("organisations")
    val organisations: Organisations? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Organisations(
    @JsonProperty("Authority")
    val authorities: List<TransportOrganization>? = emptyList(),
    @JsonProperty("Operator")
    val operators: List<TransportOrganization>? = emptyList(),
)

fun TransportOrganization.toDB() =
    TransportOrganizationDB(
        id = this.id,
        organizationId = this.companyNumber ?: "",
        prefLabel = this.tradingName?.prefLabelFromName(),
    )

fun Iterable<TransportOrganization>.toDB() = this.map { it.toDB() }
