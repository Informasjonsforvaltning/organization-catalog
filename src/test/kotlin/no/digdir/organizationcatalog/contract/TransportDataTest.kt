package no.digdir.organizationcatalog.contract

import no.digdir.organizationcatalog.adapter.EnhetsregisteretAdapter
import no.digdir.organizationcatalog.adapter.TransportOrganizationAdapter
import no.digdir.organizationcatalog.configuration.AppProperties
import no.digdir.organizationcatalog.mapping.isNullOrEmpty
import no.digdir.organizationcatalog.model.OrganizationDB
import no.digdir.organizationcatalog.model.OrganizationPrefLabel
import no.digdir.organizationcatalog.model.PrefLabel
import no.digdir.organizationcatalog.repository.OrganizationCatalogRepository
import no.digdir.organizationcatalog.repository.OrganizationPrefLabelRepository
import no.digdir.organizationcatalog.service.OrganizationCatalogService
import no.digdir.organizationcatalog.utils.ApiTestContext
import no.digdir.organizationcatalog.utils.prefLabelFromName
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import kotlin.jvm.optionals.getOrNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    properties = ["spring.profiles.active=test"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ContextConfiguration(initializers = [ApiTestContext.Initializer::class])
@Tag("contract")
class TransportDataTest : ApiTestContext() {
    val logger = LoggerFactory.getLogger(TransportDataTest::class.java)

    private lateinit var organizationCatalogService: OrganizationCatalogService

    @Autowired
    lateinit var enhetsregisteretAdapter: EnhetsregisteretAdapter

    @Autowired
    lateinit var transportOrganizationAdapter: TransportOrganizationAdapter

    @Autowired
    lateinit var organizationPrefLabelRepository: OrganizationPrefLabelRepository

    @Autowired
    lateinit var repository: OrganizationCatalogRepository

    @Autowired
    lateinit var appProperties: AppProperties

    private val orgId = "123456789"

    @BeforeEach
    fun beforeEach() {
        organizationCatalogService =
            OrganizationCatalogService(
                repository = repository,
                organizationPrefLabelRepository = organizationPrefLabelRepository,
                enhetsregisteretAdapter = enhetsregisteretAdapter,
                transportOrganizationAdapter = transportOrganizationAdapter,
                appProperties = appProperties,
            )
    }

    @Test
    fun `should update organizationDB when calling updateEnhetsregisteret`() {
        organizationCatalogService.updateTransportData()
        val orgPrefLabel = organizationPrefLabelRepository.findById(orgId).getOrNull()

        assertNotNull { orgPrefLabel }

        organizationCatalogService.updateEntryFromEnhetsregisteret(orgPrefLabel?.organizationId!!)
        val updatedOrganizationDB = repository.findById(orgPrefLabel.organizationId).getOrNull()

        assertNotNull { updatedOrganizationDB }
        assertEquals(orgPrefLabel.value, updatedOrganizationDB?.prefLabel)

        logger.info("OrgPrefLabel ${orgPrefLabel.value}")
        logger.info("OrganizationDB ${updatedOrganizationDB?.prefLabel}")
    }

    @Test
    fun `Should update transportData DB with new names`() {
        val oldTransportDb =
            OrganizationPrefLabel(
                organizationId = orgId,
                value =
                    PrefLabel(
                        nb = "Old Name",
                    ),
            )
        organizationPrefLabelRepository.save(oldTransportDb)
        organizationCatalogService.updateTransportData()
        val orgPrefLabelNew = organizationPrefLabelRepository.findById(orgId).getOrNull()

        assertEquals("Innlandstrafikk Nyy", orgPrefLabelNew?.value?.nb)
    }

    @Test
    fun `Should update transportData DB document if it has null value`() {
        val oldTransportDb =
            OrganizationPrefLabel(
                organizationId = orgId,
                value = PrefLabel(),
            )
        organizationPrefLabelRepository.save(oldTransportDb)
        organizationCatalogService.updateTransportData()
        val orgPrefLabelNew = organizationPrefLabelRepository.findById(orgId).getOrNull()

        assertEquals("Innlandstrafikk Nyy", orgPrefLabelNew?.value?.nb)
    }

    @Test
    fun `Should update transportData DB document if it has empty value`() {
        val oldTransportDb =
            OrganizationPrefLabel(
                organizationId = orgId,
                value = PrefLabel(""),
            )
        organizationPrefLabelRepository.save(oldTransportDb)
        organizationCatalogService.updateTransportData()
        val orgPrefLabelNew = organizationPrefLabelRepository.findById(orgId).getOrNull()

        assertEquals("Innlandstrafikk Nyy", orgPrefLabelNew?.value?.nb)
    }

    @Test
    fun `Prioritize downloaded transport data first if it is new`() {
        organizationCatalogService.updateTransportData()
        val orgPrefLabel = organizationPrefLabelRepository.findById(orgId).getOrNull()

        assertNotNull { orgPrefLabel }

        val enhetsRegisterets = enhetsregisteretAdapter.getOrganizationAndParents(orgId)

        assertFalse { enhetsRegisterets.isEmpty() }

        organizationCatalogService.updateEntryFromEnhetsregisteret(orgId)
        val updatedOrganizationDB = repository.findById(orgId).getOrNull()

        assertNotNull { updatedOrganizationDB }
        assertEquals(orgPrefLabel?.value, updatedOrganizationDB?.prefLabel)
    }

    @Test
    fun `Should not prioritize saved transport data if preflabel is null or empty`() {
        organizationCatalogService.updateTransportData()
        val orgPrefLabel = organizationPrefLabelRepository.findById(orgId).getOrNull()

        assertNotNull { orgPrefLabel }

        organizationPrefLabelRepository.save(orgPrefLabel?.copy(value = PrefLabel())!!)
        organizationCatalogService.updateEntryFromEnhetsregisteret(orgId)
        val updatedOrganizationDB = repository.findById(orgId).getOrNull()

        assertNotNull { updatedOrganizationDB }
        assertEquals("TESTENHETEN I TESTSUND".prefLabelFromName(), updatedOrganizationDB?.prefLabel)
    }

    @Test
    fun `Should prioritize saved to save downloaded enhets data if it is different from DB and transport data is not empty`() {
        organizationCatalogService.updateTransportData()
        organizationCatalogService.updateEntryFromEnhetsregisteret(orgId)
        var organizationDB = repository.findById(orgId).getOrNull()
        val organizationPrefLabel = organizationPrefLabelRepository.findById(orgId).getOrNull()

        assertNotNull { organizationPrefLabel }
        assertNotNull { organizationDB }

        organizationDB = repository.save(organizationDB?.copy(prefLabel = null)!!)

        assertTrue { organizationDB.prefLabel.isNullOrEmpty() }

        organizationCatalogService.updateEntryFromEnhetsregisteret(orgId)
        val updatedOrganizationDB = repository.findById(orgId).getOrNull()

        assertNotNull { updatedOrganizationDB }
        assertEquals(organizationPrefLabel?.value, updatedOrganizationDB?.prefLabel)
    }

    @Test
    fun `test transport data download`() {
        val listData = transportOrganizationAdapter.downloadTransportDataList()

        assertFalse(listData.isEmpty())
    }

    @Test
    fun `should add newly update with transport data if name is different`() {
        organizationCatalogService.updateTransportData()
        val orgPrefLabel = organizationPrefLabelRepository.findAll().first()
        repository.save(
            OrganizationDB(
                organizationId = orgId,
                name = orgPrefLabel.organizationId,
                prefLabel = orgPrefLabel.value.copy(nb = " Old Name"),
            ),
        )

        organizationCatalogService.updateAllEntriesFromEnhetsregisteret()
        val updatedOrganizationDB = organizationPrefLabelRepository.findAll().first()

        assertEquals(
            orgPrefLabel.value.nb,
            updatedOrganizationDB.value.nb,
        )
    }
}
