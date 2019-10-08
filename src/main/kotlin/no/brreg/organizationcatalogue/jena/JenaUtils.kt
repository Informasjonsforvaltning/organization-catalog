package no.brreg.organizationcatalogue.jena

import no.brreg.organizationcatalogue.generated.model.Domain
import no.brreg.organizationcatalogue.generated.model.Organization
import no.brreg.organizationcatalogue.generated.model.PrefLabel
import no.brreg.organizationcatalogue.mapping.municipalityNumberToId
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.Resource
import org.apache.jena.sparql.vocabulary.FOAF
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.ORG
import org.apache.jena.vocabulary.RDF
import org.apache.jena.vocabulary.ROV
import org.apache.jena.vocabulary.SKOS
import java.io.ByteArrayOutputStream

fun Organization.jenaResponse(responseType: JenaType, urls: ExternalUrls): String =
    listOf(this).jenaResponse(responseType, urls)

fun List<Organization>.jenaResponse(responseType: JenaType, urls: ExternalUrls): String =
    createModel(urls).createResponseString(responseType)

private fun List<Organization>.createModel(urls: ExternalUrls): Model {
    val model = ModelFactory.createDefaultModel()
    model.setNsPrefix("dct", DCTerms.getURI())
    model.setNsPrefix("skos", SKOS.uri)
    model.setNsPrefix("foaf", FOAF.getURI())
    model.setNsPrefix("org", ORG.getURI())
    model.setNsPrefix("rov", ROV.getURI())
    model.setNsPrefix("adms", ADMS.uri)
    model.setNsPrefix("br", BR.uri)

    forEach {
        model.createResource(urls.organizationCatalogue + it.organizationId)
            .addProperty(RDF.type, FOAF.Organization)
            .safeAddProperty(ROV.legalName, it.name)
            .addRegistration(it)
            .safeAddProperty(ROV.orgType, it.orgType)
            .safeAddProperty(BR.orgPath, it.orgPath)
            .safeAddLinkedProperty(ORG.subOrganizationOf, it.subOrganizationOf?.let { parentId -> urls.organizationCatalogue + parentId })
            .safeAddLinkedProperty(BR.municipality, it.municipalityNumber?.let { number -> urls.municipality + municipalityNumberToId(number) })
            .safeAddLinkedProperty(BR.norwegianRegistry, it.norwegianRegistry)
            .safeAddLinkedProperty(BR.internationalRegistry, it.internationalRegistry)
            .safeAddProperty(BR.industryCode, it.industryCode)
            .safeAddProperty(BR.sectorCode, it.sectorCode)
            .addPreferredNames(it.prefLabel)
            .addDomains(it.domains)
    }

    return model
}

fun Organization.domainsJenaResponse(responseType: JenaType, urls: ExternalUrls): String {
    val model = ModelFactory.createDefaultModel()
    model.setNsPrefix("br", BR.uri)

    domains.forEach {
        model.createResource(urls.organizationDomains + it)
            .addProperty(RDF.type, BR.Domain)
    }

    return model.createResponseString(responseType)
}

fun List<Domain>.domainsJenaResponse(jenaType: JenaType, urls: ExternalUrls): String {
    val model = ModelFactory.createDefaultModel()
    model.setNsPrefix("br", BR.uri)

    forEach {
        val domainResource = model.createResource(urls.organizationDomains + it.name)
        domainResource.addProperty(RDF.type, BR.Domain)
        domainResource.addProperty(BR.domainName, it.name)

        it.organizations.forEach {org ->
            domainResource.addProperty(
                BR.holder,
                model.createResource(urls.organizationCatalogue + org)
            )
        }
    }

    return model.createResponseString(jenaType)
}

fun Domain.organizationsJenaResponse(jenaType: JenaType, urls: ExternalUrls): String {
    val model = ModelFactory.createDefaultModel()
    model.setNsPrefix("foaf", FOAF.getURI())

    organizations.forEach {
        model.createResource(urls.organizationCatalogue + it)
            .addProperty(RDF.type, FOAF.Organization)
    }

    return model.createResponseString(jenaType)
}

private fun Resource.addRegistration(org: Organization): Resource =
    addProperty(
        ROV.registration,
        model.createResource(ADMS.Identifier)
            .safeAddProperty(DCTerms.issued, org.issued?.toString())
            .addProperty(SKOS.notation, org.organizationId)
            .addProperty(ADMS.schemaAgency, "Brønnøysundregistrene"))

private fun Resource.safeAddProperty(property: Property, value: String?): Resource =
    if(value == null) this
    else addProperty(property, value)

private fun Resource.safeAddLinkedProperty(property: Property, value: String?): Resource =
    if(value == null) this
    else addProperty(property, model.createResource(value))

private fun Resource.addPreferredNames(preferredNames: PrefLabel?): Resource {
    if (preferredNames?.nb != null) addProperty(FOAF.name, preferredNames.nb, "nb")
    if (preferredNames?.nn != null) addProperty(FOAF.name, preferredNames.nn, "nn")
    if (preferredNames?.en != null) addProperty(FOAF.name, preferredNames.en, "en")
    return this
}

private fun Resource.addDomains(domains: List<String>): Resource {

    domains.forEach {
        addProperty(BR.domainName, it)
    }

    return this
}

private fun Model.createResponseString(responseType: JenaType):String =
    ByteArrayOutputStream().use{ out ->
        write(out, responseType.value)
        out.flush()
        out.toString("UTF-8")
    }

fun acceptHeaderToJenaType(accept: String?): JenaType =
    when (accept) {
        "text/turtle" -> JenaType.TURTLE
        "application/rdf+xml" -> JenaType.RDF_XML
        "application/rdf+json" -> JenaType.RDF_JSON
        "application/ld+json" -> JenaType.JSON_LD
        "application/xml" -> JenaType.NOT_JENA
        "application/json" -> JenaType.NOT_JENA
        else -> JenaType.NOT_ACCEPTABLE
    }

enum class JenaType(val value: String){
    TURTLE("TURTLE"),
    RDF_XML("RDF/XML"),
    RDF_JSON("RDF/JSON"),
    JSON_LD("JSON-LD"),
    NOT_JENA("NOT-JENA"),
    NOT_ACCEPTABLE("")
}

data class ExternalUrls(
    val organizationCatalogue: String? = null,
    val organizationDomains: String? = null,
    val municipality: String? = null
)