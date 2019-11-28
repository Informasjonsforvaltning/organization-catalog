package no.brreg.informasjonsforvaltning.organizationcatalogue.controller

import no.brreg.informasjonsforvaltning.organizationcatalogue.configuration.AppProperties
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.api.DomainsApi
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.model.Domain
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.*
import no.brreg.informasjonsforvaltning.organizationcatalogue.security.EndpointPermissions
import no.brreg.informasjonsforvaltning.organizationcatalogue.service.DomainsService
import no.brreg.informasjonsforvaltning.organizationcatalogue.service.MissingOrganizationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import javax.servlet.http.HttpServletRequest

private val LOGGER = LoggerFactory.getLogger(DomainsApiImpl::class.java)

@Controller
open class DomainsApiImpl(
    private val domainsService: DomainsService,
    private val appProperties: AppProperties,
    private val endpointPermissions: EndpointPermissions
) : DomainsApi {

    override fun addDomain(httpServletRequest: HttpServletRequest, domain: Domain): ResponseEntity<Void> =
        if (endpointPermissions.hasAdminPermission()) {
            try {
                LOGGER.info("add domain ${domain.name}")
                domainsService.addDomain(domain)
                ResponseEntity<Void>(HttpStatus.OK)
            } catch (e: Exception) {
                if (e is MissingOrganizationException) ResponseEntity<Void>(HttpStatus.BAD_REQUEST)
                else ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        } else ResponseEntity(HttpStatus.FORBIDDEN)

    override fun getDomain(httpServletRequest: HttpServletRequest, name: String): ResponseEntity<Any> {
        LOGGER.info("get domain $name")
        val jenaType = acceptHeaderToJenaType(httpServletRequest.getHeader("Accept"))
        val domain = domainsService.getDomain(name)

        val urls = ExternalUrls(
            organizationCatalogue = appProperties.organizationCatalogueUrl,
            organizationDomains = appProperties.organizationDomainsUrl
        )

        return when {
            jenaType == JenaType.NOT_ACCEPTABLE -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            domain == null -> ResponseEntity(HttpStatus.NOT_FOUND)
            jenaType == JenaType.NOT_JENA -> ResponseEntity(domain, HttpStatus.OK)
            else -> ResponseEntity(listOf(domain).domainsJenaResponse(jenaType, urls), HttpStatus.OK)
        }
    }

    override fun getDomainOrganizations(httpServletRequest: HttpServletRequest, name: String): ResponseEntity<Any> {
        LOGGER.info("get organizations for domain $name")
        val jenaType = acceptHeaderToJenaType(httpServletRequest.getHeader("Accept"))
        val domain = domainsService.getDomain(name)

        val urls = ExternalUrls(
            organizationCatalogue = appProperties.organizationCatalogueUrl
        )

        return when {
            jenaType == JenaType.NOT_ACCEPTABLE -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            domain == null -> ResponseEntity(HttpStatus.NOT_FOUND)
            jenaType == JenaType.NOT_JENA -> ResponseEntity(domain.organizations, HttpStatus.OK)
            else -> ResponseEntity(domain.organizationsJenaResponse(jenaType, urls), HttpStatus.OK)
        }
    }

    override fun getAllDomains(httpServletRequest: HttpServletRequest): ResponseEntity<Any> {
        LOGGER.info("get all domains")
        val jenaType = acceptHeaderToJenaType(httpServletRequest.getHeader("Accept"))
        val domains = domainsService.getAllDomains()

        val urls = ExternalUrls(
            organizationCatalogue = appProperties.organizationCatalogueUrl,
            organizationDomains = appProperties.organizationDomainsUrl
        )

        return when (jenaType) {
            JenaType.NOT_ACCEPTABLE -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            JenaType.NOT_JENA -> ResponseEntity(domains, HttpStatus.OK)
            else -> ResponseEntity(domains.domainsJenaResponse(jenaType, urls), HttpStatus.OK)
        }
    }
}
