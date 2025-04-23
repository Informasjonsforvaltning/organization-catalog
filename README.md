# Organization Catalog

This application provides a common API for the management of organization data and is used by many other applications in
the overall architecture. The application has an integration
to [Enhetsregisteret](https://data.brreg.no/enhetsregisteret/api/dokumentasjon/no/index.html).

For a broader understanding of the system’s context, refer to
the [architecture documentation](https://github.com/Informasjonsforvaltning/architecture-documentation) wiki.

## Getting Started

These instructions will give you a copy of the project up and running on your local machine for development and testing
purposes.

### Prerequisites

Ensure you have the following installed:

- Java 21
- Maven
- Docker

### Running locally

Clone the repository

```sh
git clone https://github.com/Informasjonsforvaltning/organization-catalog.git
cd organization-catalog
```

Start MongoDB and the application (either through your IDE using the dev profile, or via CLI):

```sh
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=develop
```

### API Documentation (OpenAPI)

The API documentation is available at ```src/main/resources/specification```.

### Running tests

```sh
mvn verify
```
