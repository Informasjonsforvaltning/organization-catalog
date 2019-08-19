package no.publishers.jena.adms

import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.Resource

class ADMS {
    companion object {
        private val m = ModelFactory.createDefaultModel()

        val uri = "http://www.w3.org/ns/adms#"

        val Identifier: Resource = m.createResource(uri + "Identifier")

        val schemaAgency: Property = m.createProperty(uri + "schemaAgency")
    }
}
