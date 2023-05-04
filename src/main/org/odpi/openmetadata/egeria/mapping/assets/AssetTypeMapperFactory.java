package org.odpi.openmetadata.egeria.mapping.assets;

import org.odpi.openmetadata.accessservices.assetcatalog.model.AssetCatalogBean;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;

/**
 * Factory pattern for creating AssetTypeMapper objects based on the asset type
 */
public class AssetTypeMapperFactory {

    private static final String RELATIONAL_TABLE_TYPE = "RelationalTable";
    private static final String CSV_FILE_TYPE = "CSVFile";

    /**
     * Creates an AssetTypeMapper subclass based on the provided asset type.
     *
     * @param assetType String representing the Egeria asset type
     * @param requestedEntity Egeria AssetCatalogBean representation of the requested asset
     * @param serverName name of the Egeria Metadata Store server
     * @param serverPlatformURLRoot URL Root of the Egeria Metadata Store server
     * @param userId identifier of the user making the request
     *
     * @return Implementation of AssetTypeMapper based on the provided assetType parameter
     *
     * @throws InvalidParameterException if a parameter validation fails
     */
    public AssetTypeMapper getMapper(String assetType, AssetCatalogBean requestedEntity, String serverName, String serverPlatformURLRoot, String userId) throws InvalidParameterException {
        switch (assetType) {
            case CSV_FILE_TYPE:
                return new CSVFileTypeMapper(requestedEntity, serverName, serverPlatformURLRoot, userId);
            case RELATIONAL_TABLE_TYPE:
                return new RelationalTableTypeMapper(requestedEntity, serverName, serverPlatformURLRoot, userId);
            default:
                return null;
        }
    }

}
