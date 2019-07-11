package no.publishers.integration;

import no.publishers.TestData;
import no.publishers.controller.PublishersApiImpl;
import no.publishers.generated.model.Publisher;
import no.publishers.service.PublisherService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

import static no.publishers.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
@ContextConfiguration(initializers = {PublisherApi.Initializer.class})
@Tag("service")
class PublisherApi {
    private final static Logger logger = LoggerFactory.getLogger(PublisherApi.class);
    private static Slf4jLogConsumer mongoLog = new Slf4jLogConsumer(logger).withPrefix("mongo-container");

    private static String publisherId0;
    private static String publisherId1;
    private static String publisherId2;

    @Mock
    private static HttpServletRequest httpServletRequestMock;

    @Autowired
    private PublishersApiImpl publishersApiImpl;

    @Container
    private static final GenericContainer mongoContainer = new GenericContainer("mongo:latest")
        .withEnv(MONGO_ENV_VALUES)
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
    static void setup(@Autowired PublisherService publisherService) {
        publisherId0 = publisherService
            .createPublisher(TestData.PUBLISHER_0)
            .getId();
        publisherId1 = publisherService
            .createPublisher(TestData.PUBLISHER_1)
            .getId();
        publisherId2 = publisherService
            .createPublisher(TestData.PUBLISHER_2)
            .getId();
    }

    @Test
    void pingTest() {
        String response = publishersApiImpl.ping().getBody();
        assertEquals("pong", response);
    }

    @Test
    void getById() {
        Publisher response0 = publishersApiImpl
            .getPublisherById(httpServletRequestMock, publisherId0)
            .getBody();

        Publisher response1 = publishersApiImpl
            .getPublisherById(httpServletRequestMock, publisherId1)
            .getBody();
        
        Publisher response2 = publishersApiImpl
            .getPublisherById(httpServletRequestMock, publisherId2)
            .getBody();

        assertEquals(TestData.PUBLISHER_0.getName(), response0.getName());
        assertEquals(TestData.PUBLISHER_1.getName(), response1.getName());
        assertEquals(TestData.PUBLISHER_2.getName(), response2.getName());
    }

    @Test
    void getByNameSeveralPossibilities() {
        List<Publisher> response = publishersApiImpl
            .getPublishers(httpServletRequestMock, "name", null)
            .getBody();

        assertEquals(2, response.size());
        assertEquals(TestData.PUBLISHER_0.getOrganizationId(), response.get(0).getOrganizationId());
        assertEquals(TestData.PUBLISHER_2.getOrganizationId(), response.get(1).getOrganizationId());
    }

    @Test
    void getByNameSingle() {
        List<Publisher> response = publishersApiImpl
            .getPublishers(httpServletRequestMock, "name2", null)
            .getBody();

        assertEquals(1, response.size());
        assertEquals(TestData.PUBLISHER_2.getOrganizationId(), response.get(0).getOrganizationId());
    }

    @Test
    void getByOrgidSeveralPossibilities() {
        List<Publisher> response = publishersApiImpl
            .getPublishers(httpServletRequestMock, null, "34")
            .getBody();

        assertEquals(2, response.size());
        assertEquals(TestData.PUBLISHER_0.getName(), response.get(0).getName());
        assertEquals(TestData.PUBLISHER_1.getName(), response.get(1).getName());
    }

    @Test
    void getByOrgidSingle() {
        List<Publisher> response = publishersApiImpl
            .getPublishers(httpServletRequestMock, null, "3456")
            .getBody();

        assertEquals(1, response.size());
        assertEquals(TestData.PUBLISHER_1.getName(), response.get(0).getName());
    }

    @Test
    void createAndUpdateName() {
        String createdId = publishersApiImpl
            .createPublisher(httpServletRequestMock, TestData.PUBLISHER_3)
            .getHeaders()
            .getLocation()
            .getPath()
            .split("/")[2];

        Publisher publisher = publishersApiImpl
            .getPublisherById(httpServletRequestMock, createdId)
            .getBody();

        // Created publisher has correct name
        assertEquals("toBeUpdated", publisher.getName());

        Publisher newNameElseNull = new Publisher();
        newNameElseNull.setName("updatedName");
        newNameElseNull.setId("idInObjectIsIgnored");

        Publisher updated = publishersApiImpl
            .updatePublisher(httpServletRequestMock, createdId, newNameElseNull)
            .getBody();

        Publisher getUpdated = publishersApiImpl
            .getPublisherById(httpServletRequestMock, createdId)
            .getBody();

        // Publisher has updated name, same object from update and getById
        assertEquals("updatedName", updated.getName());
        assertEquals(updated, getUpdated);

        // Only name is changed
        publisher.setName("updatedName");
        assertEquals(updated, publisher);
    }
}
