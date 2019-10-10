package no.brreg.organizationcatalogue.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection="domains")
public class DomainDB {
    @Id private String name;
    private Set<String> organizations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(Set<String> organizations) {
        this.organizations = organizations;
    }
}
