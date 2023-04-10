package org.odpi.openmetadata.egeria.mapping.connections;

import org.fybrik.datacatalog.model.Connection;
import org.fybrik.datacatalog.model.Mysql;
import org.odpi.openmetadata.egeria.mapping.utils.MappingUtils;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.Endpoint;
import org.odpi.openmetadata.frameworkservices.ocf.metadatamanagement.client.ConnectedAssetUniverse;

import java.util.Map;

/**
 * ConnectionTypeMapper for mapping a MySQL connection.
 */
public class MySQLConnectionTypeMapper extends ConnectionTypeMapper {

    public MySQLConnectionTypeMapper(Map<String, Object> additionalConnectionProperties) {
        super(additionalConnectionProperties);
    }

    @Override
    public Connection mapConnectionType(ConnectedAssetUniverse assetUniverse) {
        org.odpi.openmetadata.frameworks.connectors.properties.beans.Connection egeriaConnection = MappingUtils.INSTANCE.getEgeriaConnectionFromAssetUniverse(assetUniverse);
        if (egeriaConnection == null)
            return null;

        Connection fybrikConnection = new Connection();
        fybrikConnection.setName(MY_SQL);
        fybrikConnection.setMysql(getMySQLConnection(assetUniverse, egeriaConnection));

        return fybrikConnection;
    }

    /**
     * Creates and populates Mysql object using an Egeria Connection and other relevant data from an Egeria asset represented as
     * a ConnectedAssetUniverse object.
     *
     * @param assetUniverse ConnectedAssetUniverse representation of an Egeria asset
     * @param egeriaConnection Egeria Connection which will be mapped
     *
     * @return Populated Mysql object
     */
    private Mysql getMySQLConnection(ConnectedAssetUniverse assetUniverse, org.odpi.openmetadata.frameworks.connectors.properties.beans.Connection egeriaConnection) {
        Mysql mysql = new Mysql();

        Endpoint endpoint = egeriaConnection.getEndpoint();
        if (endpoint != null) {
            mysql.setHost(endpoint.getAddress());

            Map<String, String> additionalProperties = endpoint.getAdditionalProperties();
            if (additionalProperties != null) {
                String port = additionalProperties.get("port");
                mysql.setPort(Integer.parseInt(port));
            }
        }

        // This will not make sense in the case of CSV files, have to get a few things clear to redesign it
        mysql.setDatabase(assetUniverse.getQualifiedName());
        mysql.setTable((String) additionalConnectionProperties.getOrDefault("table", null));

        return mysql;
    }

}
