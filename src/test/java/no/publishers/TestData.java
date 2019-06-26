package no.publishers;

import no.publishers.generated.model.PrefLabel;
import no.publishers.generated.model.Publisher;
import no.publishers.model.PublisherDB;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestData {
    private static final String MONGO_USER = "testuser";
    private static final String MONGO_PASSWORD = "testpassword";

    public static final String API_SERVICE_NAME = "publisher";
    public static final String MONGO_SERVICE_NAME = "mongodb";
    public static final int API_PORT = 8080;
    public static final int MONGO_PORT = 27017;

    public static final Publisher PUBLISHER_0 = createPublisher("name", "1234");
    public static final Publisher PUBLISHER_1 = createPublisher("test", "3456");
    public static final Publisher PUBLISHER_2 = createPublisher("name2", "abc");

    public static List<Publisher> PUBLISHERS = Arrays.asList(PUBLISHER_0, PUBLISHER_1, PUBLISHER_2);
    public static List<Publisher> EMPTY_PUBLISHERS = Collections.emptyList();

    public static PublisherDB PUBLISHER_DB = createPublisherDB();

    private static PublisherDB createPublisherDB() {
        PublisherDB newPublisherDB = new PublisherDB();

        newPublisherDB.setId(new ObjectId());
        newPublisherDB.setName("name");
        newPublisherDB.setUri("uri");
        newPublisherDB.setOrganizationId("orgId");
        newPublisherDB.setOrgPath("orgPath");

        PrefLabel prefLabel = new PrefLabel();
        prefLabel.setNb("nbLabel");
        prefLabel.setNn("nnLabel");
        prefLabel.setEn("enLabel");
        newPublisherDB.setPrefLabel(prefLabel);

        return newPublisherDB;
    }

    private static Publisher createPublisher(String name, String orgId) {
        Publisher publisher = new Publisher();

        publisher.setName(name);
        publisher.setOrganizationId(orgId);
        publisher.setOrgPath("orgPath");
        publisher.setUri("uri");

        PrefLabel labels = new PrefLabel();
        labels.setNb("labelNB");
        labels.setNn("labelNN");
        labels.setEn("labelEN");
        publisher.setPrefLabel(labels);

        return publisher;
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
}
