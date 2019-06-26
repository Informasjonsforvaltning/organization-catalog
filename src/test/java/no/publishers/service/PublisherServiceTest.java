package no.publishers.service;

import no.publishers.TestData;
import no.publishers.generated.model.Publisher;
import no.publishers.model.PublisherDB;
import no.publishers.repository.PublisherRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTest.class)
public class PublisherServiceTest {

    @Mock
    private PublisherRepository publisherRepository;

    @InjectMocks
    private PublisherService publisherService;

    @Before
    public void resetMocks() {
        Mockito.reset(publisherRepository);
    }

    @Test
    public void getById() {
        PublisherDB persisted = TestData.PUBLISHER_DB;
        Mockito
            .when(publisherRepository.findById("123ID"))
            .thenReturn(Optional.of(persisted));

        Publisher publisher = publisherService.getById("123ID").get();

        Assert.assertEquals(persisted.getId().toHexString(), publisher.getId());
        Assert.assertEquals(persisted.getName(), publisher.getName());
        Assert.assertEquals(persisted.getOrganizationId(), publisher.getOrganizationId());
        Assert.assertEquals(persisted.getOrgPath(), publisher.getOrgPath());
        Assert.assertEquals(persisted.getPrefLabel(), publisher.getPrefLabel());
        Assert.assertEquals(persisted.getUri(), publisher.getUri());
    }

    @Test
    public void getAll() {
        List<PublisherDB> persistedList = Collections.singletonList(TestData.PUBLISHER_DB);
        Mockito
            .when(publisherRepository.findAll())
            .thenReturn(persistedList);

        List<Publisher> publisherList = publisherService.getPublishers(null, null);

        Assert.assertEquals(persistedList.get(0).getId().toHexString(), publisherList.get(0).getId());
        Assert.assertEquals(persistedList.get(0).getName(), publisherList.get(0).getName());
        Assert.assertEquals(persistedList.get(0).getOrganizationId(), publisherList.get(0).getOrganizationId());
        Assert.assertEquals(persistedList.get(0).getOrgPath(), publisherList.get(0).getOrgPath());
        Assert.assertEquals(persistedList.get(0).getPrefLabel(), publisherList.get(0).getPrefLabel());
        Assert.assertEquals(persistedList.get(0).getUri(), publisherList.get(0).getUri());
    }

    @Test
    public void getByOrgIdIsPrioritized() {
        List<PublisherDB> persistedList = Collections.singletonList(TestData.PUBLISHER_DB);
        Mockito
            .when(publisherRepository.findByOrganizationIdLike("OrgId"))
            .thenReturn(persistedList);

        List<Publisher> publisherList = publisherService.getPublishers("Name", "OrgId");

        Assert.assertEquals(persistedList.get(0).getId().toHexString(), publisherList.get(0).getId());
        Assert.assertEquals(persistedList.get(0).getName(), publisherList.get(0).getName());
        Assert.assertEquals(persistedList.get(0).getOrganizationId(), publisherList.get(0).getOrganizationId());
        Assert.assertEquals(persistedList.get(0).getOrgPath(), publisherList.get(0).getOrgPath());
        Assert.assertEquals(persistedList.get(0).getPrefLabel(), publisherList.get(0).getPrefLabel());
        Assert.assertEquals(persistedList.get(0).getUri(), publisherList.get(0).getUri());
    }

    @Test
    public void getByName() {
        List<PublisherDB> persistedList = Collections.singletonList(TestData.PUBLISHER_DB);
        Mockito
            .when(publisherRepository.findByNameLike("Name"))
            .thenReturn(persistedList);

        List<Publisher> publisherList = publisherService.getPublishers("Name", null);

        Assert.assertEquals(persistedList.get(0).getId().toHexString(), publisherList.get(0).getId());
        Assert.assertEquals(persistedList.get(0).getName(), publisherList.get(0).getName());
        Assert.assertEquals(persistedList.get(0).getOrganizationId(), publisherList.get(0).getOrganizationId());
        Assert.assertEquals(persistedList.get(0).getOrgPath(), publisherList.get(0).getOrgPath());
        Assert.assertEquals(persistedList.get(0).getPrefLabel(), publisherList.get(0).getPrefLabel());
        Assert.assertEquals(persistedList.get(0).getUri(), publisherList.get(0).getUri());
    }
}
