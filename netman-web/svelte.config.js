import adapter from "@sveltejs/adapter-node"
import { vitePreprocess } from "@sveltejs/vite-plugin-svelte"

const config = {
  preprocess: vitePreprocess(),
  kit: {
    adapter: adapter(),
    // Enable experimental instrumentation for OpenTelemetry support
    // See: https://svelte.dev/docs/kit/observability
    experimental: {
      instrumentation: {
        server: true
      }
    }
  }
}

export default config
