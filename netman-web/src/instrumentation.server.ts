/**
 * SvelteKit instrumentation hook
 * This file runs before any application code (when adapter supports it).
 * Used to initialize OpenTelemetry for server-side observability.
 *
 * See: https://svelte.dev/docs/kit/observability
 */

import { initializeOpenTelemetry } from "$lib/server/otel"

// Initialize OpenTelemetry with Azure Monitor if enabled
initializeOpenTelemetry()
