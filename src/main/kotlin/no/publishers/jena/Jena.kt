package no.publishers.jena

import no.publishers.generated.model.PrefLabel
import no.publishers.generated.model.Publisher
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Resource
import org.apache.jena.vocabulary.DCAT
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.RDF
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
    val modelList = ModelFactory.createDefaultModel()
    modelList.setNsPrefix("dct", DCTerms.getURI())
    modelList.setNsPrefix("dcat", DCAT.getURI())
    modelList.setNsPrefix("skosxl", SKOSXL.uri)
    modelList.setNsPrefix("skos", SKOS.uri)

    forEach {
        modelList.createResource(it.uri)
            .addProperty(RDF.type, modelList.createResource("http://purl.org/dc/terms/publisher"))
            .addProperty(DCTerms.identifier, it.id)
            .addProperty(DCTerms.title, it.name)
            .addProperty(SKOS.altLabel, it.organizationId)
            .addProperty(SKOS.note, it.orgPath)
            .addProperty(
                SKOSXL.prefLabel,
                modelList.createResource(SKOSXL.Label).addPrefLabels(it.prefLabel))
    }

    return modelList
}

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