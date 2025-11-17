package no.digdir.organizationcatalog.jena

import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Resource

class ORGSTATUS {
    companion object {
        private val m = ModelFactory.createDefaultModel()

        const val URI =
            "https://raw.githubusercontent.com/Informasjonsforvaltning/organization-catalog/main/src/main/resources/ontology/org-status.ttl#"

        val NormalAktivitet: Resource = m.createResource(URI + "NormalAktivitet")
        val Avviklet: Resource = m.createResource(URI + "Avviklet")
    }
}
