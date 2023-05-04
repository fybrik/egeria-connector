package org.odpi.openmetadata.egeria.mapping.connections;

import java.security.InvalidParameterException;
import java.util.Map;

/**
 * Factory pattern for creating ConnectionTypeMapper objects based on the connection type
 */
public class ConnectionTypeMapperFactory {

    /**
     * Creates a ConnectionTypeMapper subclass based on the provided connection type.
     *
     * @param connectionType String representing the connection type
     * @param additionalConnectionProperties additional external properties needed to populate the mapped connection
     *
     * @return Implementation of ConnectionTypeMapper based on the provided connectionType parameter
     */
    public ConnectionTypeMapper getMapper(String connectionType,
                                          Map<String, Object> additionalConnectionProperties) {
        switch (connectionType) {
            case ConnectionTypeMapper.S3:
                return new S3ConnectionTypeMapper(additionalConnectionProperties);
            case ConnectionTypeMapper.MY_SQL:
                return new MySQLConnectionTypeMapper(additionalConnectionProperties);
            default:
                throw new InvalidParameterException("Unsupported connection type: " + connectionType);
        }
    }

}
