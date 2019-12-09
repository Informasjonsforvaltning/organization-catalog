package no.brreg.informasjonsforvaltning.organizationcatalogue.contract

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.model.Domain
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.model.Organization
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.ApiTestContainer
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.DOMAIN
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.Expect
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.ORG_0
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.ORG_1
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.ORG_2
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.ORG_WITHOUT_DOMAIN
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.ORG_WITH_DOMAIN
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.apiAuthorizedRequest
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.apiGet
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.jwk.Access
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.jwk.JwtToken
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpStatus

private val mapper = jacksonObjectMapper().findAndRegisterModules()

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("contract")
class DomainsApi : ApiTestContainer() {

    @Test
    fun readyTest() {
        val status = apiGet("/ready", "text/plain")["status"]
        Expect(status).to_equal(HttpStatus.OK.value())
    }

    @Nested
    internal inner class GetDomain {

        @Test
        fun whenEmptyResult404() {
            val status = apiGet("/domains/123.Null", "text/turtle")["status"]
            Expect(status).to_equal(HttpStatus.NOT_FOUND.value())
        }

        @Test
        fun wrongAcceptHeader() {
            val status = apiGet("/domains/${DOMAIN.name}", "text/plain")["status"]
            Expect(status).to_equal(HttpStatus.NOT_ACCEPTABLE.value())
        }

        @Test
        fun okWhenDomainNameExists() {
            val response: Domain = mapper.readValue(apiGet("/domains/${DOMAIN.name}", "application/json")["body"] as String)

            Expect(response).to_equal(DOMAIN)
        }
    }

    @Nested
    internal inner class GetDomainOrganizations {

        @Test
        fun whenEmptyResult404() {
            val status = apiGet("/domains/123.Null/organizations", "application/ld+json")["status"]
            Expect(status).to_equal(HttpStatus.NOT_FOUND.value())
        }

        @Test
        fun wrongAcceptHeader() {
            val status = apiGet("/domains/${DOMAIN.name}/organizations", "text/plain")["status"]
            Expect(status).to_equal(HttpStatus.NOT_ACCEPTABLE.value())
        }

        @Test
        fun okWhenDomainIdExists() {
            val response: List<String> = mapper.readValue(apiGet("/domains/${DOMAIN.name}/organizations", "application/json")["body"] as String)

            Expect(response).to_equal(listOf(ORG_WITH_DOMAIN.organizationId))
        }
    }

    @Nested
    internal inner class GetAllDomains {

        @Test
        fun wrongAcceptHeader() {
            val status = apiGet("/domains", "text/plain")["status"]
            Expect(status).to_equal(HttpStatus.NOT_ACCEPTABLE.value())
        }

        @Test
        fun listOfExistingDomainsFromSupportedRequest() {
            val response: List<Domain> = mapper.readValue(apiGet("/domains", "application/json")["body"] as String)

            Expect(response).to_contain(DOMAIN)
        }
    }

    @Nested
    internal inner class AddDomain {

        @Test
        fun unauthorizedWhenNotLoggedIn() {
            val response = apiAuthorizedRequest("/domains", "{}", null, "POST")
            Expect(response["status"]).to_equal(HttpStatus.UNAUTHORIZED.value())
        }

        @Test
        fun forbiddenWhenNotAdmin() {
            val response = apiAuthorizedRequest("/domains", "{}", JwtToken(Access.ORG_READ).toString(), "POST")
            Expect(response["status"]).to_equal(HttpStatus.FORBIDDEN.value())
        }

        @Test
        fun badRequestOnNullName() {
            val nullName = Domain()
            val response = apiAuthorizedRequest("/domains", mapper.writeValueAsString(nullName), JwtToken(Access.ROOT).toString(), "POST")
            Expect(response["status"]).to_equal(HttpStatus.BAD_REQUEST.value())
        }

        @Test
        fun badRequestWhenOrgArrayIsEmpty() {
            val noOrgs = Domain().apply {
                name = "domain.no"
                organizations = emptyList()
            }

            val response = apiAuthorizedRequest("/domains", mapper.writeValueAsString(noOrgs), JwtToken(Access.ROOT).toString(), "POST")
            Expect(response["status"]).to_equal(HttpStatus.BAD_REQUEST.value())
        }

        @Test
        fun badRequestWhenOrgDoesNotExist() {
            val invalidOrg = Domain().apply {
                name = "domain.no"
                organizations = listOf(ORG_0.organizationId, "doesNotExist")
            }

            val response = apiAuthorizedRequest("/domains", mapper.writeValueAsString(invalidOrg), JwtToken(Access.ROOT).toString(), "POST")
            Expect(response["status"]).to_equal(HttpStatus.BAD_REQUEST.value())
        }

        @Test
        fun createDomainAndUpdateOrganization() {
            val toCreate = Domain().apply {
                name = "domain.no"
                organizations = listOf(ORG_WITHOUT_DOMAIN.organizationId)
            }

            val orgPreUpdate: Organization = mapper.readValue(apiGet("/organizations/${ORG_WITHOUT_DOMAIN.organizationId}", "application/json")["body"] as String)
            Expect(orgPreUpdate.domains.size).to_equal(0)

            val response0 = apiAuthorizedRequest("/domains", mapper.writeValueAsString(toCreate), JwtToken(Access.ROOT).toString(), "POST")
            Expect(response0["status"]).to_equal(HttpStatus.OK.value())

            val created: Domain = mapper.readValue(apiGet("/domains/${toCreate.name}", "application/json")["body"] as String)
            Expect(created).to_equal(toCreate)

            val orgPostUpdate: Organization = mapper.readValue(apiGet("/organizations/${ORG_WITHOUT_DOMAIN.organizationId}", "application/json")["body"] as String)
            Expect(orgPostUpdate.domains.size).to_equal(1)
            Expect(orgPostUpdate.domains[0]).to_equal(toCreate.name)
        }
    }

}
