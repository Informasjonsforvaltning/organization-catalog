package no.publishers;

import no.publishers.generated.model.Publisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestData {

    public static Publisher publisher = new Publisher();

    public static List<Publisher> emptyPublisherList = new ArrayList<>();

    public static List<Publisher> publishers = Collections.singletonList(publisher);
}
