package no.digdir.organizationcatalog.configuration

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.filter.ShallowEtagHeaderFilter

@Configuration
class WebConfig {
    @Bean
    fun shallowEtagHeaderFilter(): FilterRegistrationBean<ShallowEtagHeaderFilter> =
        FilterRegistrationBean<ShallowEtagHeaderFilter>(GetOnlyWeakEtagFilter()).apply {
            addUrlPatterns("/organizations", "/organizations/*")
        }
}

// Weak ETag on purpose: Tomcat will not compress a response carrying a strong one.
// GET only: the filter buffers every response it sees, and a non-GET can never yield an ETag.
private class GetOnlyWeakEtagFilter : ShallowEtagHeaderFilter() {
    init {
        isWriteWeakETag = true
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean = !HttpMethod.GET.matches(request.method)
}
