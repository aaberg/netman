import { chromium, FullConfig } from "@playwright/test"

/**
 * Global setup for Playwright tests
 * Checks if required services are running before tests start
 */
async function globalSetup(config: FullConfig) {
  console.log("🔍 Checking required services...")

  const baseURL = config.use?.baseURL || "http://localhost:4173"
  const hankoURL = process.env.PUBLIC_HANKO_API_URL || "http://localhost:8000"
  const apiURL = process.env.NETMAN_API_URL || "http://localhost:8081"

  const errors: string[] = []

  // Check Hanko service
  try {
    const response = await fetch(`${hankoURL}/.well-known/jwks.json`, {
      signal: AbortSignal.timeout(3000)
    })
    if (!response.ok) {
      errors.push(`❌ Hanko service at ${hankoURL} returned ${response.status}`)
    } else {
      console.log(`✓ Hanko service is running at ${hankoURL}`)
    }
  } catch (error) {
    errors.push(`❌ Hanko service is not accessible at ${hankoURL}`)
    if (error instanceof Error) {
      errors.push(`   Error: ${error.message}`)
    }
  }

  // Check API service
  try {
    const response = await fetch(`${apiURL}/health`, {
      signal: AbortSignal.timeout(3000)
    })
    if (!response.ok) {
      errors.push(`❌ NetMan API at ${apiURL} returned ${response.status}`)
    } else {
      console.log(`✓ NetMan API is running at ${apiURL}`)
    }
  } catch (error) {
    errors.push(`❌ NetMan API is not accessible at ${apiURL}`)
    if (error instanceof Error) {
      errors.push(`   Error: ${error.message}`)
    }
  }

  if (errors.length > 0) {
    console.error("\n" + "=".repeat(80))
    console.error("⚠️  PREREQUISITE SERVICES NOT RUNNING")
    console.error("=".repeat(80))
    errors.forEach((err) => console.error(err))
    console.error("\nThe E2E tests require the following services to be running:")
    console.error("  • Hanko authentication service")
    console.error("  • NetMan API backend")
    console.error("  • PostgreSQL database")
    console.error("\nTo start all services, run:")
    console.error("  cd .. && docker compose up -d --build")
    console.error("\nThen wait for services to be healthy before running tests.")
    console.error("Check service status with:")
    console.error("  docker compose ps")
    console.error("=".repeat(80) + "\n")

    throw new Error("Required services are not running. See error messages above.")
  }

  console.log("\n✅ All required services are running. Starting tests...\n")
}

export default globalSetup
