package no.brreg.organizationcatalogue.integration;

import no.brreg.organizationcatalogue.TestResponseReader;
import no.brreg.organizationcatalogue.controller.OrganizationsApiImpl;
import no.brreg.organizationcatalogue.generated.model.PrefLabel;
import no.brreg.organizationcatalogue.generated.model.Organization;
import no.brreg.organizationcatalogue.repository.OrganizationCatalogueRepository;
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

import static no.brreg.organizationcatalogue.TestDataKt.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@ContextConfiguration(initializers = {OrganizationsApi.Initializer.class})
@Tag("service")
class OrganizationsApi {
    private final static Logger logger = LoggerFactory.getLogger(OrganizationsApi.class);
    private static Slf4jLogConsumer mongoLog = new Slf4jLogConsumer(logger).withPrefix("mongo-container");
    private TestResponseReader responseReader = new TestResponseReader();

    @Mock
    private static HttpServletRequest httpServletRequestMock;

    @Autowired
    private OrganizationsApiImpl controller;

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
    static void setup(@Autowired OrganizationCatalogueRepository repository) {
        RIOT.init();
        repository.save(getORG_DB_0());
        repository.save(getORG_DB_1());
        repository.save(getORG_DB_2());
    }

    @Test
    void pingTest() {
        String response = controller.ping().getBody();
        assertEquals("pong", response);
    }

    @Test
    void getById() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("text/turtle");

        String response0 = controller
            .getOrganizationById(httpServletRequestMock, getORG_0().getOrganizationId())
            .getBody();

        String response1 = controller
            .getOrganizationById(httpServletRequestMock, getORG_1().getOrganizationId())
            .getBody();

        String response2 = controller
            .getOrganizationById(httpServletRequestMock, getORG_2().getOrganizationId())
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

        String response = controller
            .getOrganizations(httpServletRequestMock, "ET", null)
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

        String response = controller
            .getOrganizations(httpServletRequestMock, "FORSVARET", null)
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

        String response = controller
            .getOrganizations(httpServletRequestMock, null, "60")
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

        String response = controller
            .getOrganizations(httpServletRequestMock, null, "994686011")
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

        Organization newOrganizationWithPrefLabel = getNEW_ORG_0();
        Organization newOrganizationNoPrefLabel = getNEW_ORG_1();

        controller.createOrganization(httpServletRequestMock, newOrganizationWithPrefLabel);
        controller.createOrganization(httpServletRequestMock, newOrganizationNoPrefLabel);

        ResponseEntity<Void> conflictResponseCreate = controller.createOrganization(httpServletRequestMock, newOrganizationNoPrefLabel);
        // Unable to create publisher with existing OrgId
        assertEquals(HttpStatus.CONFLICT, conflictResponseCreate.getStatusCode());

        Organization newNameElseNull = new Organization();
        newNameElseNull.setName("updatedName");
        newNameElseNull.setId("idInObjectIsIgnored");

        Organization updated0 = controller
            .updateOrganization(httpServletRequestMock, newOrganizationWithPrefLabel.getOrganizationId(), newNameElseNull)
            .getBody();

        Organization expectedUpdate0 = getNEW_ORG_0();
        expectedUpdate0.setId(updated0.getId());
        expectedUpdate0.setName("updatedName");

        // Only name was changed
        assertEquals(expectedUpdate0, updated0);

        Organization updated1 = controller
            .updateOrganization(httpServletRequestMock, newOrganizationNoPrefLabel.getOrganizationId(), getUPDATE_ORG())
            .getBody();

        Organization expectedUpdate1 = getUPDATE_ORG();
        expectedUpdate1.setId(updated1.getId());
        expectedUpdate1.setName("Name");

        // All values except name were changed
        assertEquals(expectedUpdate1, updated1);

        Organization nothingWillBeUpdated = new Organization();
        nothingWillBeUpdated.setPrefLabel(new PrefLabel());

        Organization updated2 = controller
            .updateOrganization(httpServletRequestMock, updated1.getOrganizationId(), nothingWillBeUpdated)
            .getBody();

        // No values changed
        assertEquals(expectedUpdate1, updated2);

        ResponseEntity<Organization> conflictResponseUpdate = controller.updateOrganization(httpServletRequestMock, newOrganizationWithPrefLabel.getOrganizationId(), getUPDATE_ORG());
        // Unable to update publisher with existing OrgId
        assertEquals(HttpStatus.CONFLICT, conflictResponseUpdate.getStatusCode());
    }
}
