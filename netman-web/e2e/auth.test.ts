import { expect, test } from "@playwright/test"
import {
  clickHankoButton,
  fillHankoEmail,
  fillHankoPassword,
  generateTestEmail,
  generateTestName,
  waitForHankoAuth,
  waitForNavigation
} from "./utils/test-helpers"

test.describe("Authentication", () => {
  test("user can register a new account", async ({ page }) => {
    const email = generateTestEmail()
    const password = "TestPassword123!"
    const name = generateTestName()

    // Navigate to login page
    await page.goto("/auth/login")

    // Wait for Hanko auth component
    await waitForHankoAuth(page)

    // Fill in email
    await fillHankoEmail(page, email)

    // Click continue button
    await clickHankoButton(page, "Continue")

    // Wait a bit for the next step
    await page.waitForTimeout(1000)

    // Fill in password (for new account, this should create a passcode)
    await fillHankoPassword(page, password)

    // Click continue/register button
    await clickHankoButton(page, "Continue")

    // Wait for redirect to profile creation
    await waitForNavigation(page, /\/auth\/newprofile/)

    // Fill in the profile name
    await page.fill('input[name="name"]', name)

    // Submit the form
    await page.click('button[type="submit"]')

    // Wait for redirect to dashboard
    await waitForNavigation(page, /\/app\/.*\/dashboard/)

    // Verify we're on the dashboard
    await expect(page).toHaveURL(/\/app\/.*\/dashboard/)
  })

  test("user can login with existing account", async ({ page, context }) => {
    // First, create an account
    const email = generateTestEmail()
    const password = "TestPassword123!"
    const name = generateTestName()

    await page.goto("/auth/login")
    await waitForHankoAuth(page)
    await fillHankoEmail(page, email)
    await clickHankoButton(page, "Continue")
    await page.waitForTimeout(1000)
    await fillHankoPassword(page, password)
    await clickHankoButton(page, "Continue")
    await waitForNavigation(page, /\/auth\/newprofile/)
    await page.fill('input[name="name"]', name)
    await page.click('button[type="submit"]')
    await waitForNavigation(page, /\/app\/.*\/dashboard/)

    // Now logout (clear cookies/session)
    await context.clearCookies()
    await page.goto("/auth/login")

    // Login again
    await waitForHankoAuth(page)
    await fillHankoEmail(page, email)
    await clickHankoButton(page, "Continue")
    await page.waitForTimeout(1000)
    await fillHankoPassword(page, password)
    await clickHankoButton(page, "Continue")

    // Should go directly to dashboard (profile already created)
    await waitForNavigation(page, /\/app\/.*\/dashboard/)
    await expect(page).toHaveURL(/\/app\/.*\/dashboard/)
  })
})
