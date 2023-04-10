package org.fybrik.datacatalog.api;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import org.fybrik.datacatalog.model.GetAssetRequest;
import org.fybrik.datacatalog.model.GetAssetResponse;
import org.odpi.openmetadata.egeria.client.EgeriaClient;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.Optional;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.dataCatalogServiceAssetDetails.base-path:}")
public class GetAssetInfoApiController implements GetAssetInfoApi {

    private static final String SERVER_NAME = "omas-server";
    private static final String SERVER_PLATFORM_URL_ROOT = "https://localhost:8081/";
    private static final String USER_ID = "admin";

    private static final Logger LOGGER = LoggerFactory.getLogger(GetAssetInfoApiController.class.getName());

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public GetAssetInfoApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<GetAssetResponse> getAssetInfo(
            @Parameter(description = "", required = true) @RequestHeader(value = "X-Request-Datacatalog-Cred", required = true) String xRequestDatacatalogCred,
            @Parameter(description = "Data Catalog Request Object.", required = true) @Valid @RequestBody GetAssetRequest getAssetRequest) {
        LOGGER.info("Received request in getAssetInfo");

        try {
            EgeriaClient egeriaClient = new EgeriaClient(SERVER_NAME, SERVER_PLATFORM_URL_ROOT);
            GetAssetResponse response = egeriaClient.getAssetResponse(USER_ID, getAssetRequest.getAssetID());

            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Include.NON_NULL);

            String jsonString;
            jsonString = mapper.writeValueAsString(response);

            getRequest().ifPresent(request -> {
                for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                    if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                        ApiUtil.setExampleResponse(request, "application/json", jsonString);
                        break;
                    }
                }
            });

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (InvalidParameterException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (PropertyServerException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UserNotAuthorizedException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}