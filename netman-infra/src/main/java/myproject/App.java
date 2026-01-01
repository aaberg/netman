package no.aaberg.netman.infra;

import com.pulumi.Pulumi;
import com.pulumi.azurenative.app.inputs.*;
import com.pulumi.azurenative.dbforpostgresql.Server;
import com.pulumi.azurenative.dbforpostgresql.ServerArgs;
import com.pulumi.azurenative.dbforpostgresql.inputs.AuthConfigArgs;
import com.pulumi.azurenative.dbforpostgresql.inputs.BackupArgs;
import com.pulumi.azurenative.dbforpostgresql.inputs.DataEncryptionArgs;
import com.pulumi.azurenative.dbforpostgresql.inputs.HighAvailabilityArgs;
import com.pulumi.azurenative.dbforpostgresql.inputs.MaintenanceWindowArgs;
import com.pulumi.azurenative.dbforpostgresql.inputs.NetworkArgs;
import com.pulumi.azurenative.dbforpostgresql.inputs.ReplicaArgs;
import com.pulumi.azurenative.dbforpostgresql.inputs.SkuArgs;
import com.pulumi.azurenative.dbforpostgresql.inputs.StorageArgs;
import com.pulumi.azurenative.resources.ResourceGroup;
import com.pulumi.azurenative.resources.ResourceGroupArgs;

import com.pulumi.azurenative.operationalinsights.Workspace;
import com.pulumi.azurenative.operationalinsights.WorkspaceArgs;
import com.pulumi.azurenative.operationalinsights.inputs.WorkspaceFeaturesArgs;
import com.pulumi.azurenative.operationalinsights.inputs.WorkspaceSkuArgs;
import com.pulumi.azurenative.operationalinsights.inputs.WorkspaceCappingArgs;
import com.pulumi.resources.CustomResourceOptions;

import com.pulumi.azurenative.app.ContainerApp;
import com.pulumi.azurenative.app.ContainerAppArgs;

import com.pulumi.azurenative.app.ManagedEnvironment;
import com.pulumi.azurenative.app.ManagedEnvironmentArgs;

import java.util.Map;

public class App {
    public static void main(String[] args) {
        Pulumi.run(ctx -> {
            final var environment = ctx.stackName();

            final var azureTenantId = ctx.config().requireSecret("azure.tenantId");

            // Resource group
            final var netmanRg = new ResourceGroup("netman-rg", ResourceGroupArgs.builder()
                    .resourceGroupName(String.format("netman-%s", environment))
                    .build(), CustomResourceOptions.builder()
                    .protect(true)
                    .build());

            // Postgres database
            final var netmanDb = new Server("netman-db", ServerArgs.builder()
                    .administratorLogin("psa")
                    .administratorLoginPassword(ctx.config().requireSecret("netman-db-admin-password"))
                    .authConfig(AuthConfigArgs.builder()
                            .activeDirectoryAuth("Enabled")
                            .passwordAuth("Enabled")
                            .tenantId(azureTenantId)
                            .build())
                    .availabilityZone("1")
                    .backup(BackupArgs.builder()
                            .backupRetentionDays(7)
                            .geoRedundantBackup("Disabled")
                            .build())
                    .dataEncryption(DataEncryptionArgs.builder()
                            .type("SystemManaged")
                            .build())
                    .highAvailability(HighAvailabilityArgs.builder()
                            .mode("Disabled")
                            .build())
                    .maintenanceWindow(MaintenanceWindowArgs.builder()
                            .customWindow("Disabled")
                            .dayOfWeek(0)
                            .startHour(0)
                            .startMinute(0)
                            .build())
                    .network(NetworkArgs.builder()
                            .publicNetworkAccess("Enabled")
                            .build())
                    .replica(ReplicaArgs.builder()
                            .role("Primary")
                            .build())
                    .replicationRole("Primary")
                    .resourceGroupName(netmanRg.name())
                    .serverName("netman-db")
                    .sku(SkuArgs.builder()
                            .name(ctx.config().require("netman-db-sku-name"))
                            .tier(ctx.config().require("netman-db-sku-tier"))
                            .build())
                    .storage(StorageArgs.builder()
                            .autoGrow("Disabled")
                            .iops(ctx.config().requireInteger("netman-db-storage-iops"))
                            .storageSizeGB(ctx.config().requireInteger("netman-db-storage-size"))
                            .tier(ctx.config().require("netman-db-storage-tier"))
                            .type("")
                            .build())
                    .version("17")
                    .build(), CustomResourceOptions.builder()
                    .protect(true)
                    .build());

            // Log Analytics Workspace
            final var logAnalyticsWorkspace = new Workspace("AzTest2170", WorkspaceArgs.builder()
                    .features(WorkspaceFeaturesArgs.builder()
                            .enableLogAccessUsingOnlyResourcePermissions(true)
                            .build())
                    .publicNetworkAccessForIngestion("Enabled")
                    .publicNetworkAccessForQuery("Enabled")
                    .resourceGroupName(netmanRg.name())
                    .retentionInDays(30)
                    .sku(WorkspaceSkuArgs.builder()
                            .name("PerGB2018")
                            .build())
                    .workspaceCapping(WorkspaceCappingArgs.builder()
                            .dailyQuotaGb(-1.0)
                            .build())
                    .workspaceName("workspacenetmandeva773")
                    .build(), CustomResourceOptions.builder()
                    .protect(true)
                    .build());


            // Container Apps Environment
            var containerEnv = new ManagedEnvironment("testcontainerenv", ManagedEnvironmentArgs.builder()
                    .appLogsConfiguration(AppLogsConfigurationArgs.builder()
                            .destination("log-analytics")
                            .logAnalyticsConfiguration(LogAnalyticsConfigurationArgs.builder()
                                    .customerId(logAnalyticsWorkspace.customerId())
                                    .dynamicJsonColumns(false)
                                    .build())
                            .build())
                    .environmentName(String.format("managed-environment-netman%s", environment))
                    .openTelemetryConfiguration(OpenTelemetryConfigurationArgs.builder()
                            .logsConfiguration(LogsConfigurationArgs.builder()
                                    .destinations("appInsights")
                                    .build())
                            .tracesConfiguration(TracesConfigurationArgs.builder()
                                    .destinations("appInsights")
                                    .includeDapr(false)
                                    .build())
                            .build())
                    .peerAuthentication(ManagedEnvironmentPeerAuthenticationArgs.builder()
                            .mtls(MtlsArgs.builder()
                                    .enabled(false)
                                    .build())
                            .build())
                    .peerTrafficConfiguration(ManagedEnvironmentPeerTrafficConfigurationArgs.builder()
                            .encryption(ManagedEnvironmentEncryptionArgs.builder()
                                    .enabled(false)
                                    .build())
                            .build())
                    .publicNetworkAccess("Enabled")
                    .resourceGroupName(netmanRg.name())
                    .workloadProfiles(WorkloadProfileArgs.builder()
                            .enableFips(false)
                            .name("Consumption")
                            .workloadProfileType("Consumption")
                            .build())
                    .zoneRedundant(false)
                    .build(), CustomResourceOptions.builder()
                    .protect(true)
                    .build());

            // NATS
            var natsContainerApp = new ContainerApp("nats-containerapp", ContainerAppArgs.builder()
                    .configuration(ConfigurationArgs.builder()
                            .activeRevisionsMode("Single")
                            .ingress(IngressArgs.builder()
                                    .additionalPortMappings(
                                            IngressPortMappingArgs.builder()
                                                    .exposedPort(4222)
                                                    .external(false)
                                                    .targetPort(4222)
                                                    .build(),
                                            IngressPortMappingArgs.builder()
                                                    .exposedPort(6222)
                                                    .external(false)
                                                    .targetPort(6222)
                                                    .build())
                                    .allowInsecure(false)
                                    .clientCertificateMode("Ignore")
                                    .exposedPort(0)
                                    .external(true)
                                    .stickySessions(IngressStickySessionsArgs.builder()
                                            .affinity("none")
                                            .build())
                                    .targetPort(8222)
                                    .traffic(TrafficWeightArgs.builder()
                                            .latestRevision(true)
                                            .weight(100)
                                            .build())
                                    .transport("Auto")
                                    .build())
                            .maxInactiveRevisions(100)
                            .targetLabel("")
                            .build())
                    .containerAppName("nats")
                    .environmentId(containerEnv.id())
                    .identity(ManagedServiceIdentityArgs.builder()
                            .type("None")
                            .build())
                    .kind("containerapps")
                    .managedEnvironmentId(containerEnv.id())
                    .resourceGroupName(netmanRg.name())
                    .template(TemplateArgs.builder()
                            .containers(ContainerArgs.builder()
                                    .image("docker.io/nats:2.12")
                                    .imageType("ContainerImage")
                                    .name("nats")
                                    .resources(ContainerResourcesArgs.builder()
                                            .cpu(0.25)
                                            .memory("0.5Gi")
                                            .build())
                                    .build())
                            .revisionSuffix("")
                            .scale(ScaleArgs.builder()
                                    .cooldownPeriod(300)
                                    .maxReplicas(1)
                                    .minReplicas(1)
                                    .pollingInterval(30)
                                    .build())
                            .build())
                    .workloadProfileName("Consumption")
                    .build(), CustomResourceOptions.builder()
                    .protect(false)
                    .build());

            // Netman API

            final var jdbcUrl = netmanDb.fullyQualifiedDomainName().applyValue(fqdn -> String.format("jdbc:postgresql://%s:5432/postgres?sslmode=require", fqdn));

            final var apiContainerapp = new ContainerApp("api-containerapp", ContainerAppArgs.builder()
                    .configuration(ConfigurationArgs.builder()
                            .activeRevisionsMode("Single")
                            .ingress(IngressArgs.builder()
                                    .allowInsecure(false)
                                    .clientCertificateMode("Ignore")
                                    .exposedPort(0)
                                    .external(true)
                                    .stickySessions(IngressStickySessionsArgs.builder()
                                            .affinity("none")
                                            .build())
                                    .targetPort(8080)
                                    .traffic(TrafficWeightArgs.builder()
                                            .latestRevision(true)
                                            .weight(100)
                                            .build())
                                    .transport("Auto")
                                    .build())
                            .maxInactiveRevisions(100)
                            .targetLabel("")
                            .secrets(
                                    SecretArgs.builder()
                                            .name("netman-jdbc-password")
                                            .value(ctx.config().requireSecret("netman-db-admin-password"))
                                            .build()
                            )
                            .build())
                    .containerAppName("netman-api")
                    .environmentId(containerEnv.id())
                    .identity(ManagedServiceIdentityArgs.builder()
                            .type("None")
                            .build())
                    .kind("containerapps")
                    .managedEnvironmentId(containerEnv.id())
                    .resourceGroupName(netmanRg.name())
                    .template(TemplateArgs.builder()
                            .containers(ContainerArgs.builder()
                                    .env(
                                            EnvironmentVarArgs.builder()
                                                    .name("NETMAN_PORT")
                                                    .value("8080")
                                                    .build(),
                                            EnvironmentVarArgs.builder()
                                                    .name("NETMAN_JDBC_URL")
                                                    .value(jdbcUrl)
                                                    .build(),
                                            EnvironmentVarArgs.builder()
                                                    .name("NETMAN_JDBC_USER")
                                                    .value("psa")
                                                    .build(),
                                            EnvironmentVarArgs.builder()
                                                    .name("NETMAN_JDBC_PASSWORD")
                                                    .secretRef("netman-jdbc-password")
                                                    .build(),
                                            EnvironmentVarArgs.builder()
                                                    .name("NETMAN_NATS_URL")
                                                    .value("nats:4222")
                                                    .build(),
                                            EnvironmentVarArgs.builder()
                                                    .name("NETMAN_HANKO_BASE_URL")
                                                    .value(ctx.config().require("netman-hanko-base-url"))
                                                    .build())
                                    .image("ghcr.io/aaberg/netman/api:0.0.4")
                                    .imageType("ContainerImage")
                                    .name("netman-api")
                                    .resources(ContainerResourcesArgs.builder()
                                            .cpu(0.25)
                                            .memory("0.5Gi")
                                            .build())
                                    .build())
                            .revisionSuffix("")
                            .scale(ScaleArgs.builder()
                                    .cooldownPeriod(300)
                                    .maxReplicas(1)
                                    .minReplicas(1)
                                    .pollingInterval(30)
                                    .build())
                            .build())
                    .workloadProfileName("Consumption")
                    .build(), CustomResourceOptions.builder()
                    .protect(true)
                    .build());


            // Netman Web
            var webContainerApp = new ContainerApp("web-containerapp", ContainerAppArgs.builder()
                    .configuration(ConfigurationArgs.builder()
                            .activeRevisionsMode("Single")
                            .ingress(IngressArgs.builder()
                                    .allowInsecure(false)
                                    .clientCertificateMode("Ignore")
                                    .exposedPort(0)
                                    .external(true)
                                    .stickySessions(IngressStickySessionsArgs.builder()
                                            .affinity("none")
                                            .build())
                                    .targetPort(3000)
                                    .traffic(TrafficWeightArgs.builder()
                                            .latestRevision(true)
                                            .weight(100)
                                            .build())
                                    .transport("Auto")
                                    .build())
                            .maxInactiveRevisions(100)
                            .targetLabel("")
                            .build())
                    .containerAppName("netman-web")
                    .environmentId(containerEnv.id())
                    .identity(ManagedServiceIdentityArgs.builder()
                            .type("None")
                            .build())
                    .kind("containerapps")
                    .managedEnvironmentId(containerEnv.id())
                    .resourceGroupName(netmanRg.name())
                    .template(TemplateArgs.builder()
                            .containers(ContainerArgs.builder()
                                    .env(
                                            EnvironmentVarArgs.builder()
                                                    .name("NETMAN_API_URL")
                                                    .value(containerEnv.defaultDomain().applyValue(dd ->
                                                            String.format("https://netman-api.%s", dd)))
                                                    .build(),
                                            EnvironmentVarArgs.builder()
                                                    .name("PUBLIC_HANKO_API_URL")
                                                    .value(ctx.config().require("netman-hanko-base-url"))
                                                    .build(),
                                            EnvironmentVarArgs.builder()
                                                    .name("SERVER_HANKO_API_URL")
                                                    .value(ctx.config().require("netman-hanko-base-url"))
                                                    .build(),
                                            EnvironmentVarArgs.builder()
                                                    .name("NODE_ENV")
                                                    .value("production")
                                                    .build(),
                                            EnvironmentVarArgs.builder()
                                                    .name("PORT")
                                                    .value("3000")
                                                    .build(),
                                            EnvironmentVarArgs.builder()
                                                    .name("HOST")
                                                    .value("0.0.0.0")
                                                    .build(),
                                            EnvironmentVarArgs.builder()
                                                    .name("ORIGIN")
                                                    .value(containerEnv.defaultDomain().applyValue(dd ->
                                                            String.format("https://netman-web.%s", dd)))
                                                    .build())
                                    .image("ghcr.io/aaberg/netman/web:0.0.4")
                                    .imageType("ContainerImage")
                                    .name("netman-web")
                                    .resources(ContainerResourcesArgs.builder()
                                            .cpu(0.25)
                                            .memory("0.5Gi")
                                            .build())
                                    .build())
                            .revisionSuffix("")
                            .scale(ScaleArgs.builder()
                                    .cooldownPeriod(300)
                                    .maxReplicas(1)
                                    .minReplicas(1)
                                    .pollingInterval(30)
                                    .rules(ScaleRuleArgs.builder()
                                            .http(
                                                    HttpScaleRuleArgs.builder()
                                                            .metadata(Map.of("concurrentRequests", "10"))
                                                            .build())
                                            .name("http-scaler")
                                            .build())
                                    .build())
                            .build())
                    .workloadProfileName("Consumption")
                    .build(), CustomResourceOptions.builder()
                    .protect(true)
                    .build());

//            ctx.export("storageAccountName", storageAccount.name());
        });
    }
}
