package no.brreg.organizationcatalogue.controller;

import no.brreg.organizationcatalogue.TestResponseReader;
import no.brreg.organizationcatalogue.configuration.ProfileConditionalValues;
import no.brreg.organizationcatalogue.generated.model.Organization;
import no.brreg.organizationcatalogue.service.OrganizationCatalogueService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RIOT;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.List;

import static no.brreg.organizationcatalogue.TestDataKt.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
@Tag("unit")
public class OrganizationsApiImplTest {
    private TestResponseReader responseReader = new TestResponseReader();

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @Mock
    private OrganizationCatalogueService catalogueServiceMock;

    @Mock
    private ProfileConditionalValues valuesMock;

    @InjectMocks
    private OrganizationsApiImpl controller;

    @BeforeAll
    public static void setup() {
        RIOT.init();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @BeforeEach
    public void resetMocks() {
        Mockito.reset(catalogueServiceMock, valuesMock);
    }

    @Test
    void readyTest() {
        HttpStatus statusCode = controller.ready().getStatusCode();
        assertEquals(HttpStatus.OK, statusCode);
    }

    @Nested
    class getOrganizations {

        @Test
        public void missingAcceptHeader() {
            Mockito
                .when(catalogueServiceMock.getOrganizations("123NotAccepted", null))
                .thenReturn(getEMPTY_LIST());

            Mockito
                .when(valuesMock.organizationCatalogueUrl())
                .thenReturn(ORGANIZATIONS_URL);
            Mockito
                .when(valuesMock.municipalityUrl())
                .thenReturn(MUNICIPALITY_URL);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn(null);

            ResponseEntity<Object> response = controller.getOrganizations(httpServletRequestMock, "123NotAccepted", null);

            assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        public void okWhenEmptyResult() {
            List<Organization> emptyList = getEMPTY_LIST();
            Mockito
                .when(catalogueServiceMock.getOrganizations("Name does not exists", null))
                .thenReturn(emptyList);

            Mockito
                .when(valuesMock.organizationCatalogueUrl())
                .thenReturn(ORGANIZATIONS_URL);
            Mockito
                .when(valuesMock.municipalityUrl())
                .thenReturn(MUNICIPALITY_URL);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/turtle");

            ResponseEntity<Object> response = controller.getOrganizations(httpServletRequestMock, "Name does not exists", null);
            Model modelFromResponse = responseReader.parseResponse((String)response.getBody(), "text/turtle");

            Model expectedResponse = responseReader.getExpectedResponse("emptyList.ttl", "TURTLE");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
        }

        @Test
        public void okGetAll() {
            List<Organization> regnskapList = getORGS();
            Mockito
                .when(catalogueServiceMock.getOrganizations(null, null))
                .thenReturn(regnskapList);

            Mockito
                .when(valuesMock.organizationCatalogueUrl())
                .thenReturn(ORGANIZATIONS_URL);
            Mockito
                .when(valuesMock.municipalityUrl())
                .thenReturn(MUNICIPALITY_URL);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/turtle");

            ResponseEntity<Object> response = controller.getOrganizations(httpServletRequestMock, null, null);
            Model modelFromResponse = responseReader.parseResponse((String)response.getBody(), "text/turtle");

            Model expectedResponse = responseReader.getExpectedResponse("getList.ttl", "TURTLE");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
        }

        @Test
        public void okByOrganizationId() {
            List<Organization> regnskapList = getORGS();
            Mockito
                .when(catalogueServiceMock.getOrganizations(null, "OrgId"))
                .thenReturn(regnskapList);

            Mockito
                .when(valuesMock.organizationCatalogueUrl())
                .thenReturn(ORGANIZATIONS_URL);
            Mockito
                .when(valuesMock.municipalityUrl())
                .thenReturn(MUNICIPALITY_URL);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/turtle");

            ResponseEntity<Object> response = controller.getOrganizations(httpServletRequestMock, null, "OrgId");
            Model modelFromResponse = responseReader.parseResponse((String)response.getBody(), "text/turtle");

            Model expectedResponse = responseReader.getExpectedResponse("getList.ttl", "TURTLE");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
        }

        @Test
        public void okByName() {
            List<Organization> regnskapList = getORGS();
            Mockito
                .when(catalogueServiceMock.getOrganizations("Name exists", null))
                .thenReturn(regnskapList);

            Mockito
                .when(valuesMock.organizationCatalogueUrl())
                .thenReturn(ORGANIZATIONS_URL);
            Mockito
                .when(valuesMock.municipalityUrl())
                .thenReturn(MUNICIPALITY_URL);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/turtle");

            ResponseEntity<Object> response = controller.getOrganizations(httpServletRequestMock, "Name exists", null);
            Model modelFromResponse = responseReader.parseResponse((String)response.getBody(), "text/turtle");

            Model expectedResponse = responseReader.getExpectedResponse("getList.ttl", "TURTLE");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
        }

    }

    @Nested
    class GetOrganizationById {

        @Test
        public void whenEmptyResult404() {
            Mockito
                .when(catalogueServiceMock.getByOrgnr("123Null"))
                .thenReturn(null);

            Mockito
                .when(valuesMock.organizationCatalogueUrl())
                .thenReturn(ORGANIZATIONS_URL);
            Mockito
                .when(valuesMock.municipalityUrl())
                .thenReturn(MUNICIPALITY_URL);

            ResponseEntity<Object> response = controller.getOrganizationById(httpServletRequestMock, "123Null");

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        public void wrongAcceptHeader() {
            Organization publisher = getORG_0();
            Mockito
                .when(catalogueServiceMock.getByOrgnr("123NotAccepted"))
                .thenReturn(publisher);

            Mockito
                .when(valuesMock.organizationCatalogueUrl())
                .thenReturn(ORGANIZATIONS_URL);
            Mockito
                .when(valuesMock.municipalityUrl())
                .thenReturn(MUNICIPALITY_URL);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/plain");

            ResponseEntity<Object> response = controller.getOrganizationById(httpServletRequestMock, "123NotAccepted");

            assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        public void whenNonEmptyResult200() {
            Organization publisher = getORG_0();
            Mockito
                .when(catalogueServiceMock.getByOrgnr("974760673"))
                .thenReturn(publisher);

            Mockito
                .when(valuesMock.organizationCatalogueUrl())
                .thenReturn(ORGANIZATIONS_URL);
            Mockito
                .when(valuesMock.municipalityUrl())
                .thenReturn(MUNICIPALITY_URL);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/turtle");

            ResponseEntity<Object> response = controller.getOrganizationById(httpServletRequestMock, "974760673");
            Model modelFromResponse = responseReader.parseResponse((String) response.getBody(), "text/turtle");

            System.out.println(response.getBody());

            Model expectedResponse = responseReader.getExpectedResponse("getOne.ttl", "TURTLE");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
        }

    }

    @Nested
    class UpdateOrganization {
        @Test
        public void okWhenImplemented() {
            Mockito
                .when(catalogueServiceMock.updateEntry("id", getORG_0()))
                .thenReturn(getORG_0());

            ResponseEntity<Organization> response = controller.updateOrganization(httpServletRequestMock, "id", getORG_0());

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        public void notFoundWhenIdNotAvailableInDB() {
            Mockito
                .when(catalogueServiceMock.updateEntry("id", getORG_0()))
                .thenReturn(null);

            ResponseEntity<Organization> response = controller.updateOrganization(httpServletRequestMock, "id", getORG_0());

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        public void conflictOnDuplicate() {
            Mockito
                .when(catalogueServiceMock.updateEntry("id", getORG_0()))
                .thenAnswer(invocation -> {
                    throw new DuplicateKeyException("Duplicate organizationId");
                });

            ResponseEntity<Organization> response = controller.updateOrganization(httpServletRequestMock, "id", getORG_0());

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        }

        @Test
        public void badRequestOnValidationError() {
            Mockito
                .when(catalogueServiceMock.updateEntry("id", getORG_0()))
                .thenAnswer(invocation -> {
                    throw new ConstraintViolationException(new HashSet<>());
                });

            ResponseEntity<Organization> response = controller.updateOrganization(httpServletRequestMock, "id", getORG_0());

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        public void whenUnexpectedException500() {
            Mockito
                .when(catalogueServiceMock.updateEntry("id", getORG_0()))
                .thenAnswer(invocation -> {
                    throw new Exception("Unexpected exception");
                });

            ResponseEntity<Organization> response = controller.updateOrganization(httpServletRequestMock, "id", getORG_0());

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }
    }
}
