import { expect, test } from "@playwright/test"
import {
  clickHankoButton,
  fillHankoEmail,
  fillHankoPassword,
  generateContactName,
  generateTestEmail,
  generateTestName,
  getTenantFromUrl,
  waitForHankoAuth,
  waitForHankoPasswordField,
  waitForNavigation
} from "./utils/test-helpers"

test.describe("Contacts", () => {
  // Setup: Create a user before each test
  test.beforeEach(async ({ page }) => {
    const email = generateTestEmail()
    const password = "TestPassword123!"
    const name = generateTestName()

    await page.goto("/auth/login")
    await waitForHankoAuth(page)
    await fillHankoEmail(page, email)
    await clickHankoButton(page, "Continue")
    await waitForHankoPasswordField(page)
    await fillHankoPassword(page, password)
    await clickHankoButton(page, "Continue")
    await waitForNavigation(page, /\/auth\/newprofile/)
    await page.fill('input[name="name"]', name)
    await page.click('button[type="submit"]')
    await waitForNavigation(page, /\/app\/.*\/dashboard/)
  })

  test("user can add a new contact with basic information", async ({ page }) => {
    const contactName = generateContactName()
    const contactEmail = generateTestEmail()

    // Get tenant ID from URL
    const tenant = getTenantFromUrl(page.url())
    expect(tenant).not.toBeNull()

    // Navigate to contacts page
    await page.goto(`/app/${tenant}/contacts`)
    await expect(page.locator("h1")).toContainText("Contacts")

    // Click "Add Contact" button
    await page.click('a[href*="/contacts/new"]')
    await waitForNavigation(page, /\/app\/.*\/contacts\/new/)

    // Fill in contact name
    await page.fill('input[name="name"]', contactName)

    // Add email detail
    await page.click('button:has-text("Add email")')
    await page.fill('input[placeholder*="email"]', contactEmail)

    // Save the contact
    await page.click('button[type="submit"]:has-text("Save")')

    // Wait for redirect to contacts list
    await waitForNavigation(page, /\/app\/.*\/contacts$/)

    // Verify contact appears in the list
    await expect(page.locator("text=" + contactName)).toBeVisible()
  })

  test("user can add a contact with multiple details", async ({ page }) => {
    const contactName = generateContactName()
    const contactEmail = generateTestEmail()
    const contactPhone = "+1234567890"
    const contactNote = "This is a test contact with multiple details"

    const tenant = getTenantFromUrl(page.url())
    expect(tenant).not.toBeNull()

    // Navigate to new contact page
    await page.goto(`/app/${tenant}/contacts/new`)

    // Fill in contact name
    await page.fill('input[name="name"]', contactName)

    // Add email
    await page.click('button:has-text("Add email")')
    await page.fill('input[placeholder*="email"]', contactEmail)

    // Add phone
    await page.click('button:has-text("Add phone")')
    await page.fill('input[placeholder*="phone"]', contactPhone)

    // Add note
    await page.click('button:has-text("Add note")')
    await page.fill('textarea[placeholder*="note"]', contactNote)

    // Save the contact
    await page.click('button[type="submit"]:has-text("Save")')

    // Wait for redirect
    await waitForNavigation(page, /\/app\/.*\/contacts$/)

    // Verify contact appears
    await expect(page.locator("text=" + contactName)).toBeVisible()

    // Navigate to contact detail page
    await page.click("text=" + contactName)
    await waitForNavigation(page, /\/app\/.*\/contacts\/.*/)

    // Verify all details are displayed
    await expect(page.locator("text=" + contactEmail)).toBeVisible()
    await expect(page.locator("text=" + contactPhone)).toBeVisible()
    await expect(page.locator("text=" + contactNote)).toBeVisible()
  })

  test("user can view contact details", async ({ page }) => {
    const contactName = generateContactName()
    const contactEmail = generateTestEmail()

    const tenant = getTenantFromUrl(page.url())

    // Create a contact
    await page.goto(`/app/${tenant}/contacts/new`)
    await page.fill('input[name="name"]', contactName)
    await page.click('button:has-text("Add email")')
    await page.fill('input[placeholder*="email"]', contactEmail)
    await page.click('button[type="submit"]:has-text("Save")')
    await waitForNavigation(page, /\/app\/.*\/contacts$/)

    // Click on the contact to view details
    await page.click("text=" + contactName)
    await waitForNavigation(page, /\/app\/.*\/contacts\/.*/)

    // Verify contact name is displayed
    await expect(page.locator("h1")).toContainText(contactName)

    // Verify email is displayed
    await expect(page.locator("text=" + contactEmail)).toBeVisible()

    // Verify back button exists
    await expect(page.locator('a:has-text("Back")')).toBeVisible()
  })
})
