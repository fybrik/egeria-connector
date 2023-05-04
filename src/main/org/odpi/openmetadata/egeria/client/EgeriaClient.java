package org.odpi.openmetadata.egeria.client;

import org.fybrik.datacatalog.model.GetAssetResponse;
import org.odpi.openmetadata.egeria.mapping.FybrikMapper;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;

/**
 * Client used to communicate with the Egeria Metadata Store.
 */
public class EgeriaClient {

    private final FybrikMapper fybrikMapper;

    public EgeriaClient(String serverName, String serverPlatformURLRoot)
            throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        fybrikMapper = new FybrikMapper(serverName, serverPlatformURLRoot);
    }

    /**
     * Request an asset response from the Egeria Metadata Store.
     *
     * @param userId identifier of the user making the request
     * @param assetGuid identifier of the requested asset
     *
     * @return GetAssetResponse object containing the requested asset's fields
     *
     * @throws InvalidParameterException if a parameter validation fails
     * @throws PropertyServerException if an error occurs while serving the request
     * @throws UserNotAuthorizedException if the user is not authorized to make the request
     */
    public GetAssetResponse getAssetResponse(String userId, String assetGuid) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        return fybrikMapper.getAssetResponse(userId, assetGuid);
    }

}
