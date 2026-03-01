import { expect, test } from "@playwright/test"

test("Follow Up Hub page displays pending follow-ups section", async ({ page }) => {
  await page.goto("/")
  await expect(page.getByRole('navigation').getByRole('link', { name: 'Go to Dashboard' })).toBeVisible()
  await page.getByRole('navigation').getByRole('link', { name: 'Go to Dashboard' }).click()

  await expect(page.locator("h1")).toHaveText("Dashboard")

  // Navigate to Follow-up hub via the Pending Actions card
  await page.getByText("View follow-up hub →").click()
  await expect(page.getByRole('link', { name: 'Follow-up hub', exact: true })).toBeVisible()

  // Check that the Pending Follow-ups section is present
  // It might show the heading if there are follow-ups, or a message if empty
  const pendingFollowUpsSection = page.getByText('No pending follow-ups found').or(page.getByRole('heading', { name: 'Pending Follow-ups' }))
  await expect(pendingFollowUpsSection).toBeVisible()
})