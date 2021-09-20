package no.brreg.informasjonsforvaltning.organizationcatalogue.jena

import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.Resource

class ORGSTATUS {
    companion object {
        private val m = ModelFactory.createDefaultModel()

        val uri = "https://raw.githubusercontent.com/Informasjonsforvaltning/organization-catalogue/master/src/main/resources/ontology/org-status.ttl#"

        val NormalAktivitet: Resource = m.createResource(uri + "NormalAktivitet")
        val Avviklet: Resource = m.createResource(uri + "Avviklet")
    }
}
