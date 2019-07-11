package no.publishers;

import com.google.common.collect.ImmutableMap;
import no.publishers.generated.model.PrefLabel;
import no.publishers.generated.model.Publisher;
import no.publishers.model.PublisherDB;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestData {
    private static final String MONGO_USER = "testuser";
    private static final String MONGO_PASSWORD = "testpassword";
    private static final String MONGO_AUTH = "?authSource=admin&authMechanism=SCRAM-SHA-1";
    public static final int MONGO_PORT = 27017;
    public static final String DATABASE_NAME = "publisherAPI";

    public static final Map<String, String> MONGO_ENV_VALUES = ImmutableMap.of(
        "MONGO_INITDB_ROOT_USERNAME", MONGO_USER,
        "MONGO_INITDB_ROOT_PASSWORD", MONGO_PASSWORD);

    public static String buildMongoURI(String host, int port, boolean withDbName) {
        String uri = "mongodb://" + MONGO_USER + ":" + MONGO_PASSWORD + "@" + host + ":" + port + "/";

        if(withDbName) {
            uri += DATABASE_NAME;
        }

        return uri + MONGO_AUTH;
    }

    public static final Publisher PUBLISHER_0 = createPublisher("name", "1234");
    public static final Publisher PUBLISHER_1 = createPublisher("test", "3456");
    public static final Publisher PUBLISHER_2 = createPublisher("name2", "abc");
    public static final Publisher PUBLISHER_3 = createPublisher("toBeUpdated", "qwerty");

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
}
