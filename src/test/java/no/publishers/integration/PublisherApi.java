package no.publishers.integration;

import no.publishers.TestResponseReader;
import no.publishers.controller.PublishersApiImpl;
import no.publishers.generated.model.PrefLabel;
import no.publishers.generated.model.Publisher;
import no.publishers.repository.PublisherRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RIOT;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.servlet.http.HttpServletRequest;

import static no.publishers.TestDataKt.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@ContextConfiguration(initializers = {PublisherApi.Initializer.class})
@Tag("service")
class PublisherApi {
    private final static Logger logger = LoggerFactory.getLogger(PublisherApi.class);
    private static Slf4jLogConsumer mongoLog = new Slf4jLogConsumer(logger).withPrefix("mongo-container");
    private TestResponseReader responseReader = new TestResponseReader();

    @Mock
    private static HttpServletRequest httpServletRequestMock;

    @Autowired
    private PublishersApiImpl publishersApiImpl;

    @Container
    private static final GenericContainer mongoContainer = new GenericContainer("mongo:latest")
        .withEnv(getMONGO_ENV_VALUES())
        .withLogConsumer(mongoLog)
        .withExposedPorts(MONGO_PORT)
        .waitingFor(Wait.forListeningPort());

    static class Initializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                "spring.data.mongodb.database=" + DATABASE_NAME,
                "spring.data.mongodb.uri=" + buildMongoURI(mongoContainer.getContainerIpAddress(), mongoContainer.getMappedPort(MONGO_PORT), false)
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @BeforeAll
    static void setup(@Autowired PublisherRepository repository) {
        RIOT.init();
        repository.save(getPUBLISHER_DB_0());
        repository.save(getPUBLISHER_DB_1());
        repository.save(getPUBLISHER_DB_2());
    }

    @Test
    void pingTest() {
        String response = publishersApiImpl.ping().getBody();
        assertEquals("pong", response);
    }

    @Test
    void getById() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("text/turtle");

        String response0 = publishersApiImpl
            .getPublisherById(httpServletRequestMock, getPUBLISHER_0().getId())
            .getBody();

        String response1 = publishersApiImpl
            .getPublisherById(httpServletRequestMock, getPUBLISHER_1().getId())
            .getBody();

        String response2 = publishersApiImpl
            .getPublisherById(httpServletRequestMock, getPUBLISHER_2().getId())
            .getBody();

        Model modelFromResponse0 = responseReader.parseResponse(response0, "text/turtle");
        Model expectedResponse0 = responseReader.getExpectedResponse("getOne.ttl", "TURTLE");

        Model modelFromResponse1 = responseReader.parseResponse(response1, "text/turtle");
        Model expectedResponse1 = responseReader.getExpectedResponse("getOne1.ttl", "TURTLE");

        Model modelFromResponse2 = responseReader.parseResponse(response2, "text/turtle");
        Model expectedResponse2 = responseReader.getExpectedResponse("getOne2.ttl", "TURTLE");

        assertTrue(expectedResponse0.isIsomorphicWith(modelFromResponse0));
        assertTrue(expectedResponse1.isIsomorphicWith(modelFromResponse1));
        assertTrue(expectedResponse2.isIsomorphicWith(modelFromResponse2));
    }

    @Test
    void getByNameSeveralPossibilities() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("text/turtle");

        String response = publishersApiImpl
            .getPublishers(httpServletRequestMock, "ET", null)
            .getBody();

        Model modelFromResponse = responseReader.parseResponse(response, "TURTLE");
        Model expectedResponse = responseReader.getExpectedResponse("searchByName.ttl", "TURTLE");

        assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
    }

    @Test
    void getByNameSingle() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("application/ld+json");

        String response = publishersApiImpl
            .getPublishers(httpServletRequestMock, "FORSVARET", null)
            .getBody();

        Model modelFromResponse = responseReader.parseResponse(response, "JSONLD");
        Model expectedResponse = responseReader.getExpectedResponse("getOne2.ttl", "TURTLE");

        assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
    }

    @Test
    void getByOrgidSeveralPossibilities() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("text/turtle");

        String response = publishersApiImpl
            .getPublishers(httpServletRequestMock, null, "60")
            .getBody();

        Model modelFromResponse = responseReader.parseResponse(response, "text/turtle");
        Model expectedResponse = responseReader.getExpectedResponse("searchByOrgId.ttl", "TURTLE");

        assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
    }

    @Test
    void getByOrgidSingle() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("application/rdf+xml");

        String response = publishersApiImpl
            .getPublishers(httpServletRequestMock, null, "994686011")
            .getBody();

        Model modelFromResponse = responseReader.parseResponse(response, "RDFXML");
        Model expectedResponse = responseReader.getExpectedResponse("getOne1.ttl", "TURTLE");

        assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
    }

    @Test
    void createAndUpdate() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("text/turtle");

        Publisher newPublisherWithPrefLabel = getNEW_PUBLISHER_0();
        Publisher newPublisherNoPrefLabel = getNEW_PUBLISHER_1();

        String createdId0 = publishersApiImpl
            .createPublisher(httpServletRequestMock, newPublisherWithPrefLabel)
            .getHeaders()
            .getLocation()
            .getPath()
            .split("/")[2];

        String createdId1 = publishersApiImpl
            .createPublisher(httpServletRequestMock, newPublisherNoPrefLabel)
            .getHeaders()
            .getLocation()
            .getPath()
            .split("/")[2];

        ResponseEntity<Void> conflictResponseCreate = publishersApiImpl.createPublisher(httpServletRequestMock, newPublisherNoPrefLabel);
        // Unable to create publisher with existing OrgId
        assertEquals(HttpStatus.CONFLICT, conflictResponseCreate.getStatusCode());

        Publisher newNameElseNull = new Publisher();
        newNameElseNull.setName("updatedName");
        newNameElseNull.setId("idInObjectIsIgnored");

        Publisher updated0 = publishersApiImpl
            .updatePublisher(httpServletRequestMock, createdId0, newNameElseNull)
            .getBody();

        Publisher expectedUpdate0 = getNEW_PUBLISHER_0();
        expectedUpdate0.setId(createdId0);
        expectedUpdate0.setName("updatedName");

        // Only name was changed
        assertEquals(updated0, expectedUpdate0);

        Publisher updated1 = publishersApiImpl
            .updatePublisher(httpServletRequestMock, createdId1, getUPDATE_PUBLISHER())
            .getBody();

        Publisher expectedUpdate1 = getUPDATE_PUBLISHER();
        expectedUpdate1.setId(createdId1);
        expectedUpdate1.setName("Name");

        // All values except name were changed
        assertEquals(updated1, expectedUpdate1);

        Publisher nothingWillBeUpdated = new Publisher();
        nothingWillBeUpdated.setPrefLabel(new PrefLabel());

        Publisher updated2 = publishersApiImpl
            .updatePublisher(httpServletRequestMock, createdId1, nothingWillBeUpdated)
            .getBody();

        // No values changed
        assertEquals(updated2, expectedUpdate1);

        ResponseEntity<Publisher> conflictResponseUpdate = publishersApiImpl.updatePublisher(httpServletRequestMock, createdId0, getUPDATE_PUBLISHER());
        // Unable to update publisher with existing OrgId
        assertEquals(HttpStatus.CONFLICT, conflictResponseUpdate.getStatusCode());
    }
}
