package no.digdir.organizationcatalog.configuration

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.ShallowEtagHeaderFilter

@Configuration
open class WebConfig {
    // Weak ETag on purpose: Tomcat will not compress a response carrying a strong one.
    // Scoped to the catalogue paths so status endpoints are not buffered.
    @Bean
    open fun shallowEtagHeaderFilter(): FilterRegistrationBean<ShallowEtagHeaderFilter> =
        FilterRegistrationBean(ShallowEtagHeaderFilter().apply { isWriteWeakETag = true })
            .apply { addUrlPatterns("/organizations", "/organizations/*") }
}
