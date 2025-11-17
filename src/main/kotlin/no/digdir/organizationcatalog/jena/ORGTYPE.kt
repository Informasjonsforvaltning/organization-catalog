package no.digdir.organizationcatalog.jena

import org.apache.jena.rdf.model.ModelFactory

class ORGTYPE {
    companion object {
        private val m = ModelFactory.createDefaultModel()

        const val URI =
            "https://raw.githubusercontent.com/Informasjonsforvaltning/organization-catalog/main/src/main/resources/ontology/org-type.ttl#"
    }
}
