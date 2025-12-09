package no.digdir.organizationcatalog.model

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(strict = false)
data class TransportOrganization(
    @field:Attribute(name = "id")
    var id: String? = null,
    @field:Element(name = "CompanyNumber")
    var companyNumber: String? = null,
    @field:Element(name = "TradingName")
    var tradingName: String? = null,
)

@Root(name = "PublicationDelivery", strict = false)
data class PublicationDelivery(
    @field:Element(name = "dataObjects", required = false)
    var dataObjects: DataObjects? = null,
)

@Root(name = "dataObjects", strict = false)
data class DataObjects(
    @field:Element("ResourceFrame", required = false)
    var resourceFrame: ResourceFrame? = null,
)

@Root(name = "ResourceFrame", strict = false)
data class ResourceFrame(
    @field:Element(name = "organisations", required = false)
    var organisations: Organizations? = null,
)

@Root(name = "organisations", strict = false)
data class Organizations(
    @field:ElementList(entry = "Authority", inline = true, required = false)
    var authorities: MutableList<TransportOrganization> = mutableListOf(),
    @field:ElementList(entry = "Operator", inline = true, required = false)
    var operators: MutableList<TransportOrganization> = mutableListOf(),
)

fun TransportOrganization.toDB() =
    OrganizationPrefLabel(
        organizationId = this.companyNumber ?: "",
        value =
            PrefLabel(
                nb = (this.tradingName ?: "").trim(),
            ),
    )

fun Iterable<TransportOrganization>.toDB() = this.map { it.toDB() }
