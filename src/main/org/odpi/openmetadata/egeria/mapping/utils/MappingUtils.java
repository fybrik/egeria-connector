package org.odpi.openmetadata.egeria.mapping.utils;

import org.odpi.openmetadata.frameworks.connectors.properties.Connections;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.Connection;
import org.odpi.openmetadata.frameworkservices.ocf.metadatamanagement.client.ConnectedAssetUniverse;

public enum MappingUtils {

    INSTANCE;

    /**
     * Utility method for extracting an Egeria Connection out of an Asset represented by a ConnectedAssetUniverse object.
     *
     * @param assetUniverse ConnectedAssetUniverse representation of an asset
     *
     * @return Egeria Connection object
     */
    public Connection getEgeriaConnectionFromAssetUniverse(ConnectedAssetUniverse assetUniverse) {
        Connections connections = assetUniverse.getConnections();
        if (connections == null)
            return null;

        // We assume there is 1 connection.
        if (connections.hasNext())
            return connections.next();

        return null;
    }
}
