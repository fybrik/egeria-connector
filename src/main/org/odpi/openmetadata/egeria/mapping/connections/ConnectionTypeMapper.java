package org.odpi.openmetadata.egeria.mapping.connections;

import org.fybrik.datacatalog.model.Connection;
import org.odpi.openmetadata.frameworkservices.ocf.metadatamanagement.client.ConnectedAssetUniverse;

import java.util.Map;

/**
 * Base class for every connection mapper type which contains common fields and mapping methods.
 * As of now, the mapping logic is one-directional, from Egeria Connections to Fybrik Connections.
 */
public abstract class ConnectionTypeMapper {

    public static final String S3 = "s3";
    public static final String MY_SQL = "mysql";

    protected Map<String, Object> additionalConnectionProperties;

    public ConnectionTypeMapper(Map<String, Object> additionalConnectionProperties) {
        this.additionalConnectionProperties = additionalConnectionProperties;
    }

    /**
     * Creates a Fybrik Connection object using the Egeria Connection and other relevant data extracted
     * from a ConnectedAssetUniverse object.
     *
     * @param assetUniverse ConnectedAssetUniverse object representation of an Egeria Asset
     */
    public abstract Connection mapConnectionType(ConnectedAssetUniverse assetUniverse);

}
