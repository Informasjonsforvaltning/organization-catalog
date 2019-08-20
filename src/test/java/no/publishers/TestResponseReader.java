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
        String expected = "@prefix br:    <http://data.brreg.no/informasjonsmodeller/enhetsregisteret/> .\n" +
            "@prefix dct:   <http://purl.org/dc/terms/> .\n" +
            "@prefix adms:  <http://www.w3.org/ns/adms#> .\n" +
            "@prefix org:   <http://www.w3.org/ns/org#> .\n" +
            "@prefix rov:   <http://www.w3.org/ns/regorg#> .\n" +
            "@prefix skos:  <http://www.w3.org/2004/02/skos/core#> .\n" +
            "@prefix foaf:  <http://xmlns.com/foaf/0.1/> .\n" +
            "\n" +
            "<uri>   a                      foaf:Organization ;\n" +
            "        br:industryCode        <industryUri> ;\n" +
            "        br:municipalityNumber  <0456> ;\n" +
            "        br:orgPath             \"/STAT/123/456\" ;\n" +
            "        br:sectorCode          <sectorUri> ;\n" +
            "        dct:identifier         \"" + id + "\" ;\n" +
            "        org:subOrganizationOf  <http://data.brreg.no/enhetsregisteret/enhet/98765421> ;\n" +
            "        rov:legalName          \"toBeUpdated\" ;\n" +
            "        rov:orgType            \"orgForm\" ;\n" +
            "        rov:registration       [ a                  adms:Identifier ;\n" +
            "                                 dct:issued         \"1999-02-03\" ;\n" +
            "                                 skos:notation      \"orgId\" ;\n" +
            "                                 adms:schemaAgency  \"Brønnøysundregistrene\"\n" +
            "                               ] ;\n" +
            "        foaf:name              \"nnLabel\"@nn , \"nbLabel\"@nb , \"enLabel\"@en .\n";

        Model responseModel = ModelFactory.createDefaultModel();
        responseModel.read(new StringReader(expected), "", "text/turtle");

        return responseModel;
    }

    public Model getExpectedFromUpdate(String id) {
        String expected = "@prefix br:    <http://data.brreg.no/informasjonsmodeller/enhetsregisteret/> .\n" +
            "@prefix dct:   <http://purl.org/dc/terms/> .\n" +
            "@prefix adms:  <http://www.w3.org/ns/adms#> .\n" +
            "@prefix org:   <http://www.w3.org/ns/org#> .\n" +
            "@prefix rov:   <http://www.w3.org/ns/regorg#> .\n" +
            "@prefix skos:  <http://www.w3.org/2004/02/skos/core#> .\n" +
            "@prefix foaf:  <http://xmlns.com/foaf/0.1/> .\n" +
            "\n" +
            "<uri>   a                      foaf:Organization ;\n" +
            "        br:industryCode        <industryUri> ;\n" +
            "        br:municipalityNumber  <0456> ;\n" +
            "        br:orgPath             \"/STAT/123/456\" ;\n" +
            "        br:sectorCode          <sectorUri> ;\n" +
            "        dct:identifier         \"" + id + "\" ;\n" +
            "        org:subOrganizationOf  <http://data.brreg.no/enhetsregisteret/enhet/98765421> ;\n" +
            "        rov:legalName          \"updatedName\" ;\n" +
            "        rov:orgType            \"orgForm\" ;\n" +
            "        rov:registration       [ a                  adms:Identifier ;\n" +
            "                                 dct:issued         \"1999-02-03\" ;\n" +
            "                                 skos:notation      \"orgId\" ;\n" +
            "                                 adms:schemaAgency  \"Brønnøysundregistrene\"\n" +
            "                               ] ;\n" +
            "        foaf:name              \"nnLabel\"@nn , \"nbLabel\"@nb , \"enLabel\"@en .\n";

        Model responseModel = ModelFactory.createDefaultModel();
        responseModel.read(new StringReader(expected), "", "text/turtle");

        return responseModel;
    }
}
