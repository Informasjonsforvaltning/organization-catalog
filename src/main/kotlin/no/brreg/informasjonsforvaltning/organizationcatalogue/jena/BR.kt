package no.brreg.informasjonsforvaltning.organizationcatalogue.jena

import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.Resource

class BR {
    companion object {
        private val m = ModelFactory.createDefaultModel()

        val uri = "https://github.com/Informasjonsforvaltning/organization-catalogue/blob/develop/src/main/resources/ontology/organization-catalogue.owl#"

        val municipality: Property = m.createProperty(uri + "municipality")
        val orgPath: Property = m.createProperty(uri + "orgPath")
        val sectorCode: Property = m.createProperty(uri + "sectorCode")
        val norwegianRegistry: Property = m.createProperty(uri + "norwegianRegistry")
        val internationalRegistry: Property = m.createProperty(uri + "internationalRegistry")

        val Domain: Resource = m.createResource(uri + "Domain")
        val domainName: Property = m.createProperty(uri + "domainName")
        val domainHolder: Property = m.createProperty(uri + "domainHolder")
    }
}