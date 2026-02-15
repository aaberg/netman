import { chromium } from "@playwright/test"
import { registerAccount } from "./registerUser"

async function globalSetup() {
  const browser = await chromium.launch()
  const page = await browser.newPage()

  await registerAccount(page, page.context())
  await page.context().storageState({ path: "./playwright/.auth/storage-state.json" })
  await browser.close()
}

export default globalSetup
