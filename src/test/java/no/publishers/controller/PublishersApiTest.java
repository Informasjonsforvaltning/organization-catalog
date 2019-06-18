package no.publishers.controller;

import no.publishers.TestData;
import no.publishers.generated.model.Publisher;
import no.publishers.service.PublisherService;
import no.publishers.testcategories.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTest.class)
public class PublishersApiTest {

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @Mock
    private PublisherService publisherServiceMock;

    @InjectMocks
    private PublishersApiImpl publishersApi;

    @Before
    public void resetMocks() {
        Mockito.reset(publisherServiceMock);
    }

    @Test
    public void getPublishersOkWhenEmptyResult() {
        List<Publisher> emptyList = TestData.emptyPublisherList;
        Mockito
            .when(publisherServiceMock.getByName("Name does not exists"))
            .thenReturn(emptyList);

        ResponseEntity<List<Publisher>> response = publishersApi.getPublishers(httpServletRequestMock, "Name does not exists", false);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(TestData.emptyPublisherList, response.getBody());
    }

    @Test
    public void getPublishersOkWhenNonEmptyResult() {
        List<Publisher> regnskapList = TestData.publishers;
        Mockito
            .when(publisherServiceMock.getByName("Name exists"))
            .thenReturn(regnskapList);

        ResponseEntity<List<Publisher>> response = publishersApi.getPublishers(httpServletRequestMock, "Name exists", false);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(TestData.publishers,response.getBody());
    }

    @Test
    public void getPublishers500ForExceptions() {
        Mockito
            .when(publisherServiceMock.getByName("Test error"))
            .thenAnswer( invocation -> { throw new Exception("Test error message"); });

        ResponseEntity<List<Publisher>> response = publishersApi.getPublishers(httpServletRequestMock, "Test error", false);

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void getPublisherByIdOkWhenEmptyResult() {
        Mockito
            .when(publisherServiceMock.getById("123Null"))
            .thenReturn(null);

        ResponseEntity<Publisher> response = publishersApi.getPublisherById(httpServletRequestMock, "123Null");

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNull(response.getBody());
    }

    @Test
    public void getPublisherByIdOkWhenNonEmptyResult() {
        Publisher publisher = TestData.publisher;
        Mockito
            .when(publisherServiceMock.getById("123Ok"))
            .thenReturn(publisher);

        ResponseEntity<Publisher> response = publishersApi.getPublisherById(httpServletRequestMock, "123Ok");

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(TestData.publisher, response.getBody());
    }

    @Test
    public void getPublisherById500ForExceptions() {
        Mockito
            .when(publisherServiceMock.getById("123Error"))
            .thenAnswer( invocation -> { throw new Exception("Test error message"); });

        ResponseEntity<Publisher> response = publishersApi.getPublisherById(httpServletRequestMock, "123Error");

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
