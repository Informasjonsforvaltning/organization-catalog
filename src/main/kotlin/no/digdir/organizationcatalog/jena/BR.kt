package no.digdir.organizationcatalog.jena

import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Property

class BR {
    companion object {
        private val m = ModelFactory.createDefaultModel()

        const val URI =
            "https://raw.githubusercontent.com/Informasjonsforvaltning/organization-catalog/main/src/main/resources/ontology/organization-catalog.owl#"

        val municipality: Property = m.createProperty(URI + "municipality")
        val orgPath: Property = m.createProperty(URI + "orgPath")
        val nace: Property = m.createProperty(URI + "nace")
        val sectorCode: Property = m.createProperty(URI + "sectorCode")
        val norwegianRegistry: Property = m.createProperty(URI + "norwegianRegistry")
        val internationalRegistry: Property = m.createProperty(URI + "internationalRegistry")
    }
}
