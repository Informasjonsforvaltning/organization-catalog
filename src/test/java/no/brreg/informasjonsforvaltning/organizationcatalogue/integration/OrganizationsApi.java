package no.brreg.informasjonsforvaltning.organizationcatalogue.integration;

import no.brreg.informasjonsforvaltning.organizationcatalogue.TestResponseReader;
import no.brreg.informasjonsforvaltning.organizationcatalogue.controller.OrganizationsApiImpl;
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.model.Organization;
import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.model.PrefLabel;
import no.brreg.informasjonsforvaltning.organizationcatalogue.repository.OrganizationCatalogueRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@ContextConfiguration(initializers = {OrganizationsApi.Initializer.class})
@Tag("service")
class OrganizationsApi {
    private final static Logger logger = LoggerFactory.getLogger(OrganizationsApi.class);
    private static Slf4jLogConsumer mongoLog = new Slf4jLogConsumer(logger).withPrefix("mongo-container");
    @Container
    private static final GenericContainer mongoContainer = new GenericContainer("mongo:latest")
        .withEnv(no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getMONGO_ENV_VALUES())
        .withLogConsumer(mongoLog)
        .withExposedPorts(no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.MONGO_PORT)
        .waitingFor(Wait.forListeningPort());
    @Mock
    private static HttpServletRequest httpServletRequestMock;
    private TestResponseReader responseReader = new TestResponseReader();
    @Autowired
    private OrganizationsApiImpl controller;

    @BeforeAll
    static void setup(@Autowired OrganizationCatalogueRepository repository) {
        RIOT.init();
        repository.save(no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getORG_DB_0());
        repository.save(no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getORG_DB_1());
        repository.save(no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getORG_DB_2());
        repository.save(no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getORG_DB_3());
        repository.save(no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getORG_DB_4());
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

        Object response0 = controller
            .getOrganizationById(httpServletRequestMock, no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getORG_0().getOrganizationId())
            .getBody();

        Object response1 = controller
            .getOrganizationById(httpServletRequestMock, no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getORG_1().getOrganizationId())
            .getBody();

        Object response2 = controller
            .getOrganizationById(httpServletRequestMock, no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getORG_2().getOrganizationId())
            .getBody();

        Model modelFromResponse0 = responseReader.parseResponse((String) response0, "text/turtle");
        Model expectedResponse0 = responseReader.getExpectedResponse("getOne.ttl", "TURTLE");

        Model modelFromResponse1 = responseReader.parseResponse((String) response1, "text/turtle");
        Model expectedResponse1 = responseReader.getExpectedResponse("getOne1.ttl", "TURTLE");

        Model modelFromResponse2 = responseReader.parseResponse((String) response2, "text/turtle");
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

        Object response = controller
            .getOrganizations(httpServletRequestMock, "ET", null)
            .getBody();

        Model modelFromResponse = responseReader.parseResponse((String) response, "TURTLE");
        Model expectedResponse = responseReader.getExpectedResponse("searchByName.ttl", "TURTLE");

        assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
    }

    @Test
    void getByNameSingle() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("application/ld+json");

        Object response = controller
            .getOrganizations(httpServletRequestMock, "FORSVARET", null)
            .getBody();

        Model modelFromResponse = responseReader.parseResponse((String) response, "JSONLD");
        Model expectedResponse = responseReader.getExpectedResponse("getOne2.ttl", "TURTLE");

        assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
    }

    @Test
    void getByOrgidSeveralPossibilities() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("text/turtle");

        Object response = controller
            .getOrganizations(httpServletRequestMock, null, "60")
            .getBody();

        Model modelFromResponse = responseReader.parseResponse((String) response, "text/turtle");
        Model expectedResponse = responseReader.getExpectedResponse("searchByOrgId.ttl", "TURTLE");

        assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
    }

    @Test
    void getByOrgidSingle() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("application/rdf+xml");

        Object response = controller
            .getOrganizations(httpServletRequestMock, null, "994686011")
            .getBody();

        Model modelFromResponse = responseReader.parseResponse((String) response, "RDFXML");
        Model expectedResponse = responseReader.getExpectedResponse("getOne1.ttl", "TURTLE");

        assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
    }

    @Test
    @WithMockUser(authorities = {"system:root:admin"})
    void updateOrganization() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("text/turtle");

        Organization newNameElseNull = new Organization();
        newNameElseNull.setName("updatedName");

        Organization updated0 = controller
            .updateOrganization(httpServletRequestMock, no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getNOT_UPDATED_0().getOrganizationId(), newNameElseNull)
            .getBody();

        // Only name was changed
        assertEquals(no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getUPDATED_0(), updated0);

        Organization updated1 = controller
            .updateOrganization(httpServletRequestMock, no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getNOT_UPDATED_1().getOrganizationId(), no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getUPDATED_1())
            .getBody();

        Organization expected1 = no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getUPDATED_1();
        expected1.setOrganizationId(no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getNOT_UPDATED_1().getOrganizationId());
        expected1.setNorwegianRegistry(no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getNOT_UPDATED_1().getNorwegianRegistry());

        // Name & orgId were not changed
        assertEquals(expected1, updated1);

        Organization nothingWillBeUpdated = new Organization();
        nothingWillBeUpdated.setPrefLabel(new PrefLabel());

        Organization updated2 = controller
            .updateOrganization(httpServletRequestMock, no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getUPDATED_1().getOrganizationId(), nothingWillBeUpdated)
            .getBody();

        // No values changed
        assertEquals(no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.getUPDATED_1(), updated2);
    }

    static class Initializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                "spring.data.mongodb.database=" + no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.DATABASE_NAME,
                "spring.data.mongodb.uri=" + no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.buildMongoURI(mongoContainer.getContainerIpAddress(), mongoContainer.getMappedPort(no.brreg.informasjonsforvaltning.organizationcatalogue.TestDataKt.MONGO_PORT), false),
                "application.enhetsregisteretUrl=https://invalid.org/enhetsregisteret/api/enheter/",
                "application.organizationCatalogueUrl=https://invalid.org/organizations/",
                "application.municipalityUrl=https://invalid.org/administrativeEnheter/kommune/id/",
                "application.organizationDomainsUrl=https://invalid.org/domains/"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
