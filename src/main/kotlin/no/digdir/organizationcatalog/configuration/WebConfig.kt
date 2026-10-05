package no.digdir.organizationcatalog.configuration

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.filter.ShallowEtagHeaderFilter

@Configuration
open class WebConfig {
    // Weak ETag on purpose: Tomcat will not compress a response carrying a strong one.
    // Scoped to GET on the catalogue paths: the filter buffers every response it sees,
    // and a non-GET can never yield an ETag.
    @Bean
    open fun shallowEtagHeaderFilter(): FilterRegistrationBean<ShallowEtagHeaderFilter> {
        val filter: ShallowEtagHeaderFilter =
            object : ShallowEtagHeaderFilter() {
                override fun shouldNotFilter(request: HttpServletRequest) = !HttpMethod.GET.matches(request.method)
            }.apply { isWriteWeakETag = true }

        return FilterRegistrationBean(filter)
            .apply { addUrlPatterns("/organizations", "/organizations/*") }
    }
}
