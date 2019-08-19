package no.publishers.jena

import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Property

class BR {
    companion object {
        private val m = ModelFactory.createDefaultModel()

        val uri = "http://data.brreg.no/informasjonsmodeller/enhetsregisteret/"

        val organisasjonsform: Property = m.createProperty(uri + "organisasjonsform")
        val overordnetEnhet: Property = m.createProperty(uri + "overordnetEnhet")
        val kommunenummer: Property = m.createProperty(uri + "kommunenummer")
        val orgPath: Property = m.createProperty(uri + "orgPath")
        val naeringskode: Property = m.createProperty(uri + "naeringskode")
        val sektorkode: Property = m.createProperty(uri + "sektorkode")
    }
}