import { describe, it, expect, vi, beforeEach, afterEach } from "vitest"

// Mock the Azure Monitor package
vi.mock("@azure/monitor-opentelemetry", () => ({
  useAzureMonitor: vi.fn()
}))

// Mock the env module
vi.mock("$env/dynamic/private", () => ({
  env: {}
}))

describe("OpenTelemetry Configuration", () => {
  let consoleSpy: { log: any; error: any }
  let mockEnv: Record<string, string>

  beforeEach(() => {
    // Reset mocks
    vi.resetModules()
    
    // Spy on console methods
    consoleSpy = {
      log: vi.spyOn(console, "log").mockImplementation(() => {}),
      error: vi.spyOn(console, "error").mockImplementation(() => {})
    }

    // Setup mock environment
    mockEnv = {}
    vi.doMock("$env/dynamic/private", () => ({
      env: mockEnv
    }))
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it("should not initialize when OTEL_EXPORTER_AZUREMONITOR_ENABLED is false", async () => {
    mockEnv.OTEL_EXPORTER_AZUREMONITOR_ENABLED = "false"
    
    const { initializeOpenTelemetry } = await import("$lib/server/otel")
    const { useAzureMonitor } = await import("@azure/monitor-opentelemetry")
    
    initializeOpenTelemetry()
    
    expect(consoleSpy.log).toHaveBeenCalledWith(
      "OpenTelemetry Azure Monitor exporter is disabled"
    )
    expect(useAzureMonitor).not.toHaveBeenCalled()
  })

  it("should not initialize when OTEL_EXPORTER_AZUREMONITOR_ENABLED is not set", async () => {
    // Don't set the enabled flag
    
    const { initializeOpenTelemetry } = await import("$lib/server/otel")
    const { useAzureMonitor } = await import("@azure/monitor-opentelemetry")
    
    initializeOpenTelemetry()
    
    expect(consoleSpy.log).toHaveBeenCalledWith(
      "OpenTelemetry Azure Monitor exporter is disabled"
    )
    expect(useAzureMonitor).not.toHaveBeenCalled()
  })

  it("should log error when enabled but connection string is missing", async () => {
    mockEnv.OTEL_EXPORTER_AZUREMONITOR_ENABLED = "true"
    mockEnv.OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING = ""
    
    const { initializeOpenTelemetry } = await import("$lib/server/otel")
    const { useAzureMonitor } = await import("@azure/monitor-opentelemetry")
    
    initializeOpenTelemetry()
    
    expect(consoleSpy.error).toHaveBeenCalledWith(
      "OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING must be set when Azure Monitor exporter is enabled"
    )
    expect(useAzureMonitor).not.toHaveBeenCalled()
  })

  it("should initialize when enabled with valid connection string", async () => {
    mockEnv.OTEL_EXPORTER_AZUREMONITOR_ENABLED = "true"
    mockEnv.OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING = "InstrumentationKey=test-key"
    mockEnv.OTEL_SERVICE_NAME = "test-service"
    
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
        samplingRatio: 1,
        enableLiveMetrics: true,
        enableStandardMetrics: true
      })
    )
    expect(consoleSpy.log).toHaveBeenCalledWith(
      "OpenTelemetry initialized successfully with Azure Monitor"
    )
  })

  it("should use default service name when OTEL_SERVICE_NAME is not set", async () => {
    mockEnv.OTEL_EXPORTER_AZUREMONITOR_ENABLED = "true"
    mockEnv.OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING = "InstrumentationKey=test-key"
    
    const { initializeOpenTelemetry } = await import("$lib/server/otel")
    
    initializeOpenTelemetry()
    
    expect(consoleSpy.log).toHaveBeenCalledWith(
      "Initializing OpenTelemetry with Azure Monitor for service: netman-web"
    )
  })
})
