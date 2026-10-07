package no.digdir.organizationcatalog.contract

import no.digdir.organizationcatalog.utils.ApiTestContext
import no.digdir.organizationcatalog.utils.Expect
import no.digdir.organizationcatalog.utils.apiGet
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    properties = ["spring.profiles.active=test"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ContextConfiguration(initializers = [ApiTestContext.Initializer::class])
@Tag("contract")
internal class RobotsTxt : ApiTestContext() {
    @LocalServerPort
    var port: Int = 0

    // Served unauthenticated by design, and as text/plain: a crawler ignores it otherwise.
    @Test
    fun `robots txt is served unauthenticated and disallows everything`() {
        val response = apiGet("/robots.txt", port, null)
        val lines = response["body"].toString().lines().map { it.trim() }

        Expect(response["status"]).to_equal(HttpStatus.OK.value())
        Expect(lines).to_contain("User-agent: *")
        Expect(lines).to_contain("Disallow: /")
        Expect(response["header"].toString()).to_contain("text/plain")
    }
}
