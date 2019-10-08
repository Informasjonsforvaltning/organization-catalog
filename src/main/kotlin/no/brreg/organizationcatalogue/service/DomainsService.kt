package no.brreg.organizationcatalogue.service

import no.brreg.organizationcatalogue.generated.model.Domain
import no.brreg.organizationcatalogue.generated.model.Organization
import no.brreg.organizationcatalogue.mapping.mapToGenerated
import no.brreg.organizationcatalogue.model.DomainDB
import no.brreg.organizationcatalogue.model.OrganizationDB
import no.brreg.organizationcatalogue.repository.DomainsRepository
import no.brreg.organizationcatalogue.repository.OrganizationCatalogueRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class DomainsService (
    private val domainsRepository: DomainsRepository,
    private val catalogueRepository: OrganizationCatalogueRepository
) {

    fun addDomain(domain: Domain) {
        val orgs: List<OrganizationDB> = catalogueRepository.findAllById(domain.organizations).toList()

        if (orgs.isEmpty() || orgs.size != domain.organizations.size) throw MissingOrganizationException()

        var domainDB: DomainDB? = domainsRepository.findByIdOrNull(domain.name)

        if (domainDB == null || domainDB.organizations == null) {
           domainDB = DomainDB().apply {
               name = domain.name
               organizations = domain.organizations.toSet()
           }
        } else {
            domainDB.organizations = domainDB.organizations.plus(domain.organizations)
        }

        orgs.forEach {
            if (it.domains == null) {
                it.domains = setOf(domain.name)
            } else {
                it.domains = it.domains.plus(domain.name)
            }
        }

        domainsRepository.save(domainDB)
        catalogueRepository.saveAll(orgs)
    }

    fun getDomain(domainName: String): Domain? =
        domainsRepository
            .findByIdOrNull(domainName)
            ?.mapToGenerated()

    fun getAllDomains(): List<Domain> =
        domainsRepository
            .findAll()
            .map { it.mapToGenerated() }

}

class MissingOrganizationException: Exception()