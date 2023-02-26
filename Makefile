ROOT_DIR := .

DOCKER_HOSTNAME ?= ghcr.io
DOCKER_NAMESPACE ?= fybrik
DOCKER_TAG ?= 0.0.0
DOCKER_NAME ?= egeria-connector

IMG := ${DOCKER_HOSTNAME}/${DOCKER_NAMESPACE}/${DOCKER_NAME}:${DOCKER_TAG}

.PHONY: build
build:
	mvn clean install -DskipTests

.PHONY: clean
clean:
	mvn clean

.PHONY: run
run:
	nohup java -cp target/fybrik-openapi-spring-1.0.0.jar org.openapitools.OpenAPI2SpringBoot  > output-egeriaconnector.log &  2>&1 ; echo "$$!" >> pids.txt; sleep 5

.PHONY: terminate
terminate:
	kill -9 $$( cat pids.txt ); rm -f pids.txt

.PHONY: docker-build
docker-build: build
	docker build . -t ${IMG}

.PHONY: docker-push
docker-push:
ifneq (${DOCKER_PASSWORD},)
	@docker login \
		--username ${DOCKER_USERNAME} \
		--password ${DOCKER_PASSWORD} ${DOCKER_HOSTNAME}
endif
	docker push ${IMG}

.PHONY: push-to-kind
push-to-kind:
	kind load docker-image ${IMG}
