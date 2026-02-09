 # Netman Infrastructure

 This project contains the Pulumi infrastructure code for the Netman application, provisioning resources on Azure using Java and the Azure Native provider.

 The infrastructure includes:
 - Azure Resource Group
 - Azure Database for PostgreSQL flexible server
 - Log Analytics Workspace
 - Managed Environment (Container Apps Environment)
 - Container Apps for the Netman API and Web frontend

 ## Prerequisites

 - [Pulumi CLI](https://www.pulumi.com/docs/get-started/install/)
 - [Java 25+](https://adoptium.net/)
 - [Maven 3.6+](https://maven.apache.org/install.html)
 - [Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli) (and be logged in via `az login`)

 ## Stack Management

 To list available stacks:
 ```bash
 pulumi stack ls
 ```

 To select or change the active stack (e.g., `dev` or `test`):
 ```bash
 pulumi stack select <stack-name>
 ```

 To create a new stack:
 ```bash
 pulumi stack init <stack-name>
 ```

 ## Infrastructure Setup

 1. **Configure Secrets**: Ensure all required configuration values and secrets are set for your stack (refer to `Pulumi.<stack>.yaml`).
    Example:
    ```bash
    pulumi config set --secret azure.tenantId <your-tenant-id>
    pulumi config set --secret netman-db-admin-password <your-password>
    ```

 2. **Deploy**: Run the following command to preview and deploy the infrastructure:
    ```bash
    pulumi up
    ```

 ## Infrastructure Destruction

 To tear down the provisioned resources and delete the infrastructure:
 ```bash
 pulumi destroy
 ```

 Note: Some resources might have protection enabled in `App.java`. You may need to disable protection or delete the resource group manually if `pulumi destroy` is restricted by resource locks or Pulumi protection flags.