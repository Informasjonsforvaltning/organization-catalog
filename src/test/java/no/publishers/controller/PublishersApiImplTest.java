package no.publishers.controller;

import no.publishers.TestData;
import no.publishers.generated.model.Publisher;
import no.publishers.service.PublisherService;
import no.publishers.testcategories.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
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

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTest.class)
public class PublishersApiImplTest {

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @Mock
    private PublisherService publisherServiceMock;

    @InjectMocks
    private PublishersApiImpl publishersApi;

    @BeforeClass
    public static void setup() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @Before
    public void resetMocks() {
        Mockito.reset(publisherServiceMock);
    }

    @Test
    public void getPublishersOkWhenEmptyResult() {
        List<Publisher> emptyList = TestData.EMPTY_PUBLISHERS;
        Mockito
            .when(publisherServiceMock.getPublishers("Name does not exists", null))
            .thenReturn(emptyList);

        ResponseEntity<List<Publisher>> response = publishersApi.getPublishers(httpServletRequestMock, "Name does not exists", null);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(TestData.EMPTY_PUBLISHERS, response.getBody());
    }

    @Test
    public void getAllPublishersOk() {
        List<Publisher> regnskapList = TestData.PUBLISHERS;
        Mockito
            .when(publisherServiceMock.getPublishers(null, null))
            .thenReturn(regnskapList);

        ResponseEntity<List<Publisher>> response = publishersApi.getPublishers(httpServletRequestMock, null, null);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(TestData.PUBLISHERS,response.getBody());
    }

    @Test
    public void getPublishersByOrganizationIdOk() {
        List<Publisher> regnskapList = TestData.PUBLISHERS;
        Mockito
            .when(publisherServiceMock.getPublishers(null, "OrgId"))
            .thenReturn(regnskapList);

        ResponseEntity<List<Publisher>> response = publishersApi.getPublishers(httpServletRequestMock, null, "OrgId");

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(TestData.PUBLISHERS,response.getBody());
    }

    @Test
    public void getPublishersByNameOk() {
        List<Publisher> regnskapList = TestData.PUBLISHERS;
        Mockito
            .when(publisherServiceMock.getPublishers("Name exists", null))
            .thenReturn(regnskapList);

        ResponseEntity<List<Publisher>> response = publishersApi.getPublishers(httpServletRequestMock, "Name exists", null);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(TestData.PUBLISHERS,response.getBody());
    }

    @Test
    public void getPublishers500ForExceptions() {
        Mockito
            .when(publisherServiceMock.getPublishers(null, null))
            .thenAnswer( invocation -> { throw new Exception("Test error message"); });

        ResponseEntity<List<Publisher>> response = publishersApi.getPublishers(httpServletRequestMock, null, null);

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void getPublisherById404WhenEmptyResult() {
        Mockito
            .when(publisherServiceMock.getById("123Null"))
            .thenReturn(null);

        ResponseEntity<Publisher> response = publishersApi.getPublisherById(httpServletRequestMock, "123Null");

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assert.assertNull(response.getBody());
    }

    @Test
    public void getPublisherByIdOkWhenNonEmptyResult() {
        Publisher publisher = TestData.PUBLISHER_0;
        Mockito
            .when(publisherServiceMock.getById("123Ok"))
            .thenReturn(publisher);

        ResponseEntity<Publisher> response = publishersApi.getPublisherById(httpServletRequestMock, "123Ok");

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(TestData.PUBLISHER_0, response.getBody());
    }

    @Test
    public void getPublisherById500ForExceptions() {
        Mockito
            .when(publisherServiceMock.getById("123Error"))
            .thenAnswer( invocation -> { throw new Exception("Test error message"); });

        ResponseEntity<Publisher> response = publishersApi.getPublisherById(httpServletRequestMock, "123Error");

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void createPublisher201WithLocationHeader() {
        Publisher publisher = TestData.PUBLISHER_0;
        publisher.setId("abc123qwerty");
        Mockito
            .when(publisherServiceMock.createPublisher(TestData.PUBLISHER_0))
            .thenReturn(publisher);

        ResponseEntity<Void> response = publishersApi.createPublisher(httpServletRequestMock, TestData.PUBLISHER_0);

        String expectedLocationHeader = "http://localhost/publishers/abc123qwerty";

        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assert.assertEquals(expectedLocationHeader, response.getHeaders().getLocation().toString());
    }

    @Test
    public void createPublisherConflictOnDuplicate() {
        Mockito
            .when(publisherServiceMock.createPublisher(TestData.PUBLISHER_0))
            .thenAnswer( invocation -> { throw new DuplicateKeyException("Duplicate organizationId"); });

        ResponseEntity<Void> response = publishersApi.createPublisher(httpServletRequestMock, TestData.PUBLISHER_0);

        Assert.assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void createPublisherBadRequestOnValidationError() {
        Mockito
            .when(publisherServiceMock.createPublisher(TestData.PUBLISHER_0))
            .thenAnswer( invocation -> { throw new ConstraintViolationException(new HashSet<>()); });

        ResponseEntity<Void> response = publishersApi.createPublisher(httpServletRequestMock, TestData.PUBLISHER_0);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void updatePublisherOk() {
        Mockito
            .when(publisherServiceMock.updatePublisher("id", TestData.PUBLISHER_0))
            .thenReturn(TestData.PUBLISHER_0);

        ResponseEntity<Publisher> response = publishersApi.updatePublisher(httpServletRequestMock, "id", TestData.PUBLISHER_0);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void updatePublisherNotFound() {
        Mockito
            .when(publisherServiceMock.updatePublisher("id", TestData.PUBLISHER_0))
            .thenReturn(null);

        ResponseEntity<Publisher> response = publishersApi.updatePublisher(httpServletRequestMock, "id", TestData.PUBLISHER_0);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void updatePublisherConflictOnDuplicate() {
        Mockito
            .when(publisherServiceMock.updatePublisher("id", TestData.PUBLISHER_0))
            .thenAnswer( invocation -> { throw new DuplicateKeyException("Duplicate organizationId"); });

        ResponseEntity<Publisher> response = publishersApi.updatePublisher(httpServletRequestMock, "id", TestData.PUBLISHER_0);

        Assert.assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void updatePublisherBadRequestOnValidationError() {
        Mockito
            .when(publisherServiceMock.updatePublisher("id", TestData.PUBLISHER_0))
            .thenAnswer( invocation -> { throw new ConstraintViolationException(new HashSet<>()); });

        ResponseEntity<Publisher> response = publishersApi.updatePublisher(httpServletRequestMock, "id", TestData.PUBLISHER_0);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
