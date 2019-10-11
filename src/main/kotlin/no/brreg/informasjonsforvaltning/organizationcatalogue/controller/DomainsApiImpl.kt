package no.brreg.informasjonsforvaltning.organizationcatalogue.controller

import no.brreg.informasjonsforvaltning.organizationcatalogue.configuration.ProfileConditionalValues
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.api.DomainsApi
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.model.Domain
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.ExternalUrls
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.JenaType
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.acceptHeaderToJenaType
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.domainsJenaResponse
import no.brreg.informasjonsforvaltning.organizationcatalogue.jena.organizationsJenaResponse
import no.brreg.informasjonsforvaltning.organizationcatalogue.service.DomainsService
import no.brreg.informasjonsforvaltning.organizationcatalogue.service.MissingOrganizationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import javax.servlet.http.HttpServletRequest

@Controller
open class DomainsApiImpl (
    private val domainsService: DomainsService,
    private val profileConditionalValues: ProfileConditionalValues
) : DomainsApi {

    override fun addDomain(httpServletRequest: HttpServletRequest, domain: Domain): ResponseEntity<Void> =
        try {
            domainsService.addDomain(domain)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            if (e is MissingOrganizationException) ResponseEntity(HttpStatus.BAD_REQUEST)
            else ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

    override fun getDomain(httpServletRequest: HttpServletRequest, name: String): ResponseEntity<Any> {
        val jenaType = acceptHeaderToJenaType(httpServletRequest.getHeader("Accept"))
        val domain = domainsService.getDomain(name)

        val urls = ExternalUrls(
            organizationCatalogue = profileConditionalValues.organizationCatalogueUrl(),
            organizationDomains = profileConditionalValues.organizationDomainsUrl()
        )

        return when {
            jenaType == JenaType.NOT_ACCEPTABLE -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            domain == null -> ResponseEntity(HttpStatus.NOT_FOUND)
            jenaType == JenaType.NOT_JENA -> ResponseEntity(domain, HttpStatus.OK)
            else -> ResponseEntity(listOf(domain).domainsJenaResponse(jenaType, urls), HttpStatus.OK)
        }
    }

    override fun getDomainOrganizations(httpServletRequest: HttpServletRequest, name: String): ResponseEntity<Any> {
        val jenaType = acceptHeaderToJenaType(httpServletRequest.getHeader("Accept"))
        val domain = domainsService.getDomain(name)

        val urls = ExternalUrls(
            organizationCatalogue = profileConditionalValues.organizationCatalogueUrl()
        )

        return when {
            jenaType == JenaType.NOT_ACCEPTABLE -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            domain == null -> ResponseEntity(HttpStatus.NOT_FOUND)
            jenaType == JenaType.NOT_JENA -> ResponseEntity(domain.organizations, HttpStatus.OK)
            else -> ResponseEntity(domain.organizationsJenaResponse(jenaType, urls), HttpStatus.OK)
        }
    }

    override fun getAllDomains(httpServletRequest: HttpServletRequest): ResponseEntity<Any> {
        val jenaType = acceptHeaderToJenaType(httpServletRequest.getHeader("Accept"))
        val domains = domainsService.getAllDomains()

        val urls = ExternalUrls(
            organizationCatalogue = profileConditionalValues.organizationCatalogueUrl(),
            organizationDomains = profileConditionalValues.organizationDomainsUrl()
        )

        return when (jenaType) {
            JenaType.NOT_ACCEPTABLE -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
            JenaType.NOT_JENA -> ResponseEntity(domains, HttpStatus.OK)
            else -> ResponseEntity(domains.domainsJenaResponse(jenaType, urls), HttpStatus.OK)
        }
    }
}