FROM eclipse-temurin:17-jre-alpine

ENV TZ=Europe/Oslo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

VOLUME /tmp
COPY /target/organization-catalog.jar app.jar

RUN sh -c 'touch /app.jar'
CMD java -jar app.jar
