package no.digdir.organizationcatalog.mapping

import no.digdir.organizationcatalog.model.OrganizationPrefLabel
import no.digdir.organizationcatalog.model.PrefLabel
import no.digdir.organizationcatalog.model.TransportOrganization
import no.digdir.organizationcatalog.model.toDB
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@Tag("unit")
@ActiveProfiles("test")
class UpdateFromTransportDataTest {
    val transportOrganization: TransportOrganization =
        TransportOrganization(
            id = "transp-123",
            companyNumber = "123456789",
            tradingName = "Transport AS",
        )

    @Test
    fun `Should create if there is no transport data in DB`() {
        val initialTransportDataDB =
            OrganizationPrefLabel(
                organizationId = transportOrganization.companyNumber!!,
                prefLabel = PrefLabel(),
            )
        val updatedTransportDataDB = transportOrganization.updateOrCreateTransportData(initialTransportDataDB)

        assertEquals(
            updatedTransportDataDB.prefLabel.nb,
            transportOrganization.tradingName,
        )
    }

    @Test
    fun `Should not update if the new downloaded data does not have a company name`() {
        val initialTransportDataDB = transportOrganization.toDB()
        var updatedTransportDataDB =
            transportOrganization
                .copy(tradingName = null)
                .updateOrCreateTransportData(initialTransportDataDB)

        assertNotNull(
            updatedTransportDataDB.prefLabel.nb,
        )

        updatedTransportDataDB =
            transportOrganization
                .copy(tradingName = "")
                .updateOrCreateTransportData(initialTransportDataDB)

        assertFalse(
            updatedTransportDataDB.prefLabel.nb.isNullOrEmpty(),
        )
    }

    @Test
    fun `Should not update if the new downloaded data has new trading name`() {
        val initialTransportDataDB = transportOrganization.toDB()
        var updatedTransportData = transportOrganization.copy(tradingName = "New name")

        val updatedTransportDataDB = updatedTransportData.updateOrCreateTransportData(initialTransportDataDB)

        assertNotNull(
            updatedTransportDataDB.prefLabel.nb,
        )

        assertEquals(updatedTransportDataDB.prefLabel.nb, updatedTransportData.tradingName)
    }

    @Test
    fun `Should update if data has new has empty or null prefLabel`() {
        val initialTransportDataDB = transportOrganization.toDB().copy(prefLabel = PrefLabel())
        var updatedTransportData = transportOrganization.copy(tradingName = "New name")

        val updatedTransportDataDB = updatedTransportData.updateOrCreateTransportData(initialTransportDataDB)

        assertNotNull(
            updatedTransportDataDB.prefLabel.nb,
        )

        assertEquals(updatedTransportDataDB.prefLabel.nb, updatedTransportData.tradingName)
    }
}
