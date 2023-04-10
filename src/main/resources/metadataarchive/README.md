# FybrikDataCatalog Metadata Archive

`FybrikDataCatalog.json` represents an Egeria Metadata Archive, which stores definitions of Egeria assets and their corresponding relationships, along with their various properties.

These archives can be easily loaded inside Egeria metadata servers, giving access to the data they contain.

`FybrikDataCatalog.json` contains:
- 4 assets representing CSV files with S3 connections, each of them having dummy properties corresponding with the definitions
found in `taxonomy.json` (said assets and properties are mapped to Egeria standards).
- 2 assets representing MySQL databases, with 1 relational table each.

This archive can be used to test the connector by mocking the retrieval and mapping of data
from an Egeria metadata server.

`FybrikGUIDMap.json` contains a mapping linking the unique name of an asset (ex. `File 1`) to
its identifier inside the metadata server (ex: `2605f367-5102-4fea-9f02-2a81a617ffa8`).

## Usage

Considering an Egeria metadata server is already configured and its corresponding application is running, the following steps
can be followed to load the metadata archive inside it:

#### 1. Move the archive file on the same machine that the server is running on.

#### 2. Shutdown the server instance.
- Metadata archives are only loaded upon server startup, so the server instance must be off.
- To turn the instance off, use the following HTTP request:
  ```
  curl --location --request DELETE '{{baseURL}}/open-metadata/admin-services/users/{{user}}/servers/{{server}}/instance'
  ```
  
#### 3. Add the metadata archive to server start.
- To add the metadata archive, use the following HTTP request:
    ```
    curl --location '{{baseURL}}/open-metadata/admin-services/users/{{user}}/servers/{{server}}/open-metadata-archives/file' \
    --header 'Content-Type: text/plain' \
    --data '{{absolute-path-to-archive}}'
    ```
  
#### 4. Start the server instance.
- To start the server instance using the stored configuration file, use the following HTTP request:
    ```
    curl --location --request POST '{{baseURL}}/open-metadata/admin-services/users/{{user}}/servers/{{server}}/instance'
    ```
  

The metadata server is now up and should contain all the data found inside the archive which can be retrieved and/or modified
using the identifiers found inside `FybrikGUIDMap.json`.