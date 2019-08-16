package no.publishers.integration;

import no.publishers.TestResponseReader;
import no.publishers.controller.PublishersApiImpl;
import no.publishers.generated.model.Publisher;
import no.publishers.repository.PublisherRepository;
import no.publishers.service.PublisherService;
import org.apache.jena.rdf.model.Model;
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
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

        Model modelFromResponse0 = responseReader.parseResponse(response0);
        Model expectedResponse0 = responseReader.getExpectedResponse("getOne");

        Model modelFromResponse1 = responseReader.parseResponse(response1);
        Model expectedResponse1 = responseReader.getExpectedResponse("getOne1");

        Model modelFromResponse2 = responseReader.parseResponse(response2);
        Model expectedResponse2 = responseReader.getExpectedResponse("getOne2");

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

        Model modelFromResponse = responseReader.parseResponse(response);
        Model expectedResponse = responseReader.getExpectedResponse("searchByName");

        assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
    }

    @Test
    void getByNameSingle() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("text/turtle");

        String response = publishersApiImpl
            .getPublishers(httpServletRequestMock, "FORSVARET", null)
            .getBody();

        Model modelFromResponse = responseReader.parseResponse(response);
        Model expectedResponse = responseReader.getExpectedResponse("getOne2");

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

        Model modelFromResponse = responseReader.parseResponse(response);
        Model expectedResponse = responseReader.getExpectedResponse("searchByOrgId");

        assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
    }

    @Test
    void getByOrgidSingle() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("text/turtle");

        String response = publishersApiImpl
            .getPublishers(httpServletRequestMock, null, "994686011")
            .getBody();

        Model modelFromResponse = responseReader.parseResponse(response);
        Model expectedResponse = responseReader.getExpectedResponse("getOne1");

        assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
    }

    @Test
    void createAndUpdateName() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("text/turtle");

        String createdId = publishersApiImpl
            .createPublisher(httpServletRequestMock, getPUBLISHER_3())
            .getHeaders()
            .getLocation()
            .getPath()
            .split("/")[2];

        String createdResponse = publishersApiImpl
            .getPublisherById(httpServletRequestMock, createdId)
            .getBody();

        Model modelFromCreate = responseReader.parseResponse(createdResponse);
        Model expectedFromCreate = responseReader.getExpectedFromCreate(createdId);

        // Created publisher as expected
        assertTrue(expectedFromCreate.isIsomorphicWith(modelFromCreate));

        Publisher newNameElseNull = new Publisher();
        newNameElseNull.setName("updatedName");
        newNameElseNull.setId("idInObjectIsIgnored");

        Publisher updated = publishersApiImpl
            .updatePublisher(httpServletRequestMock, createdId, newNameElseNull)
            .getBody();

        // Publisher has updated name
        assertEquals("updatedName", updated.getName());

        String updatedResponse = publishersApiImpl
            .getPublisherById(httpServletRequestMock, createdId)
            .getBody();

        Model modelFromUpdate = responseReader.parseResponse(updatedResponse);
        Model expectedFromUpdate = responseReader.getExpectedFromUpdate(createdId);

        // Created publisher as expected
        assertTrue(expectedFromUpdate.isIsomorphicWith(modelFromUpdate));

    }
}
