package no.publishers.jena

import no.publishers.generated.model.PrefLabel
import no.publishers.generated.model.Publisher
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

fun Publisher.jenaResponse(acceptHeader: String?): String =
    listOf(this)
        .createModel()
        .createResponseString(acceptHeaderToJenaType(acceptHeader))

fun List<Publisher>.jenaResponse(acceptHeader: String?): String =
    createModel()
        .createResponseString(acceptHeaderToJenaType(acceptHeader))

private fun List<Publisher>.createModel(): Model {
    val model = ModelFactory.createDefaultModel()
    model.setNsPrefix("dct", DCTerms.getURI())
    model.setNsPrefix("skos", SKOS.uri)
    model.setNsPrefix("foaf", FOAF.getURI())
    model.setNsPrefix("org", ORG.getURI())
    model.setNsPrefix("rov", ROV.getURI())
    model.setNsPrefix("adms", ADMS.uri)
    model.setNsPrefix("br", BR.uri)

    forEach {
        model.createResource(it.uri)
            .addProperty(RDF.type, FOAF.Organization)
            .addProperty(DCTerms.identifier, it.id)
            .safeAddProperty(ROV.legalName, it.name)
            .addRegistration(it)
            .safeAddProperty(ROV.orgType, it.orgType)
            .safeAddProperty(BR.orgPath, it.orgPath)
            .safeAddLinkedProperty(ORG.subOrganizationOf, it.subOrganizationOf)
            .safeAddLinkedProperty(BR.municipalityNumber, it.uriMunicipalityNumber)
            .safeAddLinkedProperty(BR.industryCode, it.uriIndustryCode)
            .safeAddLinkedProperty(BR.sectorCode, it.uriSectorCode)
            .addPreferredNames(it.prefLabel)
    }

    return model
}

private fun Resource.addRegistration(publisher: Publisher): Resource =
    addProperty(
        ROV.registration,
        model.createResource(ADMS.Identifier)
            .safeAddProperty(DCTerms.issued, publisher.issued?.toString())
            .addProperty(SKOS.notation, publisher.organizationId)
            .addProperty(ADMS.schemaAgency, "Brønnøysundregistrene"))

private fun Resource.safeAddProperty(property: Property, value: String?): Resource =
    if(value == null) this
    else addProperty(property, value)

private fun Resource.safeAddLinkedProperty(property: Property, value: String?): Resource =
    if(value == null) this
    else addProperty(property, model.createResource(value))

private fun Resource.addPreferredNames(preferredNames: PrefLabel): Resource {
    if (preferredNames.nb != null) addProperty(FOAF.name, preferredNames.nb, "nb")
    if (preferredNames.nn != null) addProperty(FOAF.name, preferredNames.nn, "nn")
    if (preferredNames.en != null) addProperty(FOAF.name, preferredNames.en, "en")
    return this
}
private fun Model.createResponseString(responseType: String):String =
    ByteArrayOutputStream().use{ out ->
        write(out, responseType)
        out.flush()
        out.toString("UTF-8")
    }

private fun acceptHeaderToJenaType(accept: String?): String =
    when (accept) {
        "text/turtle" -> "TURTLE"
        "application/rdf+xml" -> "RDF/XML"
        "application/ld+json" -> "JSON-LD"
        else -> throw MissingAcceptHeaderException()
    }

class MissingAcceptHeaderException(): Exception()