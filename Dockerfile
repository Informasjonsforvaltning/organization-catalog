FROM openjdk:8-jre

ENV TZ=Europe/Oslo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

VOLUME /tmp
COPY /target/organization-catalogue.jar app.jar

RUN sh -c 'touch /app.jar'
CMD java -jar app.jar