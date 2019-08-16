package no.publishers.model;

import javax.validation.constraints.NotBlank;

import no.publishers.generated.model.Code;
import no.publishers.generated.model.PrefLabel;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="publisher")
public class PublisherDB {
    @Id private ObjectId id;
    @NotBlank private String name;
    private String uri;
    @Indexed(unique = true) @NotBlank private String organizationId;
    private String orgForm;
    private String orgPath;
    private String orgParent;
    private String municipalityNumber;
    private Code industryCode;
    private Code sectorCode;
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

    public String getOrgForm() {
        return orgForm;
    }

    public void setOrgForm(String orgForm) {
        this.orgForm = orgForm;
    }

    public String getOrgPath() {
        return orgPath;
    }

    public void setOrgPath(String orgPath) {
        this.orgPath = orgPath;
    }

    public String getOrgParent() {
        return orgParent;
    }

    public void setOrgParent(String orgParent) {
        this.orgParent = orgParent;
    }

    public String getMunicipalityNumber() {
        return municipalityNumber;
    }

    public void setMunicipalityNumber(String municipalityNumber) {
        this.municipalityNumber = municipalityNumber;
    }

    public Code getIndustryCode() {
        return industryCode;
    }

    public void setIndustryCode(Code industryCode) {
        this.industryCode = industryCode;
    }

    public Code getSectorCode() {
        return sectorCode;
    }

    public void setSectorCode(Code sectorCode) {
        this.sectorCode = sectorCode;
    }

    public PrefLabel getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(PrefLabel prefLabel) {
        this.prefLabel = prefLabel;
    }
}