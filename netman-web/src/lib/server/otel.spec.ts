import { describe, it, expect, vi, beforeEach, afterEach } from "vitest"

// Mock the Azure Monitor package
vi.mock("@azure/monitor-opentelemetry", () => ({
  useAzureMonitor: vi.fn()
}))

describe("OpenTelemetry Configuration", () => {
  let consoleSpy: {
    log: ReturnType<typeof vi.spyOn>
    error: ReturnType<typeof vi.spyOn>
    warn: ReturnType<typeof vi.spyOn>
  }
  let originalEnv: NodeJS.ProcessEnv

  beforeEach(() => {
    // Reset mocks
    vi.resetModules()

    // Spy on console methods
    consoleSpy = {
      log: vi.spyOn(console, "log").mockImplementation(() => {}),
      error: vi.spyOn(console, "error").mockImplementation(() => {}),
      warn: vi.spyOn(console, "warn").mockImplementation(() => {})
    }

    // Save original environment and clear relevant env vars
    originalEnv = { ...process.env }
    delete process.env.OTEL_EXPORTER_AZUREMONITOR_ENABLED
    delete process.env.OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING
    delete process.env.OTEL_SERVICE_NAME
    delete process.env.OTEL_SAMPLING_RATIO
  })

  afterEach(() => {
    vi.restoreAllMocks()
    // Restore original environment
    process.env = originalEnv
  })

  it("should not initialize when OTEL_EXPORTER_AZUREMONITOR_ENABLED is false", async () => {
    process.env.OTEL_EXPORTER_AZUREMONITOR_ENABLED = "false"

    const { initializeOpenTelemetry } = await import("$lib/server/otel")
    const { useAzureMonitor } = await import("@azure/monitor-opentelemetry")

    initializeOpenTelemetry()

    expect(consoleSpy.log).toHaveBeenCalledWith("OpenTelemetry Azure Monitor exporter is disabled")
    expect(useAzureMonitor).not.toHaveBeenCalled()
  })

  it("should not initialize when OTEL_EXPORTER_AZUREMONITOR_ENABLED is not set", async () => {
    // Don't set the enabled flag

    const { initializeOpenTelemetry } = await import("$lib/server/otel")
    const { useAzureMonitor } = await import("@azure/monitor-opentelemetry")

    initializeOpenTelemetry()

    expect(consoleSpy.log).toHaveBeenCalledWith("OpenTelemetry Azure Monitor exporter is disabled")
    expect(useAzureMonitor).not.toHaveBeenCalled()
  })

  it("should log error when enabled but connection string is missing", async () => {
    process.env.OTEL_EXPORTER_AZUREMONITOR_ENABLED = "true"
    process.env.OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING = ""

    const { initializeOpenTelemetry } = await import("$lib/server/otel")
    const { useAzureMonitor } = await import("@azure/monitor-opentelemetry")

    initializeOpenTelemetry()

    expect(consoleSpy.error).toHaveBeenCalledWith(
      "OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING must be set when Azure Monitor exporter is enabled"
    )
    expect(useAzureMonitor).not.toHaveBeenCalled()
  })

  it("should initialize when enabled with valid connection string", async () => {
    process.env.OTEL_EXPORTER_AZUREMONITOR_ENABLED = "true"
    process.env.OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING = "InstrumentationKey=test-key"
    process.env.OTEL_SERVICE_NAME = "test-service"

    const { initializeOpenTelemetry } = await import("$lib/server/otel")
    const { useAzureMonitor } = await import("@azure/monitor-opentelemetry")

    initializeOpenTelemetry()

    expect(consoleSpy.log).toHaveBeenCalledWith(
      "Initializing OpenTelemetry with Azure Monitor for service: test-service"
    )
    expect(useAzureMonitor).toHaveBeenCalledWith(
      expect.objectContaining({
        azureMonitorExporterOptions: {
          connectionString: "InstrumentationKey=test-key"
        },
        samplingRatio: 0.1,
        enableLiveMetrics: true,
        enableStandardMetrics: true
      })
    )
    expect(consoleSpy.log).toHaveBeenCalledWith(
      "OpenTelemetry initialized successfully with Azure Monitor"
    )
  })

  it("should use default service name when OTEL_SERVICE_NAME is not set", async () => {
    process.env.OTEL_EXPORTER_AZUREMONITOR_ENABLED = "true"
    process.env.OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING = "InstrumentationKey=test-key"

    const { initializeOpenTelemetry } = await import("$lib/server/otel")

    initializeOpenTelemetry()

    expect(consoleSpy.log).toHaveBeenCalledWith(
      "Initializing OpenTelemetry with Azure Monitor for service: netvaerke-web"
    )
  })

  it("should use custom sampling ratio when OTEL_SAMPLING_RATIO is set", async () => {
    process.env.OTEL_EXPORTER_AZUREMONITOR_ENABLED = "true"
    process.env.OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING = "InstrumentationKey=test-key"
    process.env.OTEL_SAMPLING_RATIO = "0.5"

    const { initializeOpenTelemetry } = await import("$lib/server/otel")
    const { useAzureMonitor } = await import("@azure/monitor-opentelemetry")

    initializeOpenTelemetry()

    expect(useAzureMonitor).toHaveBeenCalledWith(
      expect.objectContaining({
        samplingRatio: 0.5
      })
    )
  })

  it("should use default sampling ratio when OTEL_SAMPLING_RATIO is invalid", async () => {
    process.env.OTEL_EXPORTER_AZUREMONITOR_ENABLED = "true"
    process.env.OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING = "InstrumentationKey=test-key"
    process.env.OTEL_SAMPLING_RATIO = "invalid"

    const { initializeOpenTelemetry } = await import("$lib/server/otel")
    const { useAzureMonitor } = await import("@azure/monitor-opentelemetry")

    initializeOpenTelemetry()

    expect(consoleSpy.warn).toHaveBeenCalledWith(
      "Invalid OTEL_SAMPLING_RATIO value: invalid. Using default: 0.1"
    )
    expect(useAzureMonitor).toHaveBeenCalledWith(
      expect.objectContaining({
        samplingRatio: 0.1
      })
    )
  })

  it("should use default sampling ratio when OTEL_SAMPLING_RATIO is out of range", async () => {
    process.env.OTEL_EXPORTER_AZUREMONITOR_ENABLED = "true"
    process.env.OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING = "InstrumentationKey=test-key"
    process.env.OTEL_SAMPLING_RATIO = "1.5"

    const { initializeOpenTelemetry } = await import("$lib/server/otel")
    const { useAzureMonitor } = await import("@azure/monitor-opentelemetry")

    initializeOpenTelemetry()

    expect(consoleSpy.warn).toHaveBeenCalledWith(
      "Invalid OTEL_SAMPLING_RATIO value: 1.5. Using default: 0.1"
    )
    expect(useAzureMonitor).toHaveBeenCalledWith(
      expect.objectContaining({
        samplingRatio: 0.1
      })
    )
  })
})
