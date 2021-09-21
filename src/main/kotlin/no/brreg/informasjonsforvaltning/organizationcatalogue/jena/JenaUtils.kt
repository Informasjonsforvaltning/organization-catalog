package no.brreg.informasjonsforvaltning.organizationcatalogue.jena

import no.brreg.informasjonsforvaltning.organizationcatalogue.model.Organization
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.PrefLabel
import no.brreg.informasjonsforvaltning.organizationcatalogue.mapping.municipalityNumberToId
import no.brreg.informasjonsforvaltning.organizationcatalogue.model.OrgStatus
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.Resource
import org.apache.jena.sparql.vocabulary.FOAF
import org.apache.jena.vocabulary.*
import java.io.ByteArrayOutputStream
import java.net.URI

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
    model.setNsPrefix("orgstatus", ORGSTATUS.uri)
    model.setNsPrefix("orgtype", ORGTYPE.uri)

    forEach {
        model.createResource(urls.organizationCatalogue + it.organizationId)
            .addProperty(RDF.type, ROV.RegisteredOrganization)
            .safeAddProperty(ROV.legalName, it.name)
            .addRegistration(it)
            .safeAddProperty(DCTerms.identifier, it.organizationId)
            .addOrgType(it.orgType)
            .safeAddProperty(BR.orgPath, it.orgPath)
            .safeAddLinkedProperty(ORG.subOrganizationOf, it.subOrganizationOf?.let { parentId -> urls.organizationCatalogue + parentId })
            .safeAddLinkedProperty(BR.municipality, it.municipalityNumber?.let { number -> urls.municipality + municipalityNumberToId(number) })
            .safeAddLinkedProperty(BR.norwegianRegistry, it.norwegianRegistry)
            .safeAddLinkedProperty(BR.internationalRegistry, it.internationalRegistry)
            .safeAddProperty(BR.nace, it.industryCode)
            .safeAddProperty(BR.sectorCode, it.sectorCode)
            .safeAddHomepage(it.homepage)
            .addPreferredNames(it.prefLabel)
            .addOrgStatus(it.orgStatus)
    }

    return model
}

private fun Resource.addRegistration(org: Organization): Resource =
    addProperty(
        ROV.registration,
        model.createResource(ADMS.Identifier)
            .safeAddProperty(DCTerms.issued, org.issued?.toString())
            .addProperty(SKOS.notation, org.organizationId)
            .addProperty(ADMS.schemaAgency, "Brønnøysundregistrene"))

private fun Resource.addOrgType(orgType: String?): Resource =
    if (orgType == null) this
    else safeAddLinkedProperty(ROV.orgType, "${ORGTYPE.uri}$orgType")

private fun Resource.safeAddProperty(property: Property, value: String?): Resource =
    if (value == null) this
    else addProperty(property, value)

private fun Resource.safeAddLinkedProperty(property: Property, value: String?): Resource =
    if(value == null) this
    else addProperty(property, model.createResource(value))

private fun String.isWellFormedIRI(): Boolean =
    try {
        URI.create(this).isAbsolute
    } catch (ex: Exception) {
        false
    }

private fun Resource.safeAddHomepage(value: String?): Resource =
    when {
        value == null -> this
        value.isWellFormedIRI() -> addProperty(FOAF.homepage, model.createResource(value))
        "http://$value".isWellFormedIRI() -> addProperty(FOAF.homepage, model.createResource("http://$value"))
        else -> this
    }

private fun Resource.addPreferredNames(preferredNames: PrefLabel?): Resource {
    if (preferredNames?.nb != null) addProperty(FOAF.name, preferredNames.nb, "nb")
    if (preferredNames?.nn != null) addProperty(FOAF.name, preferredNames.nn, "nn")
    if (preferredNames?.en != null) addProperty(FOAF.name, preferredNames.en, "en")
    return this
}

private fun Resource.addOrgStatus(orgStatus: OrgStatus?): Resource {
    when (orgStatus) {
        null, OrgStatus.NORMAL -> addProperty(ROV.orgStatus, ORGSTATUS.NormalAktivitet)
        else -> addProperty(ROV.orgStatus, ORGSTATUS.Avviklet)
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
    when {
        accept == null -> JenaType.NOT_JENA
        accept.contains("text/turtle") -> JenaType.TURTLE
        accept.contains("application/rdf+xml") -> JenaType.RDF_XML
        accept.contains("application/rdf+json") -> JenaType.RDF_JSON
        accept.contains("application/ld+json") -> JenaType.JSON_LD
        accept.contains("application/xml") -> JenaType.NOT_JENA
        accept.contains("application/json") -> JenaType.NOT_JENA
        accept.contains("*/*") -> JenaType.NOT_JENA
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
