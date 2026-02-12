import type { Page } from "@playwright/test"

/**
 * Generate a unique email for testing
 */
export function generateTestEmail(): string {
  const timestamp = Date.now()
  const random = Math.floor(Math.random() * 10000)
  return `test-${timestamp}-${random}@example.com`
}

/**
 * Generate a unique name for testing
 */
export function generateTestName(): string {
  const timestamp = Date.now()
  const random = Math.floor(Math.random() * 10000)
  return `Test User ${timestamp}-${random}`
}

/**
 * Generate a unique contact name
 */
export function generateContactName(): string {
  const timestamp = Date.now()
  const random = Math.floor(Math.random() * 10000)
  return `Contact ${timestamp}-${random}`
}

/**
 * Wait for Hanko authentication element to be ready
 */
export async function waitForHankoAuth(page: Page): Promise<void> {
  await page.waitForSelector("hanko-auth", { timeout: 10000 })
  // Wait a bit for the shadow DOM to be ready
  await page.waitForTimeout(1000)
}

/**
 * Fill Hanko email input in shadow DOM
 */
export async function fillHankoEmail(page: Page, email: string): Promise<void> {
  await waitForHankoAuth(page)
  // Hanko uses shadow DOM, so we need to access it differently
  const hankoAuth = await page.locator("hanko-auth")
  await hankoAuth.evaluate((el, emailValue) => {
    const shadowRoot = el.shadowRoot
    if (!shadowRoot) throw new Error("Shadow root not found")
    const emailInput = shadowRoot.querySelector('input[name="email"]') as HTMLInputElement
    if (!emailInput) throw new Error("Email input not found")
    emailInput.value = emailValue
    emailInput.dispatchEvent(new Event("input", { bubbles: true }))
  }, email)
}

/**
 * Fill Hanko password input in shadow DOM
 */
export async function fillHankoPassword(page: Page, password: string): Promise<void> {
  const hankoAuth = await page.locator("hanko-auth")
  await hankoAuth.evaluate((el, passwordValue) => {
    const shadowRoot = el.shadowRoot
    if (!shadowRoot) throw new Error("Shadow root not found")
    const passwordInput = shadowRoot.querySelector('input[name="password"]') as HTMLInputElement
    if (!passwordInput) throw new Error("Password input not found")
    passwordInput.value = passwordValue
    passwordInput.dispatchEvent(new Event("input", { bubbles: true }))
  }, password)
}

/**
 * Click a button in Hanko shadow DOM
 */
export async function clickHankoButton(page: Page, buttonText: string): Promise<void> {
  const hankoAuth = await page.locator("hanko-auth")
  await hankoAuth.evaluate((el, text) => {
    const shadowRoot = el.shadowRoot
    if (!shadowRoot) throw new Error("Shadow root not found")
    const buttons = Array.from(shadowRoot.querySelectorAll("button"))
    const button = buttons.find((btn) => btn.textContent?.includes(text))
    if (!button) throw new Error(`Button with text "${text}" not found`)
    button.click()
  }, buttonText)
}

/**
 * Wait for navigation to complete
 */
export async function waitForNavigation(page: Page, urlPattern: string | RegExp): Promise<void> {
  await page.waitForURL(urlPattern, { timeout: 10000 })
}

/**
 * Get the current tenant ID from URL
 */
export function getTenantFromUrl(url: string): string | null {
  const match = url.match(/\/app\/([^/]+)/)
  return match ? match[1] : null
}
