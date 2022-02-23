# Organization Catalog

Provides search and edit functionality for the current organization catalog in the National Data Catalog

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

## Nice to have
#### MongoDB
https://docs.mongodb.com/manual/installation

## Run locally in IDEA
Start local instances of SSO, MongoDB and enhetsregisteret-proxy-mock
```
% docker-compose up -d
```
-d enables "detached mode"

### Login for https://docker.pkg.github.com
The image for auth-utils-java is downloaded from github, where you need to log in.
First you need an access token from Github. Go to settings for your user in github.com -> Developer settings -> Personal access tokens where you generate a token with access to read packages.
Then you run docker login and paste the access token when it prompts you for a password.
```
% docker login https://docker.pkg.github.com -u {USERNAME}
```

### Run the application
```
mvn spring-boot:run -Dspring-boot.run.profiles=develop
```

## Get an admin jwt from auth-utils-java:
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