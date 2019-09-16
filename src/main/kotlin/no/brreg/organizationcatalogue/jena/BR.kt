package no.brreg.organizationcatalogue.jena

import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Property

class BR {
    companion object {
        private val m = ModelFactory.createDefaultModel()

        val uri = "http://data.brreg.no/informasjonsmodeller/enhetsregisteret/"

        val municipalityNumber: Property = m.createProperty(uri + "municipalityNumber")
        val orgPath: Property = m.createProperty(uri + "orgPath")
        val industryCode: Property = m.createProperty(uri + "industryCode")
        val sectorCode: Property = m.createProperty(uri + "sectorCode")
    }
}