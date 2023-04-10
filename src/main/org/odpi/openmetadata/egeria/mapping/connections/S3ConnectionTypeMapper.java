package org.odpi.openmetadata.egeria.mapping.connections;

import org.fybrik.datacatalog.model.Connection;
import org.fybrik.datacatalog.model.S3;
import org.odpi.openmetadata.egeria.mapping.utils.MappingUtils;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.Endpoint;
import org.odpi.openmetadata.frameworkservices.ocf.metadatamanagement.client.ConnectedAssetUniverse;

import java.util.Map;

/**
 * ConnectionTypeMapper for mapping a S3 connection.
 */
public class S3ConnectionTypeMapper extends ConnectionTypeMapper {

    public S3ConnectionTypeMapper(Map<String, Object> additionalConnectionProperties) {
        super(additionalConnectionProperties);
    }

    @Override
    public Connection mapConnectionType(ConnectedAssetUniverse assetUniverse) {
        org.odpi.openmetadata.frameworks.connectors.properties.beans.Connection egeriaConnection = MappingUtils.INSTANCE.getEgeriaConnectionFromAssetUniverse(assetUniverse);
        if (egeriaConnection == null)
            return null;

        Connection fybrikConnection = new Connection();
        fybrikConnection.setName(S3);
        fybrikConnection.s3(getS3Connection(egeriaConnection));

        return fybrikConnection;
    }

    /**
     * Creates and populates an S3 object using an Egeria Connection object.
     *
     * @param egeriaConnection Egeria Connection object which will be mapped
     *
     * @return Populated S3 object
     */
    private S3 getS3Connection(org.odpi.openmetadata.frameworks.connectors.properties.beans.Connection egeriaConnection) {
        S3 s3 = new S3();

        Endpoint endpoint = egeriaConnection.getEndpoint();
        if (endpoint != null)
            s3.setEndpoint(endpoint.getAddress());

        Map<String, String> additionalProperties = egeriaConnection.getAdditionalProperties();
        if (additionalProperties != null) {
            s3.setObjectKey(additionalProperties.get("object_key"));
            s3.setBucket(additionalProperties.get("bucket"));
        }

        return s3;
    }

}
