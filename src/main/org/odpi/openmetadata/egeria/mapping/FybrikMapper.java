package org.odpi.openmetadata.egeria.mapping;

import org.fybrik.datacatalog.model.GetAssetResponse;
import org.odpi.openmetadata.accessservices.assetcatalog.AssetCatalog;
import org.odpi.openmetadata.accessservices.assetcatalog.model.AssetCatalogBean;
import org.odpi.openmetadata.accessservices.assetcatalog.model.rest.responses.AssetCatalogResponse;
import org.odpi.openmetadata.egeria.mapping.assets.AssetTypeMapper;
import org.odpi.openmetadata.egeria.mapping.assets.AssetTypeMapperFactory;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;

/**
 * Handles data mapping between Fybrik Assets and Egeria Entities
 */
public class FybrikMapper {

    private final String serverName;
    private final String serverPlatformURLRoot;

    public FybrikMapper(String serverName, String serverPlatformURLRoot) {
        this.serverName = serverName;
        this.serverPlatformURLRoot = serverPlatformURLRoot;
    }

    /**
     * Request an asset response from the Egeria Metadata Store and map its data to the corresponding Fybrik Asset fields.
     *
     * @param userId identifier of the user making the request
     * @param entityGUID identifier of the requested entity
     *
     * @return GetAssetResponse object containing the requested asset's fields
     *
     * @throws InvalidParameterException if a parameter validation fails
     * @throws PropertyServerException if an error occurs while serving the request
     * @throws UserNotAuthorizedException if the user is not authorized to make the request
     */
    public GetAssetResponse getAssetResponse(String userId, String entityGUID) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        GetAssetResponse response = new GetAssetResponse();

        AssetCatalog assetCatalogClient = new AssetCatalog(serverName, serverPlatformURLRoot);
        AssetCatalogResponse assetCatalogResponse = assetCatalogClient.getAssetDetails(userId, entityGUID, "none");
        AssetCatalogBean requestedEntity = assetCatalogResponse.getAssetCatalogBean();

        String assetType = requestedEntity.getType().getName();
        AssetTypeMapperFactory assetTypeMapperFactory = new AssetTypeMapperFactory();
        AssetTypeMapper assetTypeMapper = assetTypeMapperFactory.getMapper(assetType, requestedEntity, serverName, serverPlatformURLRoot, userId);

        if (assetTypeMapper != null) {
            response.setCredentials(assetTypeMapper.getCredentials());
            response.setDetails(assetTypeMapper.getResourceDetails());
            response.setResourceMetadata(assetTypeMapper.getResourceMetadata());

            return response;
        }

        return null;
    }

}
