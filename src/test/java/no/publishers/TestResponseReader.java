package no.publishers;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public class TestResponseReader {

    private Reader resourceAsReader(final String resourceName) {
        return new InputStreamReader(getClass().getClassLoader().getResourceAsStream(resourceName), StandardCharsets.UTF_8);
    }

    public Model getExpectedResponse(String filename) {
        Model expected = ModelFactory.createDefaultModel();
        expected.read(resourceAsReader("responses/" + filename + ".ttl"), "", "text/turtle");
        return expected;
    }

    public Model parseResponse(String response) {
        Model responseModel = ModelFactory.createDefaultModel();
        responseModel.read(new StringReader(response), "", "text/turtle");
        return responseModel;
    }

    public Model getExpectedFromCreate(String id) {
        String expected = "@prefix dct:   <http://purl.org/dc/terms/> .\n" +
            "@prefix skosxl: <http://www.w3.org/2008/05/skos-xl#> .\n" +
            "@prefix skos:  <http://www.w3.org/2004/02/skos/core#> .\n" +
            "@prefix dcat:  <http://www.w3.org/ns/dcat#> .\n" +
            "\n" +
            "<uri>   a                 dct:publisher ;\n" +
            "        dct:identifier    \"" + id + "\" ;\n" +
            "        dct:title         \"toBeUpdated\" ;\n" +
            "        skos:altLabel     \"orgId\" ;\n" +
            "        skos:note         \"orgPath\" ;\n" +
            "        skosxl:prefLabel  [ a                   skosxl:Label ;\n" +
            "                            skosxl:literalForm  \"enLabel\"@en , \"nnLabel\"@nn , \"nbLabel\"@nb\n" +
            "                          ] .\n";

        Model responseModel = ModelFactory.createDefaultModel();
        responseModel.read(new StringReader(expected), "", "text/turtle");

        return responseModel;
    }

    public Model getExpectedFromUpdate(String id) {
        String expected = "@prefix dct:   <http://purl.org/dc/terms/> .\n" +
            "@prefix skosxl: <http://www.w3.org/2008/05/skos-xl#> .\n" +
            "@prefix skos:  <http://www.w3.org/2004/02/skos/core#> .\n" +
            "@prefix dcat:  <http://www.w3.org/ns/dcat#> .\n" +
            "\n" +
            "<uri>   a                 dct:publisher ;\n" +
            "        dct:identifier    \"" + id + "\" ;\n" +
            "        dct:title         \"updatedName\" ;\n" +
            "        skos:altLabel     \"orgId\" ;\n" +
            "        skos:note         \"orgPath\" ;\n" +
            "        skosxl:prefLabel  [ a                   skosxl:Label ;\n" +
            "                            skosxl:literalForm  \"enLabel\"@en , \"nnLabel\"@nn , \"nbLabel\"@nb\n" +
            "                          ] .\n";

        Model responseModel = ModelFactory.createDefaultModel();
        responseModel.read(new StringReader(expected), "", "text/turtle");

        return responseModel;
    }
}
