import { defineConfig } from "@playwright/test"

export default defineConfig({
  webServer: {
    command: "npm run build && dotenv -e .env npm run preview ",
    port: 4173
  },
  testDir: "e2e",
  globalSetup: "./e2e/setup.ts",
  use: {
    storageState: "./playwright/.auth/storage-state.json"
  }
})
