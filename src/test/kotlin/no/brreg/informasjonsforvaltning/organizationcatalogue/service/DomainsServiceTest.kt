package no.brreg.informasjonsforvaltning.organizationcatalogue.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.DomainDB
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.OrganizationDB
import no.brreg.informasjonsforvaltning.organizationcatalogue.repository.DomainsRepository
import no.brreg.informasjonsforvaltning.organizationcatalogue.repository.OrganizationCatalogueRepository
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.DB_DOMAIN_0
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.DB_DOMAIN_1
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.DOMAIN_0
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.DOMAIN_TWO_ORGS
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.ORG_0
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.ORG_1
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.orgDB0
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.orgDB1
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentCaptor
import org.springframework.test.context.ActiveProfiles
import java.util.*

@Tag("unit")
@ActiveProfiles("test")
class DomainsServiceTest {

    private val domainsRepository: DomainsRepository = mock()
    private val orgRepository: OrganizationCatalogueRepository = mock()
    private val service: DomainsService = DomainsService(domainsRepository, orgRepository)

    private val domainCaptor = ArgumentCaptor.forClass(DomainDB::class.java)
    private val orgsCaptor: ArgumentCaptor<List<OrganizationDB>> = ArgumentCaptor.forClass(List::class.java as Class<List<OrganizationDB>>)

    @Test
    fun getByNameNotFound() {
        whenever(domainsRepository.findById("brreg.no"))
            .thenReturn(Optional.empty())

        val domain = service.getDomain("brreg.no")

        Assertions.assertNull(domain)
    }

    @Test
    fun singleDomainFromGetByName() {
        whenever(domainsRepository.findById("brreg.no"))
            .thenReturn(Optional.of(DB_DOMAIN_0))

        val domain = service.getDomain("brreg.no")

        Assertions.assertEquals(DOMAIN_0, domain)
    }

    @Test
    fun listOfDomainsfromGetAll() {
        whenever(domainsRepository.findAll())
            .thenReturn(listOf(DB_DOMAIN_0))

        val domains = service.getAllDomains()

        Assertions.assertEquals(listOf(DOMAIN_0), domains)
    }

    @Nested
    internal inner class AddDomain {
        @Test
        fun throwExceptionsWhenOrgDoesNotExist(){
            whenever(domainsRepository.findById("brreg.no"))
                .thenReturn(Optional.empty())
            whenever(orgRepository.findAllById(listOf(ORG_0.organizationId)))
                .thenReturn(emptyList())

            assertThrows<MissingOrganizationException> {
                service.addDomain(DOMAIN_0)
            }
        }

        @Test
        fun updateDomainAndAllOrganizationsWhenNewDomainIsAdded(){
            whenever(domainsRepository.findById("brreg.no"))
                .thenReturn(Optional.empty())
            whenever(orgRepository.findAllById(any()))
                .thenReturn(listOf(orgDB0(), orgDB1()))

            service.addDomain(DOMAIN_TWO_ORGS)

            verify(domainsRepository, times(1)).save(domainCaptor.capture())
            verify(orgRepository, times(1)).saveAll(orgsCaptor.capture())

            Assertions.assertEquals("brreg.no", domainCaptor.value.name)
            Assertions.assertEquals(setOf(ORG_0.organizationId, ORG_1.organizationId), domainCaptor.value.organizations)

            Assertions.assertEquals(2, orgsCaptor.value.size)
            Assertions.assertEquals(setOf("brreg.no"), orgsCaptor.value[0].domains)
            Assertions.assertEquals(setOf("brreg.no"), orgsCaptor.value[1].domains)
        }

        @Test
        fun bothDomainAndOrganizationIsUpdatedWhenDomainExists(){
            whenever(domainsRepository.findById("brreg.no"))
                .thenReturn(Optional.of(DB_DOMAIN_1))
            whenever(orgRepository.findAllById(listOf(ORG_0.organizationId)))
                .thenReturn(listOf(orgDB0()))

            service.addDomain(DOMAIN_0)

            verify(domainsRepository, times(1)).save(domainCaptor.capture())
            verify(orgRepository, times(1)).saveAll(orgsCaptor.capture())

            Assertions.assertEquals("brreg.no", domainCaptor.value.name)
            Assertions.assertEquals(setOf(ORG_0.organizationId, ORG_1.organizationId), domainCaptor.value.organizations)

            Assertions.assertEquals(1, orgsCaptor.value.size)
            Assertions.assertEquals(setOf("brreg.no"), orgsCaptor.value[0].domains)
        }
    }
}
