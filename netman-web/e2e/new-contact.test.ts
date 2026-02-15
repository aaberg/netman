import { expect, test } from "@playwright/test"

test("can register a new contact", async ({ page }) => {
  await page.goto("/")
  await expect(page.locator("a")).toHaveText("Go to my dashboard")
  await page.getByText("Go to my dashboard").click()

  await expect(page.locator("h1")).toHaveText("Dashboard")

  await page.getByText("Contact list").click()
  await expect(page.locator("h1")).toHaveText("Contact list")

  await page.getByText("+ New contact").click()
  await expect(page.locator("h1")).toHaveText("New contact")

  await page.locator("input[placeholder='Name of contact']").fill("John Doe")

  await page.getByText("+ Add email").click()
  await page.getByPlaceholder("Email address").fill("john.doe@example.com")
  await page
    .locator("li:has-text('Email address')")
    .locator("input[placeholder='Label']")
    .fill("Work")

  await page.getByText("+ Add phone").click()
  await page.getByPlaceholder("Phone number").fill("12345678")
  await page
    .locator("li:has-text('Phone number')")
    .locator("input[placeholder='Label']")
    .fill("Mobile")

  await page.getByText("+ Add note").click()
  await page.getByPlaceholder("Note").fill("This is a test note for John Doe")

  await page.getByText("Save").click()

  await expect(page.locator("h1")).toHaveText("Contact list")

  await expect(page.getByRole("cell", { name: "John Doe" })).toBeVisible()
  await expect(page.getByRole("cell", { name: "john.doe@example.com" })).toBeVisible()
})
