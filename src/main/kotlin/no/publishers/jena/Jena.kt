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

fun Publisher.createModel(): Model {
    val model = ModelFactory.createDefaultModel()
    model.setNsPrefix("dct", DCTerms.getURI())
    model.setNsPrefix("dcat", DCAT.getURI())
    model.setNsPrefix("skosxl", SKOSXL.uri)
    model.setNsPrefix("skos", SKOS.uri)
    model.createResource(uri)
        .addProperty(RDF.type, model.createResource("http://purl.org/dc/terms/publisher"))
        .addProperty(DCTerms.identifier, id)
        .addProperty(DCTerms.title, name)
        .addProperty(SKOS.altLabel, organizationId)
        .addProperty(SKOS.note, orgPath)
        .addProperty(
            SKOSXL.prefLabel,
            model.createResource(SKOSXL.Label).addPrefLabels(prefLabel))

    return model
}

fun List<Publisher>.createListModel(): Model {
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

fun Resource.addPrefLabels(prefLabel: PrefLabel):Resource {
    if (prefLabel.nb != null) addProperty(SKOSXL.literalForm, prefLabel.nb, "nb")
    if (prefLabel.nn != null) addProperty(SKOSXL.literalForm, prefLabel.nn, "nn")
    if (prefLabel.en != null) addProperty(SKOSXL.literalForm, prefLabel.en, "en")
    return this
}
fun Model.createResponseString(responseType: String):String =
    ByteArrayOutputStream().use{ out ->
        write(out, responseType)
        out.flush()
        out.toString("UTF-8")
    }