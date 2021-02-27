package no.brreg.informasjonsforvaltning.organizationcatalogue.utils

import no.brreg.informasjonsforvaltning.organizationcatalogue.model.Organization
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.PrefLabel
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.EnhetsregisteretCode
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.EnhetsregisteretOrganization
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.OrganizationDB
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap
import java.time.LocalDate

const val LOCAL_SERVER_PORT = 5000

const val MONGO_USER = "testuser"
const val MONGO_PASSWORD = "testpassword"
const val MONGO_PORT = 27017

const val WIREMOCK_TEST_HOST = "http://localhost:5000"

const val ENHETSREGISTERET_URL = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/"

val MONGO_ENV_VALUES: Map<String, String> = ImmutableMap.of(
    "MONGO_INITDB_ROOT_USERNAME", MONGO_USER,
    "MONGO_INITDB_ROOT_PASSWORD", MONGO_PASSWORD
)

fun getApiAddress(port: Int, endpoint: String): String {
    return "http://localhost:$port$endpoint"
}

val ORG_0 = Organization(
    name = "REGISTERENHETEN I BRØNNØYSUND",
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/974760673",
    organizationId = "974760673",
    orgType = "ORGL",
    orgPath = "/STAT/912660680/974760673",
    subOrganizationOf = "912660680",
    issued = LocalDate.of(1999, 2, 3),
    municipalityNumber = "1813",
    industryCode = "84.110",
    sectorCode = "6100",
    allowDelegatedRegistration = true,
    prefLabel = PrefLabel(
        nb = "Brønnøysundregistrene"
    )
)

val ORG_1 = Organization(
    name = "ATB AS",
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/994686011",
    organizationId = "994686011",
    orgType = "AS",
    orgPath = "/PRIVAT/994686011",
    issued = LocalDate.of(1999, 2, 3),
    municipalityNumber = "5001",
    industryCode = "84.130",
    sectorCode = "6500",
    prefLabel = PrefLabel(nn = "AtB AS")
)

val ORG_2 = Organization(
    name = "FORSVARET",
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/986105174",
    organizationId = "986105174",
    orgType = "ORGL",
    orgPath = "/STAT/972417823/986105174",
    subOrganizationOf = "972417823",
    municipalityNumber = "0301",
    industryCode = "84.220",
    sectorCode = "6100",
    allowDelegatedRegistration = false,
    prefLabel = PrefLabel(en = "Forsvaret")
)

val NOT_UPDATED_0 = Organization(
    name = "Not updated name",
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/44332211",
    organizationId = "44332211",
    orgType = "FYLK",
    orgPath = "/FYLKE/972417823/44332211",
    subOrganizationOf = "$WIREMOCK_TEST_HOST/972417823",
    municipalityNumber = "0301",
    industryCode = "84.220",
    sectorCode = "6100",
    prefLabel = PrefLabel(
        nb = "nbNotUpdated",
        nn = "nnNotUpdated",
        en = "enNotUpdated"
    )
)

val NOT_UPDATED_1 = Organization(
    name = "Organization Name",
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/11223344",
    organizationId = "11223344",
    orgType = "STAT",
    orgPath = "/STAT/972417823/11223344",
    subOrganizationOf = "$WIREMOCK_TEST_HOST/972417823",
    municipalityNumber = "0301",
    industryCode = "84.220",
    sectorCode = "6100"
)

val UPDATE_VALUES = Organization(
    name = "Organization Name",
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/55667788",
    organizationId = "55667788",
    orgType = "ORGL",
    orgPath = "/STAT/986105174/55667788",
    subOrganizationOf = "$WIREMOCK_TEST_HOST/986105174",
    issued = LocalDate.of(2001, 1, 3),
    municipalityNumber = "6548",
    industryCode = "industryUriUpdated",
    sectorCode = "sectorUriUpdated",
    allowDelegatedRegistration = true,
    prefLabel = PrefLabel(
        nb = "nbLabelUpdated",
        nn = "nnLabelUpdated",
        en = "enLabelUpdated"
    )
)

val UPDATED_0 = Organization(
    name = "updatedName",
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/44332211",
    organizationId = "44332211",
    orgType = "FYLK",
    orgPath = "/FYLKE/972417823/44332211",
    subOrganizationOf = "$WIREMOCK_TEST_HOST/972417823",
    municipalityNumber = "0301",
    industryCode = "84.220",
    sectorCode = "6100",
    prefLabel = PrefLabel(
        nb = "nbNotUpdated",
        nn = "nnNotUpdated",
        en = "enNotUpdated"
    )
)

val UPDATED_1 = Organization(
    name = "Organization Name",
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/11223344",
    organizationId = "11223344",
    orgType = "ORGL",
    orgPath = "/STAT/986105174/55667788",
    subOrganizationOf = "$WIREMOCK_TEST_HOST/986105174",
    issued = LocalDate.of(2001, 1, 3),
    municipalityNumber = "6548",
    industryCode = "industryUriUpdated",
    sectorCode = "sectorUriUpdated",
    allowDelegatedRegistration = true,
    prefLabel = PrefLabel(
        nb = "nbLabelUpdated",
        nn = "nnLabelUpdated",
        en = "enLabelUpdated"
    )
)

val ORG_WITH_DOMAIN = Organization(
    name = "Organization With Domain",
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/55667788",
    organizationId = "55667788",
    orgType = "STAT",
    orgPath = "/STAT/972417823/55667788",
    subOrganizationOf = "$WIREMOCK_TEST_HOST/972417823",
    municipalityNumber = "0301",
    industryCode = "84.220",
    sectorCode = "6100"
)

val ORG_WITHOUT_DOMAIN = Organization(
    name = "Organization Without Domain",
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/98987676",
    organizationId = "98987676",
    orgType = "STAT",
    orgPath = "/STAT/972417823/98987676",
    subOrganizationOf = "$WIREMOCK_TEST_HOST/972417823",
    municipalityNumber = "0301",
    industryCode = "84.220",
    sectorCode = "6100"
)

val ORG_DB0 = OrganizationDB(
    name = "REGISTERENHETEN I BRØNNØYSUND",
    organizationId = "974760673",
    orgType = "ORGL",
    orgPath = "/STAT/912660680/974760673",
    subOrganizationOf = "912660680",
    issued = LocalDate.of(1999, 2, 3),
    municipalityNumber = "1813",
    industryCode = "84.110",
    sectorCode = "6100",
    domains = emptySet(),
    allowDelegatedRegistration = true,
    prefLabel = PrefLabel(nb = "Brønnøysundregistrene")
    )

val ORG_DB1 = OrganizationDB(
    name = "FORSVARET",
    organizationId = "986105174",
    orgType = "ORGL",
    orgPath = "/STAT/972417823/986105174",
    subOrganizationOf = "972417823",
    issued = LocalDate.of(1999, 2, 3),
    domains = emptySet(),
    allowDelegatedRegistration = true,
    prefLabel = PrefLabel(nb = "Forsvaret")
)

val NOT_UPDATED_2 = Organization(
    name = "TEST",
    organizationId = "123456789",
    norwegianRegistry = "http://localhost:5000/enhetsregisteret/api/enheter/123456789",
    orgType = "STAT",
    orgPath = "/STAT/123456789",
    issued = LocalDate.of(1990, 10, 31),
    municipalityNumber = "0301",
    industryCode = "84.110",
    sectorCode = "6100"
)

val UPDATED_2 = Organization(
    name = "TESTENHETEN I TESTSUND",
    organizationId = "123456789",
    orgType = "ORGL",
    norwegianRegistry = "http://localhost:5000/enhetsregisteret/api/enheter/123456789",
    orgPath = "/STAT/987654321/123456789",
    subOrganizationOf = "987654321",
    issued = LocalDate.of(1995, 8, 9),
    municipalityNumber = "1813",
    industryCode = "84.110",
    sectorCode = "6100",
    prefLabel = PrefLabel(nb = "Testenheten i testsund")
)

val PARENT_ORG = Organization(
    name = "TESTSTATEN",
    organizationId = "987654321",
    orgType = "STAT",
    norwegianRegistry = "http://localhost:5000/enhetsregisteret/api/enheter/987654321",
    orgPath = "/STAT/987654321",
    issued = LocalDate.of(2013, 10, 31),
    municipalityNumber = "0301",
    industryCode = "84.110",
    sectorCode = "6100"
)

val BRREG_ORG = EnhetsregisteretOrganization(
    navn = "FORSVARET",
    organisasjonsnummer = "986105174",
    overordnetEnhet = "972417823",
    organisasjonsform = EnhetsregisteretCode(kode = "ORGL"),
    orgPath = "/STAT/972417823/986105174",
    registreringsdatoEnhetsregisteret = "1999-02-03"
)

fun organizationsDBPopulation(): List<org.bson.Document> =
    listOf(
        ORG_0,
        ORG_1,
        ORG_2,
        NOT_UPDATED_0,
        NOT_UPDATED_1,
        ORG_WITH_DOMAIN,
        ORG_WITHOUT_DOMAIN,
        NOT_UPDATED_2,
        PARENT_ORG,
    )
        .map { it.mapDBO() }

private fun Organization.mapDBO(): org.bson.Document =
    org.bson.Document()
        .append("_id", organizationId)
        .append("name", name)
        .append("norwegianRegistry", norwegianRegistry)
        .append("organizationId", organizationId)
        .append("orgType", orgType)
        .append("orgPath", orgPath)
        .append("subOrganizationOf", subOrganizationOf)
        .append("issued", issued)
        .append("municipalityNumber", municipalityNumber)
        .append("industryCode", industryCode)
        .append("prefLabel", prefLabel)
        .append("sectorCode", sectorCode)
        .append("allowDelegatedRegistration", allowDelegatedRegistration)
