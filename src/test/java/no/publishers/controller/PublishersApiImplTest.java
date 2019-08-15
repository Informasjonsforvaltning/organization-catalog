package no.publishers.controller;

import no.publishers.TestResponseReader;
import no.publishers.generated.model.Publisher;
import no.publishers.service.PublisherService;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
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

import static no.publishers.TestDataKt.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
@Tag("unit")
public class PublishersApiImplTest {
    private TestResponseReader responseReader = new TestResponseReader();

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @Mock
    private PublisherService publisherServiceMock;

    @InjectMocks
    private PublishersApiImpl publishersApi;

    @BeforeAll
    public static void setup() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @BeforeEach
    public void resetMocks() {
        Mockito.reset(publisherServiceMock);
    }

    @Nested
    class getPublishers {

        @Test
        public void okWhenEmptyResult() {
            List<Publisher> emptyList = getEMPTY_PUBLISHERS();
            Mockito
                .when(publisherServiceMock.getPublishers("Name does not exists", null))
                .thenReturn(emptyList);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/turtle");

            ResponseEntity<String> response = publishersApi.getPublishers(httpServletRequestMock, "Name does not exists", null);
            Model modelFromResponse = responseReader.parseResponse(response.getBody());

            Model expectedResponse = responseReader.getExpectedResponse("emptyList");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
        }

        @Test
        public void okGetAll() {
            List<Publisher> regnskapList = getPUBLISHERS();
            Mockito
                .when(publisherServiceMock.getPublishers(null, null))
                .thenReturn(regnskapList);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/turtle");

            ResponseEntity<String> response = publishersApi.getPublishers(httpServletRequestMock, null, null);
            Model modelFromResponse = responseReader.parseResponse(response.getBody());

            Model expectedResponse = responseReader.getExpectedResponse("getList");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
        }

        @Test
        public void okByOrganizationId() {
            List<Publisher> regnskapList = getPUBLISHERS();
            Mockito
                .when(publisherServiceMock.getPublishers(null, "OrgId"))
                .thenReturn(regnskapList);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/turtle");

            ResponseEntity<String> response = publishersApi.getPublishers(httpServletRequestMock, null, "OrgId");
            Model modelFromResponse = responseReader.parseResponse(response.getBody());

            Model expectedResponse = responseReader.getExpectedResponse("getList");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
        }

        @Test
        public void okByName() {
            List<Publisher> regnskapList = getPUBLISHERS();
            Mockito
                .when(publisherServiceMock.getPublishers("Name exists", null))
                .thenReturn(regnskapList);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/turtle");

            ResponseEntity<String> response = publishersApi.getPublishers(httpServletRequestMock, "Name exists", null);
            Model modelFromResponse = responseReader.parseResponse(response.getBody());

            Model expectedResponse = responseReader.getExpectedResponse("getList");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
        }

        @Test
        public void whenExceptions500() {
            Mockito
                .when(publisherServiceMock.getPublishers(null, null))
                .thenAnswer(invocation -> {
                    throw new Exception("Test error message");
                });

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/turtle");

            ResponseEntity<String> response = publishersApi.getPublishers(httpServletRequestMock, null, null);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }

    }

    @Nested
    class GetPublisherById {

        @Test
        public void whenEmptyResult404() {
            Mockito
                .when(publisherServiceMock.getById("123Null"))
                .thenReturn(null);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/turtle");

            ResponseEntity<String> response = publishersApi.getPublisherById(httpServletRequestMock, "123Null");

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        public void whenNonEmptyResult200() {
            Publisher publisher = getPUBLISHER_0();
            Mockito
                .when(publisherServiceMock.getById("5d5531e55c404500068481da"))
                .thenReturn(publisher);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/turtle");

            ResponseEntity<String> response = publishersApi.getPublisherById(httpServletRequestMock, "5d5531e55c404500068481da");
            Model modelFromResponse = responseReader.parseResponse(response.getBody());

            Model expectedResponse = responseReader.getExpectedResponse("getOne");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
        }

        @Test
        public void whenException500() {
            Mockito
                .when(publisherServiceMock.getById("123Error"))
                .thenAnswer(invocation -> {
                    throw new Exception("Test error message");
                });

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/turtle");

            ResponseEntity<String> response = publishersApi.getPublisherById(httpServletRequestMock, "123Error");

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }

    }

    @Nested
    class CreatePublisher{

        @Test
        public void whenCreated201WithLocationHeader() {
            Publisher publisher = getPUBLISHER_0();
            Mockito
                .when(publisherServiceMock.createPublisher(getPUBLISHER_0()))
                .thenReturn(publisher);

            ResponseEntity<Void> response = publishersApi.createPublisher(httpServletRequestMock, getPUBLISHER_0());

            String expectedLocationHeader = "http://localhost/publishers/" + publisher.getId();

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(expectedLocationHeader, response.getHeaders().getLocation().toString());
        }

        @Test
        public void conflictOnDuplicate() {
            Mockito
                .when(publisherServiceMock.createPublisher(getPUBLISHER_0()))
                .thenAnswer( invocation -> { throw new DuplicateKeyException("Duplicate organizationId"); });

            ResponseEntity<Void> response = publishersApi.createPublisher(httpServletRequestMock, getPUBLISHER_0());

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        }

        @Test
        public void badRequestOnValidationError() {
            Mockito
                .when(publisherServiceMock.createPublisher(getPUBLISHER_0()))
                .thenAnswer( invocation -> { throw new ConstraintViolationException(new HashSet<>()); });

            ResponseEntity<Void> response = publishersApi.createPublisher(httpServletRequestMock, getPUBLISHER_0());

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

    }

    @Nested
    class UpdatePublisher {
        @Test
        public void okWhenImplemented() {
            Mockito
                .when(publisherServiceMock.updatePublisher("id", getPUBLISHER_0()))
                .thenReturn(getPUBLISHER_0());

            ResponseEntity<Publisher> response = publishersApi.updatePublisher(httpServletRequestMock, "id", getPUBLISHER_0());

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        public void notFoundWhenIdNotAvailableInDB() {
            Mockito
                .when(publisherServiceMock.updatePublisher("id", getPUBLISHER_0()))
                .thenReturn(null);

            ResponseEntity<Publisher> response = publishersApi.updatePublisher(httpServletRequestMock, "id", getPUBLISHER_0());

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        public void conflictOnDuplicate() {
            Mockito
                .when(publisherServiceMock.updatePublisher("id", getPUBLISHER_0()))
                .thenAnswer(invocation -> {
                    throw new DuplicateKeyException("Duplicate organizationId");
                });

            ResponseEntity<Publisher> response = publishersApi.updatePublisher(httpServletRequestMock, "id", getPUBLISHER_0());

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        }

        @Test
        public void badRequestOnValidationError() {
            Mockito
                .when(publisherServiceMock.updatePublisher("id", getPUBLISHER_0()))
                .thenAnswer(invocation -> {
                    throw new ConstraintViolationException(new HashSet<>());
                });

            ResponseEntity<Publisher> response = publishersApi.updatePublisher(httpServletRequestMock, "id", getPUBLISHER_0());

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }
}
