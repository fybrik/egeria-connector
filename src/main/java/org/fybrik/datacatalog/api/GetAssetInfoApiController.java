package org.fybrik.datacatalog.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import org.fybrik.datacatalog.model.GetAssetRequest;
import org.fybrik.datacatalog.model.GetAssetResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.http.MediaType;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.fybrik.datacatalog.model.Connection;
import org.fybrik.datacatalog.model.ResourceColumn;
import org.fybrik.datacatalog.model.ResourceDetails;
import org.fybrik.datacatalog.model.ResourceMetadata;
import org.fybrik.datacatalog.model.S3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.dataCatalogServiceAssetDetails.base-path:}")
public class GetAssetInfoApiController implements GetAssetInfoApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetAssetInfoApiController.class.getName());

    public ResponseEntity<GetAssetResponse> getAssetInfo(
            @Parameter(description = "", required = true) @RequestHeader(value = "X-Request-Datacatalog-Cred", required = true) String xRequestDatacatalogCred,
            @Parameter(description = "Data Catalog Request Object.", required = true) @Valid @RequestBody GetAssetRequest getAssetRequest) {
        LOGGER.info("Received request in getAssetInfo");

        GetAssetResponse g = new GetAssetResponse();
        ResourceDetails rd = new ResourceDetails();
        ResourceMetadata rm = new ResourceMetadata();

        String credentials = "/v1/kubernetes-secrets/paysim-csv?namespace=fybrik-notebook-sample";
        
        Connection c = new Connection();
        S3 s3 = new S3();
        s3.endpoint("http://localstack.fybrik-notebook-sample.svc.cluster.local:4566");
        s3.bucket("demo");
        s3.objectKey("PS_20174392719_1491204439457_log.csv");

        c.setName("s3");
        c.setS3(s3);

        g.setCredentials(credentials);
        rd.setDataFormat("csv");
        rd.setConnection(c);
        g.setDetails(rd);

        rm.setName("Synthetic Financial Datasets For Fraud Detection");
        rm.setGeography("theshire");
        Map<String, Object> tags = new HashMap<String, Object>();
        tags.put("Purpose.finance", "true");
        rm.setTags(tags);

        List<ResourceColumn> columns = new ArrayList<ResourceColumn>();
        Map<String, Object> columnTags = new HashMap<String, Object>();
        columnTags.put("PII.Sensitive", "true");
        ResourceColumn rc1 = new ResourceColumn();
        ResourceColumn rc2 = new ResourceColumn();
        ResourceColumn rc3 = new ResourceColumn();
        rc1.setName("nameOrig");
        rc1.setTags(columnTags);
        rc2.setName("oldbalanceOrg");
        rc2.setTags(columnTags);
        rc3.setName("newbalanceOrig");
        rc3.setTags(columnTags);
        columns.add(rc1);
        columns.add(rc2);
        columns.add(rc3);

        rm.setColumns(columns);

        g.setResourceMetadata(rm);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);

        String jsonString;
        try {
            jsonString = mapper.writeValueAsString(g);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    //String exampleString = "{ \"resourceMetadata\" : { \"owner\" : \"owner\", \"columns\" : [ { \"name\" : \"name\", \"tags\" : { \"region\" : \"region\", \"confidential\" : true } }, { \"name\" : \"name\", \"tags\" : { \"region\" : \"region\", \"confidential\" : true } } ], \"geography\" : \"geography\", \"name\" : \"name\", \"tags\" : { \"region\" : \"region\", \"confidential\" : true } }, \"credentials\" : \"credentials\", \"details\" : { \"connection\" : { \"additionalProperties\" : { \"datasourceType\" : \"datasourceType\", \"connectionString\" : \"connectionString\", \"connectionProperties\" : { \"s3\" : { \"bucket\" : \"bucket\", \"secret_key\" : \"secret_key\", \"endpoint\" : \"endpoint\", \"object_key\" : \"object_key\", \"api_key\" : \"api_key\", \"resource_instance_id\" : \"resource_instance_id\", \"access_key\" : \"access_key\", \"region\" : \"region\" }, \"db2\" : { \"database\" : \"database\", \"port\" : \"port\", \"ssl\" : \"ssl\", \"table\" : \"table\", \"url\" : \"url\" }, \"fybrik-arrow-flight\" : { \"hostname\" : \"hostname\", \"scheme\" : \"scheme\", \"port\" : \"port\" }, \"kafka\" : { \"schema_registry\" : \"schema_registry\", \"key_deserializer\" : \"key_deserializer\", \"ssl_truststore_password\" : \"ssl_truststore_password\", \"value_deserializer\" : \"value_deserializer\", \"sasl_mechanism\" : \"sasl_mechanism\", \"security_protocol\" : \"security_protocol\", \"topic_name\" : \"topic_name\", \"bootstrap_servers\" : \"bootstrap_servers\", \"ssl_truststore\" : \"ssl_truststore\" } }, \"assetProperties\" : [ { \"name\" : \"name\", \"value\" : \"value\" }, { \"name\" : \"name\", \"value\" : \"value\" } ], \"columns\" : [ { \"name\" : \"name\", \"type\" : { \"nullable\" : true, \"length\" : \"\", \"scale\" : \"\", \"signed\" : true, \"type\" : \"type\" } }, { \"name\" : \"name\", \"type\" : { \"nullable\" : true, \"length\" : \"\", \"scale\" : \"\", \"signed\" : true, \"type\" : \"type\" } } ] } } } }";
                    ApiUtil.setExampleResponse(request, "application/json", jsonString);
                    break;
                }
            }
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
