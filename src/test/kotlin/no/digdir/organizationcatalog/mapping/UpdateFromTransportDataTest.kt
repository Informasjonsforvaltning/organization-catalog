package no.digdir.organizationcatalog.mapping

import no.digdir.organizationcatalog.model.TransportOrganization
import no.digdir.organizationcatalog.model.TransportOrganizationDB
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
        val intitialTransportDataDB = TransportOrganizationDB(organizationId = transportOrganization.companyNumber!!)
        val updatedTransportDataDB = transportOrganization.updateOrCreateTransportData(intitialTransportDataDB)

        assertEquals(
            updatedTransportDataDB.navn,
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
            updatedTransportDataDB.navn
        )
        assertNotNull(
            updatedTransportDataDB.navn
        )

        updatedTransportDataDB =
            transportOrganization
                .copy(tradingName = " ")
                .updateOrCreateTransportData(initialTransportDataDB)

        assertFalse(
            updatedTransportDataDB.navn.isNullOrEmpty(),
        )
    }

    @Test
    fun `Should not update if the new downloaded data has new trading name`() {
        val initialTransportDataDB = transportOrganization.toDB()
        var updatedTransportData = transportOrganization.copy(tradingName = "New name")

        val updatedTransportDataDB = updatedTransportData.updateOrCreateTransportData(initialTransportDataDB)

        assertNotNull(
            updatedTransportDataDB.navn
        )

        assertEquals(updatedTransportDataDB.navn, updatedTransportData.tradingName)
    }

    @Test
    fun `Should update if data has new has empty or null prefLabel`() {
        val initialTransportDataDB = transportOrganization.toDB().copy(navn = null)
        var updatedTransportData = transportOrganization.copy(tradingName = "New name")

        val updatedTransportDataDB = updatedTransportData.updateOrCreateTransportData(initialTransportDataDB)

        assertNotNull(
            updatedTransportDataDB.navn,
        )

        assertEquals(updatedTransportDataDB.navn, updatedTransportData.tradingName)
    }
}
