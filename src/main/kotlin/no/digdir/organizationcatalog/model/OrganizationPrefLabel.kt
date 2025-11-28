package no.digdir.organizationcatalog.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "orgPrefLabel")
data class OrganizationPrefLabel(
    @Id
    val organizationId: String,
    val prefLabel: PrefLabel,
)

fun OrganizationPrefLabel.toTransportOrganization() =
    TransportOrganization(
        companyNumber = this.organizationId,
        tradingName = this.prefLabel.nb,
    )
