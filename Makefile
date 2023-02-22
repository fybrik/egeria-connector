ROOT_DIR := .


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
