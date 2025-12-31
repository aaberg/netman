import {
  useAzureMonitor,
  type AzureMonitorOpenTelemetryOptions
} from "@azure/monitor-opentelemetry"

/**
 * Initializes OpenTelemetry with Azure Monitor integration if enabled.
 * This function should be called from src/instrumentation.server.ts before any other application code.
 *
 * Configuration is controlled via environment variables:
 * - OTEL_EXPORTER_AZUREMONITOR_ENABLED: Set to "true" to enable (disabled by default)
 * - OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING: Azure Application Insights connection string
 * - OTEL_SERVICE_NAME: Service name for telemetry (defaults to "netman-web")
 * - OTEL_SAMPLING_RATIO: Sampling ratio 0-1 (defaults to 0.1 for 10% sampling)
 */
export function initializeOpenTelemetry(): void {
  const enabled = process.env.OTEL_EXPORTER_AZUREMONITOR_ENABLED === "true"

  if (!enabled) {
    console.log("OpenTelemetry Azure Monitor exporter is disabled")
    return
  }

  const connectionString = process.env.OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING

  if (!connectionString || connectionString.trim() === "") {
    console.error(
      "OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING must be set when Azure Monitor exporter is enabled"
    )
    return
  }

  const serviceName = process.env.OTEL_SERVICE_NAME || "netman-web"

  console.log(`Initializing OpenTelemetry with Azure Monitor for service: ${serviceName}`)

  // Sampling ratio - defaults to 0.1 (10%) to reduce telemetry volume
  // Set OTEL_SAMPLING_RATIO to override (value between 0 and 1)
  let samplingRatio = parseFloat(process.env.OTEL_SAMPLING_RATIO || "0.1")

  // Validate sampling ratio
  if (isNaN(samplingRatio) || samplingRatio < 0 || samplingRatio > 1) {
    console.warn(
      `Invalid OTEL_SAMPLING_RATIO value: ${process.env.OTEL_SAMPLING_RATIO}. Using default: 0.1`
    )
    samplingRatio = 0.1
  }

  const options: AzureMonitorOpenTelemetryOptions = {
    azureMonitorExporterOptions: {
      connectionString
    },
    // Sample a percentage of requests - configurable via OTEL_SAMPLING_RATIO
    samplingRatio,
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
      enabled: false
    }
  }

  try {
    useAzureMonitor(options)
    console.log("OpenTelemetry initialized successfully with Azure Monitor")
  } catch (error) {
    console.error("Failed to initialize OpenTelemetry:", error)
  }
}
