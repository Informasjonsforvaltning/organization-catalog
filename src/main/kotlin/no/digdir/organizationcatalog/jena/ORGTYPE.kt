package no.digdir.organizationcatalog.jena

import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.Resource

class ORGTYPE {
    companion object {
        private val m = ModelFactory.createDefaultModel()

        val uri = "https://raw.githubusercontent.com/Informasjonsforvaltning/organization-catalog/main/src/main/resources/ontology/org-type.ttl#"
    }
}
