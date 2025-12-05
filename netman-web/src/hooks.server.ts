import { type Handle, redirect, type RequestEvent } from "@sveltejs/kit"
import { env } from "$env/dynamic/public"

const hankoApiUrl = env.PUBLIC_HANKO_API_URL

export const handle: Handle = async ({ event, resolve }) => {
  const session = await authenticatedUser(event)
  const path = event.url.pathname
  if (path.startsWith("/app") && !session.is_valid) {
    throw redirect(303, "/auth/login")
  }

  return resolve(event)
}

const authenticatedUser = async (event: RequestEvent) => {
  const { cookies } = event
  const cookieToken = cookies.get("hanko")

  if (!cookieToken) {
    return { is_valid: false }
  }

  const validationOptions = {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: `{"session_token": "${cookieToken}"}`
  }

  try {
    const response = await fetch(`${hankoApiUrl}/sessions/validate`, validationOptions)

    if (!response.ok) {
      console.error("Hanko session validation failed, response " + (await response.text()))
      return false
    }

    return await response.json()
  } catch (error) {
    return false
  }
}
