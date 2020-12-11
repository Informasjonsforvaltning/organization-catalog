package no.brreg.informasjonsforvaltning.organizationcatalogue.contract

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.model.Organization
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.model.PrefLabel
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.ApiTestContext
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.ORG_0
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.ORG_1
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.ORG_2
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.Expect
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.JenaAndHeader
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.NOT_UPDATED_0
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.NOT_UPDATED_1
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.UPDATED_0
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.UPDATED_1
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.UPDATE_VALUES
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.apiGet
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.apiAuthorizedRequest
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.jwk.Access
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.jwk.JwtToken
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration

private val turtle = JenaAndHeader("text/turtle", "TURTLE")
private val ldjson = JenaAndHeader("application/ld+json", "JSONLD")
private val rdfjson = JenaAndHeader("application/rdf+json", "RDF/JSON")
private val rdfxml = JenaAndHeader("application/rdf+xml", "RDFXML")
private val mapper = jacksonObjectMapper().findAndRegisterModules()

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    properties = ["spring.profiles.active=test"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [ApiTestContext.Initializer::class])
@Tag("contract")
internal class OrganizationsApi : ApiTestContext() {

    @LocalServerPort
    var port: Int = 0

    @Test
    fun pingTest() {
        val response = apiGet("/ping", port, "text/plain")["body"]
        Expect(response).to_equal("pong")
    }

    @Nested
    internal inner class GetOrganizationById {

        @Test
        fun whenEmptyResult404() {
            val status = apiGet("/organizations/123Null", port, "application/json")["status"]
            Expect(status).to_equal(HttpStatus.NOT_FOUND.value())
        }

        @Test
        fun wrongAcceptHeader() {
            val status = apiGet("/organizations/123", port, "text/plain")["status"]
            Expect(status).to_equal(HttpStatus.NOT_ACCEPTABLE.value())
        }

        @Test
        fun getById() {
            val response0 = apiGet("/organizations/${ORG_0.organizationId}", port, turtle.acceptHeader)["body"]
            val response1 = apiGet("/organizations/${ORG_1.organizationId}", port, turtle.acceptHeader)["body"]
            val response2 = apiGet("/organizations/${ORG_2.organizationId}", port, turtle.acceptHeader)["body"]

            Expect(response0).isomorphic_with_response_in_file("getBrreg.ttl", turtle.jenaType)
            Expect(response1).isomorphic_with_response_in_file("getATB.ttl", turtle.jenaType)
            Expect(response2).isomorphic_with_response_in_file("getForsvaret.ttl", turtle.jenaType)
        }
    }

    @Nested
    internal inner class GetDelegatedOrganizations {

        @Test
        fun wrongAcceptHeader() {
            val status = apiGet("/organizations/delegated", port, "text/plain")["status"]
            Expect(status).to_equal(HttpStatus.NOT_ACCEPTABLE.value())
        }

        @Test
        fun listOfDelegatedOrganizationsFromSupportedRequest() {
            val response0 = apiGet("/organizations/delegated", port, rdfjson.acceptHeader)["body"]

            Expect(response0).isomorphic_with_response_in_file("getBrreg.ttl", rdfjson.jenaType)
        }
    }

    @Nested
    internal inner class GetOrganizations {

        @Test
        fun missingAcceptHeader() {
            val status = apiGet("/organizations", port, null)["status"]
            Expect(status).to_equal(HttpStatus.OK.value())
        }

        @Test
        fun okWhenEmptyResult() {
            val response = apiGet("/organizations?name=NameDoesNotExist", port, "application/json")
            Expect(response["status"]).to_equal(HttpStatus.OK.value())
            Expect(response["body"]).to_equal("[]")
        }

        @Test
        fun okGetAll() {
            val response = apiGet("/organizations", port, "application/json")
            val body: List<Organization> = mapper.readValue(response["body"] as String)
            Expect(response["status"]).to_equal(HttpStatus.OK.value())
            Expect(body.size).to_equal(7)
        }

        @Test
        fun getByNameSeveralPossibilities() {
            val response = apiGet("/organizations/?name=ET", port, turtle.acceptHeader)["body"]
            Expect(response).isomorphic_with_response_in_file("searchByName.ttl", turtle.jenaType)
        }

        @Test
        fun getByNameSingle() {
            val response = apiGet("/organizations/?name=forsvaret", port, ldjson.acceptHeader)["body"]
            Expect(response).isomorphic_with_response_in_file("getForsvaret.ttl", ldjson.jenaType)
        }

        @Test
        fun getByOrgidSeveralPossibilities() {
            val response = apiGet("/organizations/?organizationId=60", port, turtle.acceptHeader)["body"]
            Expect(response).isomorphic_with_response_in_file("searchByOrgId.ttl", turtle.jenaType)
        }

        @Test
        fun getByOrgidSingle() {
            val response = apiGet("/organizations/?organizationId=994686011", port, rdfxml.acceptHeader)["body"]
            Expect(response).isomorphic_with_response_in_file("getATB.ttl", rdfxml.jenaType)
        }

        @Test
        fun getByOrgidAndName() {
            val response = apiGet("/organizations/?name=ET&organizationId=60", port, rdfxml.acceptHeader)["body"]
            Expect(response).isomorphic_with_response_in_file("getBrreg.ttl", rdfxml.jenaType)
        }
    }

    @Nested
    internal inner class UpdateOrganization {

        @Test
        fun unauthorizedWhenNotLoggedIn() {
            val response = apiAuthorizedRequest("/organizations/994686011", port, "{}", null, "PUT")
            Expect(response["status"]).to_equal(HttpStatus.UNAUTHORIZED.value())
        }

        @Test
        fun forbiddenWhenNotAdmin() {
            val response = apiAuthorizedRequest("/organizations/994686011", port, "{}", JwtToken(Access.ORG_READ).toString(), "PUT")
            Expect(response["status"]).to_equal(HttpStatus.FORBIDDEN.value())
        }

        @Test
        fun notFoundWhenIdNotAvailableInDB() {
            val response = apiAuthorizedRequest("/organizations/123NotFound", port, "{}", JwtToken(Access.ROOT).toString(), "PUT")
            Expect(response["status"]).to_equal(HttpStatus.NOT_FOUND.value())
        }

        @Test
        fun badRequestOnBlankName() {
            val blankName = Organization().apply { name = "  " }
            val response = apiAuthorizedRequest("/organizations/${ORG_0.organizationId}", port, mapper.writeValueAsString(blankName), JwtToken(Access.ROOT).toString(), "PUT")
            Expect(response["status"]).to_equal(HttpStatus.BAD_REQUEST.value())
        }

        @Test
        fun noValuesUpdated() {
            val orgId = ORG_0.organizationId
            val nullValues = Organization().apply { prefLabel = PrefLabel() }

            val preValues: Organization = mapper.readValue(apiGet("/organizations/$orgId", port, "application/json")["body"] as String)

            val response = apiAuthorizedRequest("/organizations/$orgId", port, mapper.writeValueAsString(nullValues), JwtToken(Access.ROOT).toString(), "PUT")
            Expect(response["status"]).to_equal(HttpStatus.OK.value())
            val updated: Organization = mapper.readValue(response["body"] as String)
            Expect(updated).to_equal(preValues)
        }

        @Test
        fun updateOnlyName() {
            val orgId = NOT_UPDATED_0.organizationId
            val newName = Organization().apply { name = UPDATED_0.name }

            val preValues: Organization = mapper.readValue(apiGet("/organizations/$orgId", port, "application/json")["body"] as String)
            Expect(preValues).to_equal(NOT_UPDATED_0)

            val updated: Organization = mapper.readValue(apiAuthorizedRequest("/organizations/$orgId", port, mapper.writeValueAsString(newName), JwtToken(Access.ROOT).toString(), "PUT")["body"] as String)
            Expect(updated).to_equal(UPDATED_0)
        }

        @Test
        fun updateAllValuesExceptOrganizationIdAndNorwegianRegistry() {
            val orgId = NOT_UPDATED_1.organizationId
            val oldValues: Organization = mapper.readValue(apiGet("/organizations/$orgId", port, "application/json")["body"] as String)
            Expect(oldValues).to_equal(NOT_UPDATED_1)

            val updated: Organization = mapper.readValue(apiAuthorizedRequest("/organizations/$orgId", port, mapper.writeValueAsString(UPDATE_VALUES), JwtToken(Access.ROOT).toString(), "PUT")["body"] as String)
            Expect(updated).to_equal(UPDATED_1)
        }
    }

    @Nested
    internal inner class GetOrgPath {

        @Test
        fun returnsOrgPathForOrganization() {
            val orgPath = apiGet("/organizations/orgpath/${ORG_0.organizationId}", port, "text/plain")["body"] as String
            Expect(orgPath).to_equal(ORG_0.orgPath)
        }

        @Test
        fun createsDefaultOrgPathWhenNotFound() {
            val orgPath = apiGet("/organizations/orgpath/123", port, "text/plain")["body"] as String
            Expect(orgPath).to_equal("/ANNET/123")
        }

    }
}
