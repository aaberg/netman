import { registerAccount } from "./registerUser"
import { test } from "@playwright/test"


test("can register a new user", async ({page, context}) => {
  await registerAccount(page, context)
})

