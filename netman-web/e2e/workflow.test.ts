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

test.describe("End-to-End Workflow", () => {
  test("complete user journey: register -> create contact -> add follow-up -> view dashboard", async ({
    page
  }) => {
    // Step 1: Register a new user
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

    // Verify we're on the dashboard
    await expect(page).toHaveURL(/\/app\/.*\/dashboard/)

    // Step 2: Create a contact
    const tenant = getTenantFromUrl(page.url())
    expect(tenant).not.toBeNull()

    // Navigate to contacts via menu/link
    await page.goto(`/app/${tenant}/contacts`)
    await page.click('a[href*="/contacts/new"]')
    await waitForNavigation(page, /\/app\/.*\/contacts\/new/)

    const contactName = generateContactName()
    const contactEmail = generateTestEmail()
    const contactPhone = "+1234567890"

    await page.fill('input[name="name"]', contactName)
    await page.click('button:has-text("Add email")')
    await page.fill('input[placeholder*="email"]', contactEmail)
    await page.click('button:has-text("Add phone")')
    await page.fill('input[placeholder*="phone"]', contactPhone)
    await page.click('button[type="submit"]:has-text("Save")')
    await waitForNavigation(page, /\/app\/.*\/contacts$/)

    // Verify contact was created
    await expect(page.locator("text=" + contactName)).toBeVisible()

    // Step 3: View contact details
    await page.click("text=" + contactName)
    await waitForNavigation(page, /\/app\/.*\/contacts\/.*/)
    await expect(page.locator("h1")).toContainText(contactName)
    await expect(page.locator("text=" + contactEmail)).toBeVisible()
    await expect(page.locator("text=" + contactPhone)).toBeVisible()

    // Step 4: Create a follow-up task for this contact
    await page.goto(`/app/${tenant}/tasks/new`)

    // Find and select our contact
    await page.selectOption("select#contact", { label: contactName })

    const followUpNote = "Discuss project timeline and deliverables"
    await page.fill("textarea#note", followUpNote)

    // Use relative time (7 days)
    await page.fill("input#relativeSpan", "7")

    await page.click('button[type="submit"]:has-text("Create Task")')
    await waitForNavigation(page, /\/app\/.*\/tasks$/)

    // Verify task was created
    await expect(page.locator("text=" + followUpNote)).toBeVisible()

    // Verify task is linked to the correct contact
    await expect(
      page.locator(`a[href*="${tenant}/contacts"]:has-text("${contactName}")`)
    ).toBeVisible()

    // Step 5: Navigate back to dashboard
    await page.goto(`/app/${tenant}/dashboard`)
    await expect(page).toHaveURL(/\/app\/.*\/dashboard/)

    // Step 6: Navigate to contacts and verify our contact is still there
    await page.goto(`/app/${tenant}/contacts`)
    await expect(page.locator("text=" + contactName)).toBeVisible()

    // Step 7: Navigate to tasks and verify our task is still there
    await page.goto(`/app/${tenant}/tasks`)
    await expect(page.locator("text=" + followUpNote)).toBeVisible()
  })

  test("user can navigate between different sections of the app", async ({ page }) => {
    // Create a user first
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

    const tenant = getTenantFromUrl(page.url())

    // Test navigation to contacts
    await page.goto(`/app/${tenant}/contacts`)
    await expect(page).toHaveURL(/\/app\/.*\/contacts/)
    await expect(page.locator("h1:has-text('Contacts')")).toBeVisible()

    // Test navigation to tasks
    await page.goto(`/app/${tenant}/tasks`)
    await expect(page).toHaveURL(/\/app\/.*\/tasks/)
    await expect(page.locator("text=Follow-up hub")).toBeVisible()

    // Test navigation back to dashboard
    await page.goto(`/app/${tenant}/dashboard`)
    await expect(page).toHaveURL(/\/app\/.*\/dashboard/)
  })

  test("multiple contacts and follow-ups workflow", async ({ page }) => {
    // Create user
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

    const tenant = getTenantFromUrl(page.url())

    // Create multiple contacts
    const contacts = [
      { name: generateContactName(), email: generateTestEmail() },
      { name: generateContactName(), email: generateTestEmail() },
      { name: generateContactName(), email: generateTestEmail() }
    ]

    for (const contact of contacts) {
      await page.goto(`/app/${tenant}/contacts/new`)
      await page.fill('input[name="name"]', contact.name)
      await page.click('button:has-text("Add email")')
      await page.fill('input[placeholder*="email"]', contact.email)
      await page.click('button[type="submit"]:has-text("Save")')
      await waitForNavigation(page, /\/app\/.*\/contacts$/)
    }

    // Verify all contacts are in the list
    await page.goto(`/app/${tenant}/contacts`)
    for (const contact of contacts) {
      await expect(page.locator("text=" + contact.name)).toBeVisible()
    }

    // Create follow-ups for each contact
    for (let i = 0; i < contacts.length; i++) {
      await page.goto(`/app/${tenant}/tasks/new`)
      await page.selectOption("select#contact", { label: contacts[i].name })
      await page.fill("textarea#note", `Follow-up task ${i + 1} for ${contacts[i].name}`)
      await page.fill("input#relativeSpan", String((i + 1) * 7)) // 7, 14, 21 days
      await page.click('button[type="submit"]:has-text("Create Task")')
      await waitForNavigation(page, /\/app\/.*\/tasks$/)
    }

    // Verify all follow-ups are in the list
    await page.goto(`/app/${tenant}/tasks`)
    for (let i = 0; i < contacts.length; i++) {
      await expect(
        page.locator(`text=Follow-up task ${i + 1} for ${contacts[i].name}`)
      ).toBeVisible()
    }
  })
})
