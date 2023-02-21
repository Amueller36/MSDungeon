FROM maven:3.8.7-openjdk-18 AS build

## download dependencies



ADD pom.xml /
RUN mvn clean

## build after dependencies are down so it wont redownload unless the POM changes
ADD . /
RUN mvn package

FROM eclipse-temurin:18
#
ENV GAME_HOST=http://game:8080
ENV RABBITMQ_HOST=rabbitmq
ENV RABBITMQ_PORT=5672
ENV RABBITMQ_USERNAME=admin
ENV RABBITMQ_PASSWORD=admin
ARG GAME_HOST
ARG RABBITMQ_HOST
ARG RABBITMQ_PORT
ARG RABBITMQ_USERNAME
ARG RABBITMQ_PASSWORD

EXPOSE 8090
WORKDIR /root/
## COPY packaged JAR file and rename as app.jar
## → this relies on your MAVEN package command building a jar
## that matches *-jar-with-dependencies.jar with a single match
COPY --from=build /target/*-jar-with-dependencies.jar app.jar
#add network dungeon
ENTRYPOINT ["java","-jar", "./app.jar"]
