ARG REGISTRY=docker.io

FROM ${REGISTRY}/openjdk:8u131-jre-alpine

ENV HOME=/tmp
WORKDIR /tmp

COPY src/main/resources/* src/main/resources/
COPY target/ target/

ENV SPRING_CONFIG_NAME "bootstrap-config,application"

RUN apk add --no-cache curl
USER 10001
EXPOSE 8080

ENTRYPOINT ["java", "-cp", "src/main/lib/*:target/libs/*:target/classes:target/fybrik-openapi-spring-1.0.0.jar", "org.openapitools.OpenAPI2SpringBoot"]

