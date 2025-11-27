package no.digdir.organizationcatalog.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "transport_data")
data class TransportOrganizationDB(
    @Id
    val organizationId: String,
    val navn: String? = null,
)

fun TransportOrganizationDB.toTransportOrganization() =
    TransportOrganization(
        companyNumber = this.organizationId,
        tradingName = this.navn,
    )
