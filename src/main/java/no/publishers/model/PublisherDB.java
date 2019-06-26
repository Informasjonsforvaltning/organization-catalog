package no.publishers.model;

import no.publishers.generated.model.PrefLabel;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="publisher")
public class PublisherDB {
    @Id
    private ObjectId id;
    @Indexed
    private String name;
    private String uri;
    private String organizationId;
    private String orgPath;
    private PrefLabel prefLabel;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrgPath() {
        return orgPath;
    }

    public void setOrgPath(String orgPath) {
        this.orgPath = orgPath;
    }

    public PrefLabel getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(PrefLabel prefLabel) {
        this.prefLabel = prefLabel;
    }
}