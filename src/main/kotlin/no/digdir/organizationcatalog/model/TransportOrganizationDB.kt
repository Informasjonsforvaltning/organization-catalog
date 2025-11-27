package no.digdir.organizationcatalog.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "transport_data")
data class TransportOrganizationDB(
    @Id
    val organizationId: String,
    val id: String? = null,
    val prefLabel: PrefLabel? = null,
)

fun TransportOrganizationDB.toTransportOrganization() = TransportOrganization(
    id = this.id,
    companyNumber = this.organizationId,
    tradingName = this.prefLabel?.nb,
)
