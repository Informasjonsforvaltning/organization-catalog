package no.publishers;

import no.publishers.generated.model.Publisher;
import no.publishers.graphql.CreatePublisher;
import no.publishers.model.PublisherDB;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestData {
    private static final String MONGO_USER = "testuser";
    private static final String MONGO_PASSWORD = "testpassword";

    public static final String API_SERVICE_NAME = "publisher";
    public static final String MONGO_SERVICE_NAME = "mongodb";
    public static final int API_PORT = 8080;
    public static final int MONGO_PORT = 27017;

    public static Publisher publisher = new Publisher();
    public static List<Publisher> emptyPublisherList = new ArrayList<>();
    public static List<Publisher> publishers = Collections.singletonList(publisher);
    public static PublisherDB persistedPublisher = createPublisherDB();

    private static PublisherDB createPublisherDB() {
        PublisherDB newPublisherDB = new PublisherDB();

        newPublisherDB.setId(new ObjectId());
        newPublisherDB.setName("name");
        newPublisherDB.setUri("uri");
        newPublisherDB.setOrganizationId("orgId");
        newPublisherDB.setOrgPath("orgPath");
        newPublisherDB.setNbPrefLabel("nbLabel");
        newPublisherDB.setNnPrefLabel("nnLabel");
        newPublisherDB.setEnPrefLabel("enLabel");

        return newPublisherDB;
    }

    public static String TEST_COMPOSE = "version: \"2.0\"\n" +
        "\n" +
        "services:\n" +
        "  " + API_SERVICE_NAME + ":\n" +
        "    image: brreg/publishers-api:latest\n" +
        "    environment:\n" +
        "      - PUBAPI_MONGO_USERNAME=" + MONGO_USER + "\n" +
        "      - PUBAPI_MONGO_PASSWORD=" + MONGO_PASSWORD + "\n" +
        "    depends_on:\n" +
        "      - mongodb\n" +
        "\n" +
        "  " + MONGO_SERVICE_NAME + ":\n" +
        "    image: mongo:latest\n" +
        "    environment:\n" +
        "      - MONGO_INITDB_ROOT_USERNAME=" + MONGO_USER + "\n" +
        "      - MONGO_INITDB_ROOT_PASSWORD=" + MONGO_PASSWORD + "\n";

    public static String createPublisherJson(CreatePublisher input) {
        return "{\n" +
            "\"name\": \"" + input.getName() + "\",\n" +
            "\"organizationId\": \"" + input.getOrganizationId() + "\",\n" +
            "\"orgPath\": \"" + input.getOrgPath() + "\",\n" +
            "\"nbPrefLabel\": \"" + input.getNbPrefLabel() + "\",\n" +
            "\"nnPrefLabel\": \"" + input.getNnPrefLabel() + "\",\n" +
            "\"enPrefLabel\": \"" + input.getEnPrefLabel() + "\",\n" +
            "\"uri\": \"" + input.getUri() + "\"\n" +
            "}";
    }

    public static final CreatePublisher CREATE_PUBLISHER_0 = new CreatePublisher(
        "name", "uri", "1234", "orgPath", "nbLabel", "nnLabel", "enLabel"
    );

    public static final CreatePublisher CREATE_PUBLISHER_1 = new CreatePublisher(
        "test", "uri1", "3456", "orgPath1", "nbLabel1", "nnLabel1", "enLabel1"
    );

    public static final CreatePublisher CREATE_PUBLISHER_2 = new CreatePublisher(
        "name2", "uri2", "abc", "orgPath2", "nbLabel2", "nnLabel2", "enLabel2"
    );

    public static String publisherReturnJson(CreatePublisher input, String id) {
        return "{\"id\":\"" + id + "\"," +
            "\"uri\":\"" + input.getUri() + "\"," +
            "\"organizationId\":\"" + input.getOrganizationId() + "\"," +
            "\"name\":\"" + input.getName() + "\"," +
            "\"orgPath\":\"" + input.getOrgPath() + "\"," +
            "\"prefLabel\":{\"nb\":\"" + input.getNbPrefLabel() + "\",\"nn\":\"" + input.getNnPrefLabel() + "\",\"en\":\"" + input.getEnPrefLabel() + "\"}}";
    }
}
