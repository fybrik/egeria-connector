package org.odpi.openmetadata.egeria.mapping.assets;

import org.fybrik.datacatalog.model.Connection;
import org.fybrik.datacatalog.model.ResourceColumn;
import org.fybrik.datacatalog.model.ResourceDetails;
import org.fybrik.datacatalog.model.ResourceMetadata;
import org.odpi.openmetadata.accessservices.assetcatalog.model.AssetCatalogBean;
import org.odpi.openmetadata.accessservices.assetcatalog.model.Classification;
import org.odpi.openmetadata.accessservices.assetmanager.client.ConnectedAssetClient;
import org.odpi.openmetadata.accessservices.assetmanager.client.exchange.DataAssetExchangeClient;
import org.odpi.openmetadata.egeria.mapping.connections.ConnectionTypeMapper;
import org.odpi.openmetadata.egeria.mapping.connections.ConnectionTypeMapperFactory;
import org.odpi.openmetadata.egeria.mapping.utils.MappingUtils;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.frameworks.connectors.properties.Locations;
import org.odpi.openmetadata.frameworks.connectors.properties.NestedSchemaType;
import org.odpi.openmetadata.frameworks.connectors.properties.SchemaAttributes;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.*;
import org.odpi.openmetadata.frameworkservices.ocf.metadatamanagement.client.ConnectedAssetUniverse;

import java.util.*;

/**
 * Base class for every asset mapper type which contains common fields and mapping methods.
 * As of now, the mapping logic is one-directional, from Egeria Entities to Fybrik Assets
 */
public abstract class AssetTypeMapper {

    protected ConnectedAssetClient connectedAssetClient;
    protected DataAssetExchangeClient dataAssetExchangeClient;
    protected AssetCatalogBean requestedEntity;
    protected String userId;

    protected Map<String, Object> additionalConnectionProperties = new HashMap<>();

    /**
     * Constructor that instantiates the Egeria clients used to communicate with the Metadata Store server.
     *
     * @param requestedEntity       Egeria AssetCatalogBean representation of the requested asset
     * @param serverName            name of the Egeria Metadata Store server
     * @param serverPlatformURLRoot URL Root of the Egeria Metadata Store server
     * @param userId                identifier of the user making the request
     * @throws InvalidParameterException if a parameter validation fails
     */
    public AssetTypeMapper(AssetCatalogBean requestedEntity, String serverName, String serverPlatformURLRoot, String userId) throws InvalidParameterException {
        this.connectedAssetClient = new ConnectedAssetClient(serverName, serverPlatformURLRoot);
        this.dataAssetExchangeClient = new DataAssetExchangeClient(serverName, serverPlatformURLRoot);
        this.requestedEntity = requestedEntity;
        this.userId = userId;
    }

    /**
     * Maps the relevant data from an Egeria Asset onto a Fybrik ResourceDetails object
     *
     * @return ResourceDetails object populated with the asset's data
     *
     * @throws InvalidParameterException if a parameter validation fails
     * @throws PropertyServerException if an error occurs while serving the request
     * @throws UserNotAuthorizedException if the user is not authorized to make the request
     */
    public abstract ResourceDetails getResourceDetails() throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException;

    /**
     * Maps the relevant data from an Egeria Asset onto a Fybrik ResourceMetadata object
     *
     * @return ResourceMetadata object populated with the asset's data
     *
     * @throws InvalidParameterException if a parameter validation fails
     * @throws PropertyServerException if an error occurs while serving the request
     * @throws UserNotAuthorizedException if the user is not authorized to make the request
     */
    public abstract ResourceMetadata getResourceMetadata() throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException;

    /**
     * Extracts the credentials from an Egeria metadata object.
     *
     * @return string containing the credentials
     *
     * @throws InvalidParameterException if a parameter validation fails
     * @throws PropertyServerException if an error occurs while serving the request
     * @throws UserNotAuthorizedException if the user is not authorized to make the request
     */
    public abstract String getCredentials() throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException;

    /**
     * Extracts the credentials from an Egeria ConnectedAssetUniverse object.
     *
     * @param  assetUniverse ConnectedAssetUniverse object representation of an Egeria Asset
     *
     * @return string containing the credentials
     */
    protected String getCredentials(ConnectedAssetUniverse assetUniverse) {
        org.odpi.openmetadata.frameworks.connectors.properties.beans.Connection egeriaConnection = MappingUtils.INSTANCE.getEgeriaConnectionFromAssetUniverse(assetUniverse);
        if (egeriaConnection == null)
            return null;

        Map<String, Object> configurationProperties = egeriaConnection.getConfigurationProperties();
        if (configurationProperties == null)
            return null;

        Object credentials = configurationProperties.get("credentials");
        if (!(credentials instanceof String))
            return null;

        return (String) credentials;
    }

    /**
     * Retrieves an Egeria ConnectedAssetUniverse asset representation for a provided asset GUID.
     *
     * @param assetGuid identifier of the requested asset
     *
     * @return ConnectedAssetUniverse object containing the asset's properties
     *
     * @throws InvalidParameterException if a parameter validation fails
     * @throws PropertyServerException if an error occurs while serving the request
     * @throws UserNotAuthorizedException if the user is not authorized to make the request
     */
    protected ConnectedAssetUniverse getAssetUniverse(String assetGuid) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        return (ConnectedAssetUniverse) connectedAssetClient.getAssetProperties("asset-manager", userId, assetGuid);
    }

    /**
     * Creates a Fybrik Connection object by mapping the relevant properties of an Egeria ConnectedAssetUniverse object.
     *
     * @param assetUniverse ConnectedAssetUniverse object representation of an Egeria Asset
     *
     * @return Fybrik Connection object
     */
    protected Connection getFybrikConnection(ConnectedAssetUniverse assetUniverse) {
        org.odpi.openmetadata.frameworks.connectors.properties.beans.Connection egeriaConnection = MappingUtils.INSTANCE.getEgeriaConnectionFromAssetUniverse(assetUniverse);
        if (egeriaConnection == null)
            return null;

        ConnectionTypeMapperFactory connectionTypeMapperFactory = new ConnectionTypeMapperFactory();
        String connectionType = egeriaConnection.getDisplayName();
        ConnectionTypeMapper connectionTypeMapper = connectionTypeMapperFactory.getMapper(connectionType, additionalConnectionProperties);

        return connectionTypeMapper.mapConnectionType(assetUniverse);
    }

    /**
     * Extracts the geography of an asset from its Egeria ConnectedAssetUniverse object representation.
     *
     * @param assetUniverse ConnectedAssetUniverse object representation of an Egeria Asset
     *
     * @return String representing the geography of the asset
     */
    protected String getGeography(ConnectedAssetUniverse assetUniverse) {
        Locations locations = assetUniverse.getKnownLocations();
        if (locations == null)
            return null;

        if (locations.hasNext()) {
            Location egeriaLocation = locations.next();
            return egeriaLocation.getQualifiedName();
        }

        return null;
    }

    /**
     * Extracts the tags from an Egeria entity.
     *
     * @param assetCatalogBean AssetCatalogBean object representing an Egeria entity
     * @return Map containing tag keys and values.
     */
    protected Map<String, Object> getAssetTags(AssetCatalogBean assetCatalogBean) {
        List<Classification> classifications = assetCatalogBean.getClassifications();
        for (Classification classification : classifications) {
            if (!"SecurityTags".equals(classification.getType().getName()))
                continue;

            String securityPropertiesString = classification.getProperties().get("securityProperties");
            return extractSecurityPropertiesFromString(securityPropertiesString);
        }

        return null;
    }

    private Map<String, Object> extractSecurityPropertiesFromString(String securityPropertiesString) {
        // Remove the '{' and '}' characters from the margins
        String trimmedString = securityPropertiesString.substring(1, securityPropertiesString.length() - 1);

        String[] keyValuePairs = trimmedString.split(", ");
        Map<String, Object> securityProperties = new LinkedHashMap<>();
        for (String keyValuePair : keyValuePairs) {
            String[] keyAndValue = keyValuePair.split("=");
            String key = keyAndValue[0];
            String value = keyAndValue[1];

            securityProperties.put(key, value);
        }

        return securityProperties;
    }

    @SuppressWarnings(value = "unchecked")
    protected Map<String, Object> getAssetTags(ElementHeader elementHeader) {
        List<ElementClassification> classifications = elementHeader.getClassifications();
        for (ElementClassification classification : classifications) {
            if (!"SecurityTags".equals(classification.getType().getTypeName()))
                continue;

            // Should the map be <String, String> ?
            Map<String, Object> classificationProperties = classification.getClassificationProperties();
            return (LinkedHashMap<String, Object>) classificationProperties.get("securityProperties");
        }

        return null;
    }

    /**
     * Creates a Fybrik ResourceColumn list by mapping the relevant data from an Egeria ConnectedAssetUniverse object.
     *
     * @param connectedAssetUniverse ConnectedAssetUniverse object representation of an Egeria Asset
     *
     * @return List of ResourceColumn objects
     */
    protected List<ResourceColumn> getColumns(ConnectedAssetUniverse connectedAssetUniverse) {
        SchemaType schemaType = connectedAssetUniverse.getSchema();
        if (!(schemaType instanceof NestedSchemaType))
            return null;

        List<ResourceColumn> columns = new ArrayList<>();
        SchemaAttributes schemaAttributes = ((NestedSchemaType) schemaType).getSchemaAttributes();

        while (schemaAttributes.hasNext()) {
            SchemaAttribute attribute = schemaAttributes.next();
            ResourceColumn column = new ResourceColumn();

            column.setName(attribute.getQualifiedName());
            column.setTags(getAssetTags(attribute));

            columns.add(column);
        }

        if (columns.size() != 0)
            return columns;

        return null;
    }

}
