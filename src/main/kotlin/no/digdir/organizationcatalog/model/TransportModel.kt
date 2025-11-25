package no.digdir.organizationcatalog.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class TransportModel(
    @JsonProperty("id")
    val id: String? = null,

    @JsonProperty("CompanyNumber")
    val companyNumber: String? = null,

    @JsonProperty("TradingName")
    val tradingName: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class PublicationDelivery(
    @JsonProperty("dataObjects")
    val dataObjects: DataObjects? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DataObjects(
    @JsonProperty("ResourceFrame")
    val resourceFrame: ResourceFrame? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ResourceFrame(
    @JsonProperty("organisations")
    val organisations: Organisations? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Organisations(
    @JsonProperty("Authority")
    val authorities: List<TransportModel>? = emptyList(),

    @JsonProperty("Operator")
    val operators: List<TransportModel>? = emptyList()
)

fun TransportModel.toDB() = TransportModelDB(
    organizationId = this.companyNumber ?: "",
    prefLabel = this.tradingName?.let { PrefLabel(nb = it) }
)

fun Iterable<TransportModel>.toDB() = this.map { it.toDB() }
