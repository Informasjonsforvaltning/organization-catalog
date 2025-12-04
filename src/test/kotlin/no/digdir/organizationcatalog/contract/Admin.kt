package no.digdir.organizationcatalog.contract

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.organizationcatalog.model.Organization
import no.digdir.organizationcatalog.utils.ApiTestContext
import no.digdir.organizationcatalog.utils.Expect
import no.digdir.organizationcatalog.utils.apiAuthorizedRequest
import no.digdir.organizationcatalog.utils.apiGet
import no.digdir.organizationcatalog.utils.jwk.Access
import no.digdir.organizationcatalog.utils.jwk.JwtToken
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration

private val mapper = jacksonObjectMapper().findAndRegisterModules()

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    properties = ["spring.profiles.active=test"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ContextConfiguration(initializers = [ApiTestContext.Initializer::class])
@Tag("contract")
internal class Admin : ApiTestContext() {
    @LocalServerPort
    var port: Int = 0

    @Nested
    internal inner class UpdateSTAT {
        @Test
        fun unauthorizedWhenNotLoggedIn() {
            val response = apiAuthorizedRequest("/admin/update/stat", port, null, null, "POST")
            Expect(response["status"]).to_equal(HttpStatus.UNAUTHORIZED.value())
        }

        @Test
        fun forbiddenWhenNotAdmin() {
            val response =
                apiAuthorizedRequest("/admin/update/stat", port, null, JwtToken(Access.ORG_READ).toString(), "POST")
            Expect(response["status"]).to_equal(HttpStatus.FORBIDDEN.value())
        }

        @Test
        fun updateSTAT() {
            val statOrgs = listOf("984195796", "926721720")
            val oldValues: List<Organization> =
                mapper.readValue(apiGet("/organizations", port, "application/json")["body"] as String)
            Assertions.assertFalse(oldValues.map { it.organizationId }.contains(statOrgs[0]))
            Assertions.assertFalse(oldValues.map { it.organizationId }.contains(statOrgs[1]))

            val response =
                apiAuthorizedRequest("/admin/update/stat", port, null, JwtToken(Access.ROOT).toString(), "POST")
            Expect(response["status"]).to_equal(HttpStatus.OK.value())

            val updated: List<Organization> =
                mapper.readValue(apiGet("/organizations", port, "application/json")["body"] as String)
            Assertions.assertTrue(updated.map { it.organizationId }.containsAll(statOrgs))
        }

        @Test
        fun updateFYLK() {
            val fylkOrg = "971045698"
            val oldValues: List<Organization> =
                mapper.readValue(apiGet("/organizations", port, "application/json")["body"] as String)
            Assertions.assertFalse(oldValues.map { it.organizationId }.contains(fylkOrg))

            val response =
                apiAuthorizedRequest("/admin/update/fylk", port, null, JwtToken(Access.ROOT).toString(), "POST")
            Expect(response["status"]).to_equal(HttpStatus.OK.value())

            val updated: List<Organization> =
                mapper.readValue(apiGet("/organizations", port, "application/json")["body"] as String)
            Assertions.assertTrue(updated.map { it.organizationId }.contains(fylkOrg))
        }

        @Test
        fun updateKOMM() {
            val kommOrg = "964969590"
            val oldValues: List<Organization> =
                mapper.readValue(apiGet("/organizations", port, "application/json")["body"] as String)
            Assertions.assertFalse(oldValues.map { it.organizationId }.contains(kommOrg))

            val response =
                apiAuthorizedRequest("/admin/update/komm", port, null, JwtToken(Access.ROOT).toString(), "POST")
            Expect(response["status"]).to_equal(HttpStatus.OK.value())

            val updated: List<Organization> =
                mapper.readValue(apiGet("/organizations", port, "application/json")["body"] as String)
            Assertions.assertTrue(updated.map { it.organizationId }.contains(kommOrg))
        }
    }

}
