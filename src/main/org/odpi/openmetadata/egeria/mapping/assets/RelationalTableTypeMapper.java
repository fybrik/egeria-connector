package org.odpi.openmetadata.egeria.mapping.assets;

import org.fybrik.datacatalog.model.Connection;
import org.fybrik.datacatalog.model.ResourceColumn;
import org.fybrik.datacatalog.model.ResourceDetails;
import org.fybrik.datacatalog.model.ResourceMetadata;
import org.odpi.openmetadata.accessservices.assetcatalog.model.AssetCatalogBean;
import org.odpi.openmetadata.accessservices.assetmanager.metadataelements.SchemaAttributeElement;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.frameworks.connectors.properties.RelatedAssets;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.Asset;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.RelatedAsset;
import org.odpi.openmetadata.frameworkservices.ocf.metadatamanagement.client.ConnectedAssetUniverse;

import java.util.ArrayList;
import java.util.List;

/**
 * AssetTypeMapper for mapping Egeria RelationTable assets
 */
public class RelationalTableTypeMapper extends AssetTypeMapper {

    private ConnectedAssetUniverse database;
    private ConnectedAssetUniverse deployedDBSchema;

    public RelationalTableTypeMapper(AssetCatalogBean requestedEntity, String serverName, String serverPlatformURLRoot, String userId) throws InvalidParameterException {
        super(requestedEntity, serverName, serverPlatformURLRoot, userId);
    }

    @Override
    public ResourceDetails getResourceDetails() throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        ResourceDetails details = new ResourceDetails();

        deployedDBSchema = getDeployedDBSchemaFromTable(requestedEntity);
        if (deployedDBSchema == null)
            return null;

        database = getDatabaseFromDeployedDBSchema(deployedDBSchema);
        if (database == null)
            return null;

        // Still not sure about this
        details.setDataFormat("csv");

        String tableName = requestedEntity.getProperties().get("qualifiedName");
        additionalConnectionProperties.put("table", tableName);
        Connection connection = getFybrikConnection(database);
        details.setConnection(connection);

        return details;
    }

    @Override
    public ResourceMetadata getResourceMetadata() throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        ResourceMetadata resourceMetadata = new ResourceMetadata();

        String tableName = requestedEntity.getProperties().get("qualifiedName");
        resourceMetadata.setName(tableName);
        resourceMetadata.setColumns(getColumns(requestedEntity));

        deployedDBSchema = getDeployedDBSchemaFromTable(requestedEntity);
        if (deployedDBSchema == null)
            return null;

        database = getDatabaseFromDeployedDBSchema(deployedDBSchema);
        if (database == null)
            return null;

        resourceMetadata.setGeography(getGeography(database));
        resourceMetadata.setTags(getAssetTags(requestedEntity));

        return resourceMetadata;
    }

    @Override
    public String getCredentials() throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        deployedDBSchema = getDeployedDBSchemaFromTable(requestedEntity);
        if (deployedDBSchema == null)
            return null;

        database = getDatabaseFromDeployedDBSchema(deployedDBSchema);
        if (database == null)
            return null;

        return getCredentials(database);
    }

    /**
     * Retrieves an Egeria Database asset represented as a ConnectedAssetUniverse object from a DeployedDBSchema
     *
     * @param deployedDBSchema DeployedDBSchema schema entity represented as a ConnectedAssetUniverse object
     *
     * @return Database represented as a ConnectedAssetUniverse object
     *
     * @throws InvalidParameterException if a parameter validation fails
     * @throws PropertyServerException if an error occurs while serving the request
     * @throws UserNotAuthorizedException if the user is not authorized to make the request
     */
    private ConnectedAssetUniverse getDatabaseFromDeployedDBSchema(ConnectedAssetUniverse deployedDBSchema) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        if (database != null)
            return database;

        RelatedAssets relatedAssets = deployedDBSchema.getRelatedAssets();
        RelatedAsset databaseRelatedAsset = null;
        while (relatedAssets.hasNext()) {
            RelatedAsset relatedAsset = relatedAssets.next();
            if ("DataContentForDataSet".equals(relatedAsset.getRelationshipName()))
                databaseRelatedAsset = relatedAsset;
        }

        if (databaseRelatedAsset == null)
            return null;

        return getAssetUniverse(databaseRelatedAsset.getGUID());
    }

    /**
     * Retreives an Egeria DeployedDBSchema asset represented as a ConnectedAssetUniverse object from a RelationalTable.
     *
     * @param relationalTable Egeria RelationalTable entity represented as a DataAssetElement
     *
     * @return DeployedDBSchema represented as a ConnectedAssetUniverse object
     *
     * @throws InvalidParameterException if a parameter validation fails
     * @throws PropertyServerException if an error occurs while serving the request
     * @throws UserNotAuthorizedException if the user is not authorized to make the request
     */
    private ConnectedAssetUniverse getDeployedDBSchemaFromTable(AssetCatalogBean relationalTable) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        if (deployedDBSchema != null)
            return deployedDBSchema;

        Asset deployedDBSchemaSummary = connectedAssetClient.getAnchorAssetFromGUID(userId, relationalTable.getGuid());
        return getAssetUniverse(deployedDBSchemaSummary.getGUID());
    }

    /**
     * Creates a Fybrik ResourceColumn list by mapping the relevant data from an Egeria DataAssetElement object.
     *
     * @param requestedEntity Egeria RelationalTable entity represented as a DataAssetElement
     *
     * @return List of ResourceColumn objects
     *
     * @throws InvalidParameterException if a parameter validation fails
     * @throws PropertyServerException if an error occurs while serving the request
     * @throws UserNotAuthorizedException if the user is not authorized to make the request
     */
    private List<ResourceColumn> getColumns(AssetCatalogBean requestedEntity) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        List<SchemaAttributeElement> nestedSchemaAttributes = dataAssetExchangeClient.getNestedSchemaAttributes(userId,
                null,
                null,
                requestedEntity.getGuid(),
                0,
                100,
                null,
                false,
                false);

        List<ResourceColumn> columns = new ArrayList<>();
        for (SchemaAttributeElement schemaAttributeElement : nestedSchemaAttributes) {
            ResourceColumn column = new ResourceColumn();
            column.setName(schemaAttributeElement.getSchemaAttributeProperties().getQualifiedName());
            column.setTags(getAssetTags(schemaAttributeElement.getElementHeader()));

            columns.add(column);
        }

        return columns;
    }

}
