package no.publishers.controller;

import no.publishers.TestData;
import no.publishers.generated.model.Publisher;
import no.publishers.service.PublisherService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
@Tag("unit")
public class PublishersApiImplTest {

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
            List<Publisher> emptyList = TestData.EMPTY_PUBLISHERS;
            Mockito
                .when(publisherServiceMock.getPublishers("Name does not exists", null))
                .thenReturn(emptyList);

            ResponseEntity<List<Publisher>> response = publishersApi.getPublishers(httpServletRequestMock, "Name does not exists", null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(TestData.EMPTY_PUBLISHERS, response.getBody());
        }

        @Test
        public void okGetAll() {
            List<Publisher> regnskapList = TestData.PUBLISHERS;
            Mockito
                .when(publisherServiceMock.getPublishers(null, null))
                .thenReturn(regnskapList);

            ResponseEntity<List<Publisher>> response = publishersApi.getPublishers(httpServletRequestMock, null, null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(TestData.PUBLISHERS,response.getBody());
        }

        @Test
        public void okByOrganizationId() {
            List<Publisher> regnskapList = TestData.PUBLISHERS;
            Mockito
                .when(publisherServiceMock.getPublishers(null, "OrgId"))
                .thenReturn(regnskapList);

            ResponseEntity<List<Publisher>> response = publishersApi.getPublishers(httpServletRequestMock, null, "OrgId");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(TestData.PUBLISHERS, response.getBody());
        }

        @Test
        public void okByName() {
            List<Publisher> regnskapList = TestData.PUBLISHERS;
            Mockito
                .when(publisherServiceMock.getPublishers("Name exists", null))
                .thenReturn(regnskapList);

            ResponseEntity<List<Publisher>> response = publishersApi.getPublishers(httpServletRequestMock, "Name exists", null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(TestData.PUBLISHERS, response.getBody());
        }

        @Test
        public void whenExceptions500() {
            Mockito
                .when(publisherServiceMock.getPublishers(null, null))
                .thenAnswer(invocation -> {
                    throw new Exception("Test error message");
                });

            ResponseEntity<List<Publisher>> response = publishersApi.getPublishers(httpServletRequestMock, null, null);

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

            ResponseEntity<Publisher> response = publishersApi.getPublisherById(httpServletRequestMock, "123Null");

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        public void whenNonEmptyResult200() {
            Publisher publisher = TestData.PUBLISHER_0;
            Mockito
                .when(publisherServiceMock.getById("123Ok"))
                .thenReturn(publisher);

            ResponseEntity<Publisher> response = publishersApi.getPublisherById(httpServletRequestMock, "123Ok");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(TestData.PUBLISHER_0, response.getBody());
        }

        @Test
        public void whenException500() {
            Mockito
                .when(publisherServiceMock.getById("123Error"))
                .thenAnswer(invocation -> {
                    throw new Exception("Test error message");
                });

            ResponseEntity<Publisher> response = publishersApi.getPublisherById(httpServletRequestMock, "123Error");

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }

    }

    @Nested
    class CreatePublisher{

        @Test
        public void whenCreated201WithLocationHeader() {
            Publisher publisher = TestData.PUBLISHER_0;
            publisher.setId("abc123qwerty");
            Mockito
                .when(publisherServiceMock.createPublisher(TestData.PUBLISHER_0))
                .thenReturn(publisher);

            ResponseEntity<Void> response = publishersApi.createPublisher(httpServletRequestMock, TestData.PUBLISHER_0);

            String expectedLocationHeader = "http://localhost/publishers/abc123qwerty";

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(expectedLocationHeader, response.getHeaders().getLocation().toString());
        }

        @Test
        public void conflictOnDuplicate() {
            Mockito
                .when(publisherServiceMock.createPublisher(TestData.PUBLISHER_0))
                .thenAnswer( invocation -> { throw new DuplicateKeyException("Duplicate organizationId"); });

            ResponseEntity<Void> response = publishersApi.createPublisher(httpServletRequestMock, TestData.PUBLISHER_0);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        }

        @Test
        public void badRequestOnValidationError() {
            Mockito
                .when(publisherServiceMock.createPublisher(TestData.PUBLISHER_0))
                .thenAnswer( invocation -> { throw new ConstraintViolationException(new HashSet<>()); });

            ResponseEntity<Void> response = publishersApi.createPublisher(httpServletRequestMock, TestData.PUBLISHER_0);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

    }

    @Nested
    class UpdatePublisher {
        @Test
        public void okWhenImplemented() {
            Mockito
                .when(publisherServiceMock.updatePublisher("id", TestData.PUBLISHER_0))
                .thenReturn(TestData.PUBLISHER_0);

            ResponseEntity<Publisher> response = publishersApi.updatePublisher(httpServletRequestMock, "id", TestData.PUBLISHER_0);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        public void notFoundWhenIdNotAvailableInDB() {
            Mockito
                .when(publisherServiceMock.updatePublisher("id", TestData.PUBLISHER_0))
                .thenReturn(null);

            ResponseEntity<Publisher> response = publishersApi.updatePublisher(httpServletRequestMock, "id", TestData.PUBLISHER_0);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        public void conflictOnDuplicate() {
            Mockito
                .when(publisherServiceMock.updatePublisher("id", TestData.PUBLISHER_0))
                .thenAnswer(invocation -> {
                    throw new DuplicateKeyException("Duplicate organizationId");
                });

            ResponseEntity<Publisher> response = publishersApi.updatePublisher(httpServletRequestMock, "id", TestData.PUBLISHER_0);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        }

        @Test
        public void badRequestOnValidationError() {
            Mockito
                .when(publisherServiceMock.updatePublisher("id", TestData.PUBLISHER_0))
                .thenAnswer(invocation -> {
                    throw new ConstraintViolationException(new HashSet<>());
                });

            ResponseEntity<Publisher> response = publishersApi.updatePublisher(httpServletRequestMock, "id", TestData.PUBLISHER_0);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }
}
