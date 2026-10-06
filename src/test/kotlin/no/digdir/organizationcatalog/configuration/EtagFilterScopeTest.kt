package no.digdir.organizationcatalog.configuration

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.util.ContentCachingResponseWrapper

@Tag("unit")
class EtagFilterScopeTest {
    private fun responseSeenByChain(method: String): ServletResponse {
        val filter = WebConfig().shallowEtagHeaderFilter().filter!!
        var seen: ServletResponse? = null
        filter.doFilter(
            MockHttpServletRequest(method, "/organizations/123456789"),
            MockHttpServletResponse(),
            FilterChain { _, res -> seen = res },
        )
        return seen!!
    }

    @Test
    fun `GET is buffered so an ETag can be computed`() {
        Assertions.assertInstanceOf(ContentCachingResponseWrapper::class.java, responseSeenByChain("GET"))
    }

    @Test
    fun `PUT is not buffered`() {
        Assertions.assertFalse(responseSeenByChain("PUT") is ContentCachingResponseWrapper)
    }
}
