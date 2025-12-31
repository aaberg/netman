package netman.configuration

import com.azure.monitor.opentelemetry.autoconfigure.AzureMonitorAutoConfigure
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Primary
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk
import jakarta.inject.Singleton

@Factory
class OpenTelemetryConfig {

    @Singleton
    @Primary
    @Requires(property = "otel.exporter.azuremonitor.enabled", value = "true")
    fun azureMonitorExporter(@Value("\${otel.exporter.azuremonitor.connection-string}") connectionString: String)
    : OpenTelemetry {
        require(connectionString.isNotBlank()) {
            "Configuration property 'otel.exporter.azuremonitor.connection-string' must not be blank when Azure Monitor exporter is enabled."
        }
        val otelBuilder = AutoConfiguredOpenTelemetrySdk.builder()

        AzureMonitorAutoConfigure.customize(otelBuilder, connectionString)

        return otelBuilder.build().openTelemetrySdk
    }
}