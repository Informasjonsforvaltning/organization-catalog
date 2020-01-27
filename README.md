# Organization Catalogue

Provides search and edit functionality for the current organization catalogue in the National Data Catalog

# Local development

## Install java, git, maven, docker and docker-compose

##### Linux
```
sudo apt update
sudo apt-get update
sudo apt-get upgrade
sudo apt-get install default-jdk git maven docker.io docker-compose
```

##### Windows
Git for Windows - https://gitforwindows.org/

Apache Maven - http://maven.apache.org/download.cgi

Docker for Windows - https://hub.docker.com/editions/community/docker-ce-desktop-windows

## Steps only necessary for Linux

##### Configure Docker to start on Boot
```
systemctl start docker
systemctl enable docker
```

##### Enable executing Docker and Maven without sudo
```
sudo adduser ${USER} docker
sudo adduser ${USER} mvn
```

Check that they have been added with "id -nG", force the update with a reboot or with "su - ${USER}"

## Environment variables
These are needed to connect to the local database
```
MONGO_USERNAME="pub_db"
MONGO_PASSWORD="Passw0rd"
```

##### Linux
Open ~/.bashrc and add the lines
```
export MONGO_USERNAME="pub_db"
export MONGO_PASSWORD="Passw0rd"
```
Update from ~/.bashrc with
```
source ~/.bashrc
```

Check that they have been added with "printenv"

##### Windows
“Advanced system settings” → “Environment Variables”

## Nice to have
#### Postman
https://www.getpostman.com/

#### MongoDB
https://docs.mongodb.com/manual/installation

## Run locally in IDEA
Start local instances of SSO, MongoDB and enhetsregisteret-proxy-mock
```
% docker-compose up -d
```
-d enables "detached mode"

Add `-Dspring.profiles.active=develop` as a VM option in the Run/Debug Configuration

Run (Shift+F10) or debug (Shift+F9) the application

## Test that everything is running
"/src/main/resources/specification/examples/PublishersAPI.postman_collection.json"

Import this collection in Postman to test the api locally.

## Get an admin jwt:
```
% curl localhost:8084/jwt/admin -o jwt.txt
```

## Helpful commands

Populate organizations cache, just run get

```
curl localhost:8140/organizations/910244132 -H "accept:application/json"
```

Get all cached organizations
```
curl localhost:8140/organizations -H "accept:application/json"
```

Add a domain to organization, replace <token> with generated jwt

```
curl localhost:8140/domains -d '{"name":"ramrog.no", "organizations":["910244132"]}' -H 'content-type:application/json' -H 'Authorization: Bearer <token>' 
```

Get organizations for domain

```
curl localhost:8140/domains/brreg.no/organizations -H "accept:application/json"
```