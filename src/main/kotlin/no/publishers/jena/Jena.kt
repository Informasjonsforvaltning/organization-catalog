package no.publishers.jena

import no.publishers.generated.model.PrefLabel
import no.publishers.generated.model.Publisher
import no.publishers.jena.adms.ADMS
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.Resource
import org.apache.jena.sparql.vocabulary.FOAF
import org.apache.jena.vocabulary.DCAT
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.RDF
import org.apache.jena.vocabulary.ROV
import org.apache.jena.vocabulary.SKOS
import org.apache.jena.vocabulary.SKOSXL
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
    model.setNsPrefix("dcat", DCAT.getURI())
    model.setNsPrefix("skosxl", SKOSXL.uri)
    model.setNsPrefix("skos", SKOS.uri)
    model.setNsPrefix("foaf", FOAF.getURI())
    model.setNsPrefix("rov", ROV.getURI())
    model.setNsPrefix("adms", ADMS.uri)

    forEach {
        model.createResource(it.uri)
            .addProperty(RDF.type, model.createResource(FOAF.Organization))
            .addProperty(DCTerms.identifier, it.id)
            .safeAddProperty(FOAF.name, it.name)
            .addProperty(
                ROV.registration,
                model.createResource(ADMS.Identifier)
                    .safeAddProperty(DCTerms.issued, it.issued?.toString())
                    .addProperty(SKOS.notation, it.organizationId)
                    .addProperty(ADMS.schemaAgency, "Brønnøysundregistrene"))
            .safeAddProperty(DCTerms.format, it.orgForm)
            .safeAddProperty(SKOS.mappingRelation, it.orgPath)
            .safeAddProperty(DCTerms.isPartOf, it.orgParent)
            .safeAddProperty(SKOS.historyNote, it.municipalityNumber)
            .addProperty(
                FOAF.interest,
                model.createResource(SKOSXL.Label)
                    .safeAddProperty(DCAT.accessURL, it.industryCode?.uri)
                    .safeAddProperty(FOAF.nick, it.industryCode?.code)
                    .addProperty(
                        SKOSXL.prefLabel,
                        model.createResource(SKOSXL.Label).addPrefLabels(it.industryCode?.prefLabel ?: PrefLabel())))
            .addProperty(
                FOAF.schoolHomepage,
                model.createResource(SKOSXL.Label)
                    .safeAddProperty(DCAT.accessURL, it.sectorCode?.uri)
                    .safeAddProperty(FOAF.nick, it.sectorCode?.code)
                    .addProperty(
                        SKOSXL.prefLabel,
                        model.createResource(SKOSXL.Label).addPrefLabels(it.sectorCode?.prefLabel ?: PrefLabel())))
            .addProperty(
                SKOSXL.prefLabel,
                model.createResource(SKOSXL.Label).addPrefLabels(it.prefLabel))
    }

    return model
}

private fun Resource.safeAddProperty(property: Property, value: String?): Resource =
    if(value == null) this
    else addProperty(property, value)

private fun Resource.addPrefLabels(prefLabel: PrefLabel):Resource {
    if (prefLabel.nb != null) addProperty(SKOSXL.literalForm, prefLabel.nb, "nb")
    if (prefLabel.nn != null) addProperty(SKOSXL.literalForm, prefLabel.nn, "nn")
    if (prefLabel.en != null) addProperty(SKOSXL.literalForm, prefLabel.en, "en")
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