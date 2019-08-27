package no.publishers.service;

import no.publishers.generated.model.Publisher;
import no.publishers.model.PublisherDB;
import no.publishers.repository.PublisherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static no.publishers.TestDataKt.getPUBLISHER_0;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static no.publishers.TestDataKt.getPUBLISHER_DB_0;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
@Tag("unit")
public class PublisherServiceTest {

    @Mock
    private PublisherRepository publisherRepository;

    @InjectMocks
    private PublisherService publisherService;

    @BeforeEach
    public void resetMocks() {
        Mockito.reset(publisherRepository);
    }

    @Test
    public void getByIdNotFound() {
        Mockito
            .when(publisherRepository.findById("123ID"))
            .thenReturn(Optional.empty());

        Publisher publisher = publisherService.getById("123ID");

        assertNull(publisher);
    }

    @Test
    public void getById() {
        PublisherDB persisted = getPUBLISHER_DB_0();
        Mockito
            .when(publisherRepository.findById("123ID"))
            .thenReturn(Optional.of(persisted));

        Publisher publisher = publisherService.getById("123ID");

        assertEquals(persisted.getId().toHexString(), publisher.getId());
        assertEquals(persisted.getName(), publisher.getName());
        assertEquals(persisted.getOrganizationId(), publisher.getOrganizationId());
        assertEquals(persisted.getOrgPath(), publisher.getOrgPath());
        assertEquals(persisted.getPrefLabel(), publisher.getPrefLabel());
        assertEquals(persisted.getUri(), publisher.getUri());
    }

    @Test
    public void getAll() {
        List<PublisherDB> persistedList = Collections.singletonList(getPUBLISHER_DB_0());
        Mockito
            .when(publisherRepository.findAll())
            .thenReturn(persistedList);

        List<Publisher> publisherList = publisherService.getPublishers(null, null);

        assertEquals(persistedList.get(0).getId().toHexString(), publisherList.get(0).getId());
        assertEquals(persistedList.get(0).getName(), publisherList.get(0).getName());
        assertEquals(persistedList.get(0).getOrganizationId(), publisherList.get(0).getOrganizationId());
        assertEquals(persistedList.get(0).getOrgPath(), publisherList.get(0).getOrgPath());
        assertEquals(persistedList.get(0).getPrefLabel(), publisherList.get(0).getPrefLabel());
        assertEquals(persistedList.get(0).getUri(), publisherList.get(0).getUri());
    }

    @Test
    public void getByOrgIdIsPrioritized() {
        List<PublisherDB> persistedList = Collections.singletonList(getPUBLISHER_DB_0());
        Mockito
            .when(publisherRepository.findByOrganizationIdLike("OrgId"))
            .thenReturn(persistedList);

        List<Publisher> publisherList = publisherService.getPublishers("Name", "OrgId");

        assertEquals(persistedList.get(0).getId().toHexString(), publisherList.get(0).getId());
        assertEquals(persistedList.get(0).getName(), publisherList.get(0).getName());
        assertEquals(persistedList.get(0).getOrganizationId(), publisherList.get(0).getOrganizationId());
        assertEquals(persistedList.get(0).getOrgPath(), publisherList.get(0).getOrgPath());
        assertEquals(persistedList.get(0).getPrefLabel(), publisherList.get(0).getPrefLabel());
        assertEquals(persistedList.get(0).getUri(), publisherList.get(0).getUri());
    }

    @Test
    public void getByName() {
        List<PublisherDB> persistedList = Collections.singletonList(getPUBLISHER_DB_0());
        Mockito
            .when(publisherRepository.findByNameLike("Name"))
            .thenReturn(persistedList);

        List<Publisher> publisherList = publisherService.getPublishers("Name", null);

        assertEquals(persistedList.get(0).getId().toHexString(), publisherList.get(0).getId());
        assertEquals(persistedList.get(0).getName(), publisherList.get(0).getName());
        assertEquals(persistedList.get(0).getOrganizationId(), publisherList.get(0).getOrganizationId());
        assertEquals(persistedList.get(0).getOrgPath(), publisherList.get(0).getOrgPath());
        assertEquals(persistedList.get(0).getPrefLabel(), publisherList.get(0).getPrefLabel());
        assertEquals(persistedList.get(0).getUri(), publisherList.get(0).getUri());
    }

    @Test
    public void updateNotFound() {
        Mockito
            .when(publisherRepository.findById("123ID"))
            .thenReturn(Optional.empty());

        Publisher publisher = publisherService.updatePublisher("123ID", getPUBLISHER_0());

        assertNull(publisher);
    }
}
