# NetMan Web (Svelte/SvelteKit)

This folder contains the Svelte/SvelteKit frontend for NetMan.

Looking for setup and project-wide instructions? See the repository root README:

- ../README.md
- Or view it on your hosting platform (GitHub/GitLab/etc.) at the repo root.

## Quick commands

```powershell
npm install
npm run dev        # start dev server (usually http://localhost:5173)
npm run build      # production build
npm run preview    # preview prod build locally
npm run test       # unit/component tests (Vitest)
npm run test:e2e   # end-to-end tests (Playwright)
```

## OpenTelemetry Integration

The web application includes OpenTelemetry support for Azure Application Insights, which is **disabled by default**.

### Configuration

OpenTelemetry can be configured using the following environment variables:

- `OTEL_EXPORTER_AZUREMONITOR_ENABLED`: Set to `"true"` to enable Azure Monitor exporter (default: `false`)
- `OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING`: Azure Application Insights connection string (required when enabled)
- `OTEL_SERVICE_NAME`: Service name for telemetry (default: `"netman-web"`)

### Example

To enable OpenTelemetry with Azure Monitor:

1. Set the environment variables:
   ```bash
   export OTEL_EXPORTER_AZUREMONITOR_ENABLED=true
   export OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING="InstrumentationKey=your-key;..."
   export OTEL_SERVICE_NAME=netman-web
   ```

2. Or add them to your `.env` file (copy from `.env.example`):
   ```
   OTEL_EXPORTER_AZUREMONITOR_ENABLED=true
   OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING=InstrumentationKey=your-key;...
   OTEL_SERVICE_NAME=netman-web
   ```

3. When using Docker Compose, set the environment variables before running:
   ```bash
   export OTEL_EXPORTER_AZUREMONITOR_ENABLED=true
   export OTEL_EXPORTER_AZUREMONITOR_CONNECTION_STRING="InstrumentationKey=your-key;..."
   docker compose up -d
   ```

Note: When running the web app against a locally running API, ensure the API base URL is configured (e.g., PUBLIC_API_BASE_URL). See the root README for details on configuration, Docker Compose, and environment variables.
