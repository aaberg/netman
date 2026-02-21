import { BrowserContext, expect, Page } from "@playwright/test"
import { defaultPassword, generateUniqueEmail, getOneTimeCodeFromMailSlurper } from "./test-utils"

export async function registerAccount(page: Page, context: BrowserContext) {
  await context.grantPermissions(["clipboard-read", "clipboard-write"])
  await context.clearCookies()
  await page.goto("http://localhost:4173/")
  await expect(page.getByRole('link', { name: 'Login' })).toBeVisible()
  await page.getByRole('link', { name: 'Login' }).click()
  await expect(page.locator("h1")).toHaveText("Sign in")

  await page.getByText("Don't have an account?").click()
  await expect(page.locator("h1")).toHaveText("Create account")

  const email = generateUniqueEmail()
  await page.locator("input[name=email]").isVisible()
  await page.locator("input[type=email]").fill(email)
  await page.waitForTimeout(500)
  await page.getByText("Continue").waitFor({ state: "visible" })
  await page.getByRole("button").getByText("Continue").click()

  await expect(page.locator("h1")).toHaveText("Enter passcode")
  const otc = await getOneTimeCodeFromMailSlurper(email)

  // add otc to clipboard

  await page.evaluate(async (text) => {
    await navigator.clipboard.writeText(text)
  }, otc)

  await page.locator("input[name=passcode0]").focus()
  await page.keyboard.press("Control+v")

  await expect(page.locator("h1")).toHaveText("Set new password")
  await page.locator("input[type=password]").fill(defaultPassword())
  await page.locator("input[type=password]").press("Enter")

  await expect(page.locator("h1")).toHaveText("Set up MFA")
  await page.getByText("Skip").click()

  await expect(page.locator("h1")).toHaveText("Create a passkey")
  await page.getByText("Skip").click()

  // out of Hanko, back in netman
  await expect(page.locator("h1")).toContainText("more information")
  await page.locator("input[name=name]").fill("Test User")
  await page.getByRole("button").getByText("Submit").click()
  await expect(page.locator("h1")).toHaveText("Dashboard")
}
