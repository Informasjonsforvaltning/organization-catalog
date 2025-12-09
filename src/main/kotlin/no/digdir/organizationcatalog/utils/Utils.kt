package no.digdir.organizationcatalog.utils

import no.digdir.organizationcatalog.adapter.TransportOrganizationAdapter
import no.digdir.organizationcatalog.model.PrefLabel
import no.digdir.organizationcatalog.model.PublicationDelivery
import no.digdir.organizationcatalog.model.TransportOrganization
import org.simpleframework.xml.core.Persister
import org.slf4j.LoggerFactory
import java.util.Locale

private val logger = LoggerFactory.getLogger(TransportOrganizationAdapter::class.java)

fun String.isOrganizationNumber(): Boolean {
    val regex = Regex("""^[0-9]{9}$""")
    return regex.containsMatchIn(this)
}

fun String.prefLabelFromName(): PrefLabel =
    PrefLabel(
        nb =
            this
                .lowercase(Locale.getDefault())
                .replaceFirstChar { it.titlecase(Locale.getDefault()) },
    )

fun String.transformToTransportDataList(): List<TransportOrganization> =
    this.let {
        val serializer = Persister()
        try {
            val publicationDelivery = serializer.read(PublicationDelivery::class.java, this)
            logger.info("PublicationDelivery parsed: $publicationDelivery")

            val organisations =
                publicationDelivery
                    ?.dataObjects
                    ?.resourceFrame
                    ?.organisations

            return@let (organisations?.authorities ?: emptyList()) +
                (organisations?.operators ?: emptyList())
        } catch (ex: Exception) {
            logger.error("Error transforming xml data : ${ex.message}")
        }
        return@let emptyList()
    }
