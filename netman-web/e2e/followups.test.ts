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
  waitForNavigation
} from "./utils/test-helpers"

test.describe("Follow-ups", () => {
  // Setup: Create a user and a contact before each test
  test.beforeEach(async ({ page }) => {
    const email = generateTestEmail()
    const password = "TestPassword123!"
    const name = generateTestName()

    // Create user
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

    // Create a test contact
    const tenant = getTenantFromUrl(page.url())
    const contactName = generateContactName()
    await page.goto(`/app/${tenant}/contacts/new`)
    await page.fill('input[name="name"]', contactName)
    await page.click('button[type="submit"]:has-text("Save")')
    await waitForNavigation(page, /\/app\/.*\/contacts$/)
  })

  test("user can add a follow-up with relative time", async ({ page }) => {
    const tenant = getTenantFromUrl(page.url())
    expect(tenant).not.toBeNull()

    // Navigate to tasks page
    await page.goto(`/app/${tenant}/tasks`)
    await expect(page.locator("text=Follow-up hub")).toBeVisible()

    // Click "Add follow-up task" button
    await page.click('a[href*="/tasks/new"]')
    await waitForNavigation(page, /\/app\/.*\/tasks\/new/)

    // Verify we're on the add task page
    await expect(page.locator("h1")).toContainText("Add follow-up task")

    // Select a contact
    await page.selectOption('select#contact', { index: 1 })

    // Fill in note
    const followUpNote = "Call this contact to discuss the project"
    await page.fill('textarea#note', followUpNote)

    // Verify relative time is selected by default
    await expect(page.locator('input[value="relative"]')).toBeChecked()

    // Set relative time (7 days is default)
    await page.fill('input#relativeSpan', "7")

    // Create the task
    await page.click('button[type="submit"]:has-text("Create Task")')

    // Wait for redirect to tasks page
    await waitForNavigation(page, /\/app\/.*\/tasks$/)

    // Verify the task appears in the list
    await expect(page.locator("text=" + followUpNote)).toBeVisible()
    await expect(page.locator("text=Pending")).toBeVisible()
  })

  test("user can add a follow-up with absolute time", async ({ page }) => {
    const tenant = getTenantFromUrl(page.url())

    // Navigate to add task page
    await page.goto(`/app/${tenant}/tasks/new`)

    // Select a contact
    await page.selectOption('select#contact', { index: 1 })

    // Fill in note
    const followUpNote = "Send follow-up email about meeting"
    await page.fill('textarea#note', followUpNote)

    // Select absolute time
    await page.click('input[value="absolute"]')

    // Set a future date/time (tomorrow at 10:00)
    const tomorrow = new Date()
    tomorrow.setDate(tomorrow.getDate() + 1)
    tomorrow.setHours(10, 0, 0, 0)
    const dateTimeString = tomorrow.toISOString().slice(0, 16) // Format: YYYY-MM-DDTHH:MM
    await page.fill('input#triggerTime', dateTimeString)

    // Create the task
    await page.click('button[type="submit"]:has-text("Create Task")')

    // Wait for redirect
    await waitForNavigation(page, /\/app\/.*\/tasks$/)

    // Verify the task appears
    await expect(page.locator("text=" + followUpNote)).toBeVisible()
  })

  test("user can view follow-up tasks list", async ({ page }) => {
    const tenant = getTenantFromUrl(page.url())

    // Create a follow-up task first
    await page.goto(`/app/${tenant}/tasks/new`)
    await page.selectOption('select#contact', { index: 1 })
    await page.fill('textarea#note', "Test follow-up task")
    await page.click('button[type="submit"]:has-text("Create Task")')
    await waitForNavigation(page, /\/app\/.*\/tasks$/)

    // Verify we're on the tasks page
    await expect(page.locator("text=Follow-up hub")).toBeVisible()

    // Verify table headers
    await expect(page.locator("th:has-text('Status')")).toBeVisible()
    await expect(page.locator("th:has-text('Note')")).toBeVisible()
    await expect(page.locator("th:has-text('Contact')")).toBeVisible()
    await expect(page.locator("th:has-text('Trigger Time')")).toBeVisible()

    // Verify task appears in table
    await expect(page.locator("text=Test follow-up task")).toBeVisible()
  })

  test("form validation prevents creating invalid follow-up", async ({ page }) => {
    const tenant = getTenantFromUrl(page.url())

    await page.goto(`/app/${tenant}/tasks/new`)

    // Try to submit without filling required fields
    const submitButton = page.locator('button[type="submit"]:has-text("Create Task")')
    await expect(submitButton).toBeDisabled()

    // Fill contact
    await page.selectOption('select#contact', { index: 1 })
    await expect(submitButton).toBeDisabled()

    // Fill note
    await page.fill('textarea#note', "Valid note")

    // Now button should be enabled
    await expect(submitButton).toBeEnabled()
  })
})
