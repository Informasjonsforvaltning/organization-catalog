package no.publishers;

import no.publishers.generated.model.Publisher;
import no.publishers.graphql.CreatePublisher;
import no.publishers.model.PublisherDB;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestData {

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
        newPublisherDB.setPrefLabel("prefLabel");

        return newPublisherDB;
    }

    public static CreatePublisher publisherInput = new CreatePublisher(
        "name", "uri", "orgId", "orgPath", "prefLabel"
    );
}
