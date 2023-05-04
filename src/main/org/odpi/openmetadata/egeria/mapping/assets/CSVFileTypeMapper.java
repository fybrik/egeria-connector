package org.odpi.openmetadata.egeria.mapping.assets;

import org.fybrik.datacatalog.model.Connection;
import org.fybrik.datacatalog.model.ResourceDetails;
import org.fybrik.datacatalog.model.ResourceMetadata;
import org.odpi.openmetadata.accessservices.assetcatalog.model.AssetCatalogBean;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.frameworkservices.ocf.metadatamanagement.client.ConnectedAssetUniverse;

/**
 * AssetTypeMapper for mapping Egeria CSV File assets
 */
public class CSVFileTypeMapper extends AssetTypeMapper {

    private ConnectedAssetUniverse assetUniverse;

    public CSVFileTypeMapper(AssetCatalogBean requestedEntity, String serverName, String serverPlatformURLRoot, String userId) throws InvalidParameterException {
        super(requestedEntity, serverName, serverPlatformURLRoot, userId);
    }

    @Override
    public ResourceDetails getResourceDetails() throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        ResourceDetails details = new ResourceDetails();

        if (assetUniverse == null)
            assetUniverse = getAssetUniverse(requestedEntity.getGuid());

        details.setDataFormat("csv");
        Connection connection = getFybrikConnection(assetUniverse);
        details.setConnection(connection);

        return details;
    }

    @Override
    public ResourceMetadata getResourceMetadata() throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        ResourceMetadata resourceMetadata = new ResourceMetadata();

        if (assetUniverse == null)
            assetUniverse = getAssetUniverse(requestedEntity.getGuid());

        resourceMetadata.setName(assetUniverse.getQualifiedName());
        resourceMetadata.setGeography(getGeography(assetUniverse));
        resourceMetadata.setTags(getAssetTags(assetUniverse));
        resourceMetadata.setColumns(getColumns(assetUniverse));

        return resourceMetadata;
    }

    @Override
    public String getCredentials() throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        if (assetUniverse == null)
            assetUniverse = getAssetUniverse(requestedEntity.getGuid());

        return getCredentials(assetUniverse);
    }

}
