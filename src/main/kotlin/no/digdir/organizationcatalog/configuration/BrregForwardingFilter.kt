package no.digdir.organizationcatalog.configuration

import no.digdir.organizationcatalog.utils.isOrganizationNumber
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val LOGGER = LoggerFactory.getLogger(BrregForwardingFilter::class.java)

@Component
class BrregForwardingFilter(private val appProperties: AppProperties) : Filter {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        if (request != null && response != null && chain != null) {
            try {
                val httpRequest = request as HttpServletRequest
                val httpResponse = response as HttpServletResponse

                val acceptContainsHtml: Boolean = httpRequest.getHeader("Accept")
                    ?.contains("text/html")
                    ?: false

                val orgId = httpRequest.servletPath
                    .substringAfter("organizations/", "")

                if (acceptContainsHtml && orgId.isOrganizationNumber()) {
                    httpResponse.setHeader("Location", "${appProperties.enhetsregisteretHtmlUrl}$orgId")
                    httpResponse.status = HttpStatus.SEE_OTHER.value()
                    return
                }
            } catch (ex: Exception) {
                LOGGER.error("Forwarding filter failed", ex)
            }
        }

        chain?.doFilter(request, response)
    }

}
