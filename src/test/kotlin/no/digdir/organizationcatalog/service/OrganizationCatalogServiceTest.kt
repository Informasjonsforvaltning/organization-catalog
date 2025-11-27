package no.digdir.organizationcatalog.service

import no.digdir.organizationcatalog.adapter.EnhetsregisteretAdapter
import no.digdir.organizationcatalog.adapter.TransportOrganizationAdapter
import no.digdir.organizationcatalog.configuration.AppProperties
import no.digdir.organizationcatalog.repository.OrganizationCatalogRepository
import no.digdir.organizationcatalog.repository.TransportDataRepository
import no.digdir.organizationcatalog.utils.ENHETSREGISTERET_URL
import no.digdir.organizationcatalog.utils.ORG_0
import no.digdir.organizationcatalog.utils.ORG_DB0
import no.digdir.organizationcatalog.utils.ORG_DB1
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.test.context.ActiveProfiles
import java.util.Optional

@Tag("unit")
@ActiveProfiles("test")
class OrganizationCatalogServiceTest {
    private val repository: OrganizationCatalogRepository = mock()
    private val transportDataRepository: TransportDataRepository = mock()
    private val adapter: EnhetsregisteretAdapter = mock()
    private val transportOrganizationAdapter: TransportOrganizationAdapter = mock()
    private val valuesMock: AppProperties = mock()
    private val catalogService: OrganizationCatalogService =
        OrganizationCatalogService(
            repository,
            transportDataRepository,
            adapter,
            transportOrganizationAdapter,
            valuesMock,
        )

    @Test
    fun getByIdNotFound() {
        whenever(repository.findById("123ID"))
            .thenReturn(Optional.empty())

        val publisher = catalogService.getByOrgnr("123ID")

        assertNull(publisher)
    }

    @Test
    fun getById() {
        val persisted = ORG_DB0
        whenever(repository.findById("123ID"))
            .thenReturn(Optional.of(persisted))
        whenever(valuesMock.enhetsregisteretUrl)
            .thenReturn(ENHETSREGISTERET_URL)

        val publisher = catalogService.getByOrgnr("123ID")

        assertEquals(persisted.name, publisher!!.name)
        assertEquals(persisted.organizationId, publisher.organizationId)
        assertEquals(persisted.orgPath, publisher.orgPath)
        assertEquals(persisted.prefLabel, publisher.prefLabel)
        assertEquals(ENHETSREGISTERET_URL + persisted.organizationId, publisher.norwegianRegistry)
    }

    @Test
    fun getAll() {
        val persistedList = listOf(ORG_DB0)
        whenever(repository.findAll())
            .thenReturn(persistedList)
        whenever(valuesMock.enhetsregisteretUrl)
            .thenReturn(ENHETSREGISTERET_URL)

        val publisherList = catalogService.getOrganizations(null, null, null, true)

        assertEquals(persistedList[0].name, publisherList[0].name)
        assertEquals(persistedList[0].organizationId, publisherList[0].organizationId)
        assertEquals(persistedList[0].orgPath, publisherList[0].orgPath)
        assertEquals(persistedList[0].prefLabel, publisherList[0].prefLabel)
        assertEquals(ENHETSREGISTERET_URL + persistedList[0].organizationId, publisherList[0].norwegianRegistry)
    }

    @Test
    fun getByOrgIdIsPrioritized() {
        val persistedList = listOf(ORG_DB0, ORG_DB1)
        whenever(repository.findByNameLike("Name"))
            .thenReturn(persistedList)
        whenever(valuesMock.enhetsregisteretUrl)
            .thenReturn(ENHETSREGISTERET_URL)

        val publisherList = catalogService.getOrganizations("Name", listOf("974760673"), null, true)

        assertEquals(persistedList[0].name, publisherList[0].name)
        assertEquals(persistedList[0].organizationId, publisherList[0].organizationId)
        assertEquals(persistedList[0].orgPath, publisherList[0].orgPath)
        assertEquals(persistedList[0].prefLabel, publisherList[0].prefLabel)
        assertEquals(ENHETSREGISTERET_URL + persistedList[0].organizationId, publisherList[0].norwegianRegistry)
    }

    @Test
    fun getByName() {
        val persistedList = listOf(ORG_DB0)
        whenever(repository.findByNameLike("NAME"))
            .thenReturn(persistedList)
        whenever(valuesMock.enhetsregisteretUrl)
            .thenReturn(ENHETSREGISTERET_URL)

        val publisherList = catalogService.getOrganizations("Name", null, null, true)

        assertEquals(persistedList[0].name, publisherList[0].name)
        assertEquals(persistedList[0].organizationId, publisherList[0].organizationId)
        assertEquals(persistedList[0].orgPath, publisherList[0].orgPath)
        assertEquals(persistedList[0].prefLabel, publisherList[0].prefLabel)
        assertEquals(ENHETSREGISTERET_URL + persistedList[0].organizationId, publisherList[0].norwegianRegistry)
    }

    @Test
    fun updateNotFound() {
        whenever(repository.findById("123ID"))
            .thenReturn(Optional.empty())

        val publisher = catalogService.updateEntry("123ID", ORG_0)

        assertNull(publisher)
    }

    @Test
    fun getOrganizationsWithDelegationPermissions() {
        whenever(repository.findByAllowDelegatedRegistration(true))
            .thenReturn(listOf(ORG_DB0))
        whenever(valuesMock.enhetsregisteretUrl)
            .thenReturn(ENHETSREGISTERET_URL)

        val publishers = catalogService.getOrganizationsWithDelegationPermissions()

        assertEquals(listOf(ORG_0), publishers)
    }
}
