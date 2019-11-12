package no.brreg.informasjonsforvaltning.organizationcatalogue.model;

import javax.validation.constraints.NotBlank;

import no.brreg.informasjonsforvaltning.organizationcatalogue.generated.model.PrefLabel;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Set;

@Document(collection="organizations")
public class OrganizationDB {
    @Id private String organizationId;
    @NotBlank private String name;
    private String internationalRegistry;
    private String orgType;
    private String orgPath;
    private String subOrganizationOf;
    private LocalDate issued;
    private String municipalityNumber;
    private String industryCode;
    private String sectorCode;
    private PrefLabel prefLabel;
    private Set<String> domains;
    private Boolean allowDelegatedRegistration;

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInternationalRegistry() {
        return internationalRegistry;
    }

    public void setInternationalRegistry(String internationalRegistry) {
        this.internationalRegistry = internationalRegistry;
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

    public String getMunicipalityNumber() {
        return municipalityNumber;
    }

    public void setMunicipalityNumber(String municipalityNumber) {
        this.municipalityNumber = municipalityNumber;
    }

    public String getIndustryCode() {
        return industryCode;
    }

    public void setIndustryCode(String industryCode) {
        this.industryCode = industryCode;
    }

    public String getSectorCode() {
        return sectorCode;
    }

    public void setSectorCode(String sectorCode) {
        this.sectorCode = sectorCode;
    }

    public PrefLabel getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(PrefLabel prefLabel) {
        this.prefLabel = prefLabel;
    }

    public Set<String> getDomains() {
        return domains;
    }

    public void setDomains(Set<String> domains) {
        this.domains = domains;
    }

    public Boolean getAllowDelegatedRegistration() {
        return allowDelegatedRegistration;
    }

    public void setAllowDelegatedRegistration(Boolean allowDelegatedRegistration) {
        this.allowDelegatedRegistration = allowDelegatedRegistration;
    }
}
