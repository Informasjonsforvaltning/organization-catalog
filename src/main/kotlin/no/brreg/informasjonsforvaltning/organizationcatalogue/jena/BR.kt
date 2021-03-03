package no.brreg.informasjonsforvaltning.organizationcatalogue.jena

import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.Resource

class BR {
    companion object {
        private val m = ModelFactory.createDefaultModel()

        val uri = "https://raw.githubusercontent.com/Informasjonsforvaltning/organization-catalogue/master/src/main/resources/ontology/organization-catalogue.owl#"

        val municipality: Property = m.createProperty(uri + "municipality")
        val orgPath: Property = m.createProperty(uri + "orgPath")
        val nace: Property = m.createProperty(uri + "nace")
        val sectorCode: Property = m.createProperty(uri + "sectorCode")
        val norwegianRegistry: Property = m.createProperty(uri + "norwegianRegistry")
        val internationalRegistry: Property = m.createProperty(uri + "internationalRegistry")
    }
}
