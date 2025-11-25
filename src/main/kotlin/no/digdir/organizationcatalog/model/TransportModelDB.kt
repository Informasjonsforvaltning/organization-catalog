package no.digdir.organizationcatalog.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "transport_data")
data class TransportModelDB(
    val id: String? = null,
    @Id
    val organizationId: String,
    val prefLabel: PrefLabel? = null,
)

fun TransportModelDB.toTransportModel() = TransportModel(
    id = this.id,
    companyNumber = this.organizationId,
    tradingName = this.prefLabel?.nb,
)
