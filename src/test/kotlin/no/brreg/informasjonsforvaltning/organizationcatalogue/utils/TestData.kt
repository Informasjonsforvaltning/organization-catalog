package no.brreg.informasjonsforvaltning.organizationcatalogue.utils

import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.model.Organization
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.model.PrefLabel
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.OrganizationDB
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.ApiTestContainer.Companion.TEST_API
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap
import java.time.LocalDate

const val API_PORT = 8080
const val LOCAL_SERVER_PORT = 5000

const val MONGO_SERVICE_NAME = "mongodb"
const val MONGO_USER = "testuser"
const val MONGO_PASSWORD = "testpassword"
const val MONGO_AUTH = "?authSource=admin&authMechanism=SCRAM-SHA-1"
const val MONGO_PORT = 27017

const val WIREMOCK_TEST_HOST = "http://host.testcontainers.internal:$LOCAL_SERVER_PORT"

const val ENHETSREGISTERET_URL = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/"

val MONGO_ENV_VALUES: Map<String, String> = ImmutableMap.of(
    "MONGO_INITDB_ROOT_USERNAME", MONGO_USER,
    "MONGO_INITDB_ROOT_PASSWORD", MONGO_PASSWORD
)

val API_ENV_VALUES : Map<String,String> = ImmutableMap.of(
    "PUBAPI_MONGO_USERNAME", MONGO_USER,
    "PUBAPI_MONGO_PASSWORD", MONGO_PASSWORD,
    "SPRING_PROFILES_ACTIVE" , "test",
    "WIREMOCK_TEST_HOST" , WIREMOCK_TEST_HOST
)

fun getApiAddress( endpoint: String ): String{
    return "http://${TEST_API.getContainerIpAddress()}:${TEST_API.getMappedPort(API_PORT)}$endpoint"
}

val ORG_0 = Organization().apply {
    name = "REGISTERENHETEN I BRØNNØYSUND"
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/974760673"
    organizationId = "974760673"
    orgType = "ORGL"
    orgPath = "/STAT/912660680/974760673"
    subOrganizationOf = "912660680"
    issued = LocalDate.of(1999, 2, 3)
    municipalityNumber = "1813"
    industryCode = "84.110"
    sectorCode = "6100"
    allowDelegatedRegistration = true
    prefLabel = PrefLabel().apply {
        nb = "Brønnøysundregistrene"
    }
}

val ORG_1 =  Organization().apply {
    name = "ATB AS"
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/994686011"
    organizationId = "994686011"
    orgType = "AS"
    orgPath = "/PRIVAT/994686011"
    issued = LocalDate.of(1999, 2, 3)
    municipalityNumber = "5001"
    industryCode = "84.130"
    sectorCode = "6500"
    prefLabel = PrefLabel().apply {
        nn = "AtB AS"
    }
}

val ORG_2 =  Organization().apply {
    name = "FORSVARET"
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/986105174"
    organizationId = "986105174"
    orgType = "ORGL"
    orgPath = "/STAT/972417823/986105174"
    subOrganizationOf = "972417823"
    municipalityNumber = "0301"
    industryCode = "84.220"
    sectorCode = "6100"
    allowDelegatedRegistration = false
    prefLabel = PrefLabel().apply {
        en = "Forsvaret"
    }
}

val NOT_UPDATED_0 =  Organization().apply {
    name = "Not updated name"
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/44332211"
    organizationId = "44332211"
    orgType = "FYLK"
    orgPath = "/FYLKE/972417823/44332211"
    subOrganizationOf = "$WIREMOCK_TEST_HOST/972417823"
    municipalityNumber = "0301"
    industryCode = "84.220"
    sectorCode = "6100"
    prefLabel = PrefLabel().apply {
        nb = "nbNotUpdated"
        nn = "nnNotUpdated"
        en = "enNotUpdated"
    }
}

val NOT_UPDATED_1 =  Organization().apply {
    name = "Organization Name"
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/11223344"
    organizationId = "11223344"
    orgType = "STAT"
    orgPath = "/STAT/972417823/11223344"
    subOrganizationOf = "$WIREMOCK_TEST_HOST/972417823"
    municipalityNumber = "0301"
    industryCode = "84.220"
    sectorCode = "6100"
}

val UPDATE_VALUES =  Organization().apply {
    name = "Organization Name"
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/55667788"
    organizationId = "55667788"
    orgType = "ORGL"
    orgPath = "/STAT/986105174/55667788"
    subOrganizationOf = "$WIREMOCK_TEST_HOST/986105174"
    issued = LocalDate.of(2001, 1, 3)
    municipalityNumber = "6548"
    industryCode = "industryUriUpdated"
    sectorCode = "sectorUriUpdated"
    allowDelegatedRegistration = true
    prefLabel = PrefLabel().apply {
        nb = "nbLabelUpdated"
        nn = "nnLabelUpdated"
        en = "enLabelUpdated"
    }
}

val UPDATED_0 =  Organization().apply {
    name = "updatedName"
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/44332211"
    organizationId = "44332211"
    orgType = "FYLK"
    orgPath = "/FYLKE/972417823/44332211"
    subOrganizationOf = "$WIREMOCK_TEST_HOST/972417823"
    municipalityNumber = "0301"
    industryCode = "84.220"
    sectorCode = "6100"
    prefLabel = PrefLabel().apply {
        nb = "nbNotUpdated"
        nn = "nnNotUpdated"
        en = "enNotUpdated"
    }
}

val UPDATED_1 =  Organization().apply {
    name = "Organization Name"
    norwegianRegistry = "$WIREMOCK_TEST_HOST/enhetsregisteret/api/enheter/11223344"
    organizationId = "11223344"
    orgType = "ORGL"
    orgPath = "/STAT/986105174/55667788"
    subOrganizationOf = "$WIREMOCK_TEST_HOST/986105174"
    issued = LocalDate.of(2001, 1, 3)
    municipalityNumber = "6548"
    industryCode = "industryUriUpdated"
    sectorCode = "sectorUriUpdated"
    allowDelegatedRegistration = true
    prefLabel = PrefLabel().apply {
        nb = "nbLabelUpdated"
        nn = "nnLabelUpdated"
        en = "enLabelUpdated"
    }
}

var ORGS = listOf(ORG_0, ORG_1, ORG_2)

var ORG_DB_0 = OrganizationDB().apply {
    name = "REGISTERENHETEN I BRØNNØYSUND"
    organizationId = "974760673"
    orgType = "ORGL"
    orgPath = "/STAT/912660680/974760673"
    subOrganizationOf = "912660680"
    issued = LocalDate.of(1999, 2, 3)
    municipalityNumber = "1813"
    industryCode = "84.110"
    sectorCode = "6100"
    allowDelegatedRegistration = true
    prefLabel = PrefLabel().apply {
        nb = "Brønnøysundregistrene"
    }
}

fun dataForDBPopulation(): List<org.bson.Document> =
    listOf(ORG_0, ORG_1, ORG_2, NOT_UPDATED_0, NOT_UPDATED_1)
        .map { it.mapOrgToDBO() }

private fun Organization.mapOrgToDBO(): org.bson.Document =
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
