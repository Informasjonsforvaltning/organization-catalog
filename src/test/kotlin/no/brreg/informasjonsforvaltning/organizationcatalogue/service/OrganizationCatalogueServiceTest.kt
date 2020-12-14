package no.brreg.informasjonsforvaltning.organizationcatalogue.service

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import no.brreg.informasjonsforvaltning.organizationcatalogue.adapter.EnhetsregisteretAdapter
import no.brreg.informasjonsforvaltning.organizationcatalogue.configuration.AppProperties
import no.brreg.informasjonsforvaltning.organizationcatalogue.repository.OrganizationCatalogueRepository
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.ENHETSREGISTERET_URL
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

import java.util.Optional

import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.ORG_0
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.orgDB0
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.springframework.test.context.ActiveProfiles

@Tag("unit")
@ActiveProfiles("test")
class OrganizationCatalogueServiceTest {

    private val repository: OrganizationCatalogueRepository = mock()
    private val adapter: EnhetsregisteretAdapter = mock()
    private val valuesMock: AppProperties = mock()
    private val catalogueService: OrganizationCatalogueService = OrganizationCatalogueService(repository, adapter, valuesMock)

    @Test
    fun getByIdNotFound() {
        whenever(repository.findById("123ID"))
            .thenReturn(Optional.empty())

        val publisher = catalogueService.getByOrgnr("123ID")

        assertNull(publisher)
    }

    @Test
    fun getById() {
        val persisted = orgDB0()
        whenever(repository.findById("123ID"))
            .thenReturn(Optional.of(persisted))
        whenever(valuesMock.enhetsregisteretUrl)
            .thenReturn(ENHETSREGISTERET_URL)

        val publisher = catalogueService.getByOrgnr("123ID")

        assertEquals(persisted.name, publisher!!.name)
        assertEquals(persisted.organizationId, publisher.organizationId)
        assertEquals(persisted.orgPath, publisher.orgPath)
        assertEquals(persisted.prefLabel, publisher.prefLabel)
        assertEquals(ENHETSREGISTERET_URL + persisted.organizationId, publisher.norwegianRegistry)
    }

    @Test
    fun getAll() {
        val persistedList = listOf(orgDB0())
        whenever(repository.findAll())
            .thenReturn(persistedList)
        whenever(valuesMock.enhetsregisteretUrl)
            .thenReturn(ENHETSREGISTERET_URL)

        val publisherList = catalogueService.getOrganizations(null, null)

        assertEquals(persistedList[0].name, publisherList[0].name)
        assertEquals(persistedList[0].organizationId, publisherList[0].organizationId)
        assertEquals(persistedList[0].orgPath, publisherList[0].orgPath)
        assertEquals(persistedList[0].prefLabel, publisherList[0].prefLabel)
        assertEquals(ENHETSREGISTERET_URL + persistedList[0].organizationId, publisherList[0].norwegianRegistry)
    }

    @Test
    fun getByOrgIdIsPrioritized() {
        val persistedList = listOf(orgDB0())
        whenever(repository.findByNameLikeAndOrganizationIdLike("Name", "OrgId"))
            .thenReturn(persistedList)
        whenever(valuesMock.enhetsregisteretUrl)
            .thenReturn(ENHETSREGISTERET_URL)

        val publisherList = catalogueService.getOrganizations("Name", "OrgId")

        assertEquals(persistedList[0].name, publisherList[0].name)
        assertEquals(persistedList[0].organizationId, publisherList[0].organizationId)
        assertEquals(persistedList[0].orgPath, publisherList[0].orgPath)
        assertEquals(persistedList[0].prefLabel, publisherList[0].prefLabel)
        assertEquals(ENHETSREGISTERET_URL + persistedList[0].organizationId, publisherList[0].norwegianRegistry)
    }

    @Test
    fun getByName() {
        val persistedList = listOf(orgDB0())
        whenever(repository.findByNameLike("NAME"))
            .thenReturn(persistedList)
        whenever(valuesMock.enhetsregisteretUrl)
            .thenReturn(ENHETSREGISTERET_URL)

        val publisherList = catalogueService.getOrganizations("Name", null)

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

        val publisher = catalogueService.updateEntry("123ID", ORG_0)

        assertNull(publisher)
    }

    @Test
    fun getOrganizationsWithDelegationPermissions() {
        whenever(repository.findByAllowDelegatedRegistration(true))
            .thenReturn(listOf(orgDB0()))
        whenever(valuesMock.enhetsregisteretUrl)
            .thenReturn(ENHETSREGISTERET_URL)

        val publishers = catalogueService.getOrganizationsWithDelegationPermissions()

        assertEquals(listOf(ORG_0), publishers)
    }
}
