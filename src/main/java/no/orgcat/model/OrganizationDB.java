package no.orgcat.model;

import javax.validation.constraints.NotBlank;

import no.orgcat.generated.model.PrefLabel;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection="catalogue")
public class OrganizationDB {
    @Id private ObjectId id;
    @NotBlank private String name;
    private String uri;
    @Indexed(unique = true) @NotBlank private String organizationId;
    private String orgType;
    private String orgPath;
    private String subOrganizationOf;
    private LocalDate issued;
    private String uriMunicipalityNumber;
    private String uriIndustryCode;
    private String uriSectorCode;
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

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getOrgPath() {
        return orgPath;
    }

    public void setOrgPath(String orgPath) {
        this.orgPath = orgPath;
    }

    public String getSubOrganizationOf() {
        return subOrganizationOf;
    }

    public void setSubOrganizationOf(String subOrganizationOf) {
        this.subOrganizationOf = subOrganizationOf;
    }

    public LocalDate getIssued() {
        return issued;
    }

    public void setIssued(LocalDate issued) {
        this.issued = issued;
    }

    public String getUriMunicipalityNumber() {
        return uriMunicipalityNumber;
    }

    public void setUriMunicipalityNumber(String uriMunicipalityNumber) {
        this.uriMunicipalityNumber = uriMunicipalityNumber;
    }

    public String getUriIndustryCode() {
        return uriIndustryCode;
    }

    public void setUriIndustryCode(String uriIndustryCode) {
        this.uriIndustryCode = uriIndustryCode;
    }

    public String getUriSectorCode() {
        return uriSectorCode;
    }

    public void setUriSectorCode(String uriSectorCode) {
        this.uriSectorCode = uriSectorCode;
    }

    public PrefLabel getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(PrefLabel prefLabel) {
        this.prefLabel = prefLabel;
    }
}