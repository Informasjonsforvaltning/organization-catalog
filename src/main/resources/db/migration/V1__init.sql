CREATE TABLE organizations (
    organization_id              VARCHAR(255) PRIMARY KEY,
    name                         VARCHAR(500) NOT NULL,
    international_registry       VARCHAR(500),
    org_type                     VARCHAR(50),
    org_path                     VARCHAR(500),
    sub_organization_of          VARCHAR(500),
    issued                       DATE,
    municipality_number          VARCHAR(20),
    industry_code                VARCHAR(50),
    sector_code                  VARCHAR(50),
    pref_label_nb                VARCHAR(500),
    pref_label_nn                VARCHAR(500),
    pref_label_en                VARCHAR(500),
    org_status                   VARCHAR(50),
    homepage                     VARCHAR(500),
    subordinate                  BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE org_pref_labels (
    organization_id   VARCHAR(255) PRIMARY KEY,
    nb                VARCHAR(500),
    nn                VARCHAR(500),
    en                VARCHAR(500)
);

CREATE INDEX idx_org_name ON organizations (name);
