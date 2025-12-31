import { useAzureMonitor, type AzureMonitorOpenTelemetryOptions } from "@azure/monitor-opentelemetry"
import { env } from "$env/dynamic/private"

/**
 * Initializes OpenTelemetry with Azure Monitor integration if enabled.
 * This function should be called once at application startup, before any other imports.
 * 
 * Configuration is controlled via environment variables:
 * - OTEL_EXPORTER_AZUREMONITOR_ENABLED: Set to "true" to enable (disabled by default)
 * - OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING: Azure Application Insights connection string
 * - OTEL_SERVICE_NAME: Service name for telemetry (defaults to "netman-web")
 */
export function initializeOpenTelemetry(): void {
  const enabled = env.OTEL_EXPORTER_AZUREMONITOR_ENABLED === "true"
  
  if (!enabled) {
    console.log("OpenTelemetry Azure Monitor exporter is disabled")
    return
  }

  const connectionString = env.OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING
  
  if (!connectionString || connectionString.trim() === "") {
    console.error(
      "OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING must be set when Azure Monitor exporter is enabled"
    )
    return
  }

  const serviceName = env.OTEL_SERVICE_NAME || "netman-web"

  console.log(`Initializing OpenTelemetry with Azure Monitor for service: ${serviceName}`)

  const options: AzureMonitorOpenTelemetryOptions = {
    azureMonitorExporterOptions: {
      connectionString
    },
    // Sample all requests in production - adjust as needed
    samplingRatio: 1,
    // Configure instrumentations
    instrumentationOptions: {
      http: { enabled: true },
      azureSdk: { enabled: false },
      mongoDb: { enabled: false },
      mySql: { enabled: false },
      postgreSql: { enabled: false },
      redis: { enabled: false },
      redis4: { enabled: false },
      bunyan: { enabled: false },
      winston: { enabled: false }
    },
    enableLiveMetrics: true,
    enableStandardMetrics: true,
    // Disable browser SDK loader (server-side only)
    browserSdkLoaderOptions: {
      enabled: false,
      connectionString: ""
    }
  }

  try {
    useAzureMonitor(options)
    console.log("OpenTelemetry initialized successfully with Azure Monitor")
  } catch (error) {
    console.error("Failed to initialize OpenTelemetry:", error)
  }
}
