package no.brreg.organizationcatalogue.jena

import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.Resource

class BR {
    companion object {
        private val m = ModelFactory.createDefaultModel()

        val uri = "http://data.brreg.no/informasjonsmodeller/enhetsregisteret/"

        val municipality: Property = m.createProperty(uri + "municipality")
        val orgPath: Property = m.createProperty(uri + "orgPath")
        val industryCode: Property = m.createProperty(uri + "industryCode")
        val sectorCode: Property = m.createProperty(uri + "sectorCode")
        val norwegianRegistry: Property = m.createProperty(uri + "norwegianRegistry")
        val internationalRegistry: Property = m.createProperty(uri + "internationalRegistry")

        val Domain: Resource = m.createResource(uri + "Domain")
        val domainName: Property = m.createProperty(uri + "domainName")
        val holder: Property = m.createProperty(uri + "holder")
    }
}