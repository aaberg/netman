import { redirect, type Handle, type RequestEvent } from "@sveltejs/kit"
import { env } from "$env/dynamic/public"

const hankoApiUrl = env.PUBLIC_HANKO_API_URL

export const handle: Handle = async ({ event, resolve }) => {
  const session = await authenticatedUser(event)
  const path = event.url.pathname
  if ((path.startsWith("/dashboard") || path.startsWith("/profile")) && !session.is_valid) {
    throw redirect(303, "/auth/login")
  }

  return resolve(event);
}

const authenticatedUser = async (event: RequestEvent) => {
  const { cookies } = event
  const cookieToken = cookies.get("hanko")

  const validationOptions = {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: `{"session_token": "${cookieToken}"}`
  }

  try {
    const response = await fetch(`${hankoApiUrl}/sessions/validate`, validationOptions)

    if (!response.ok) {
      throw new Error("Hanko session validation failed, response " + (await response.text()))
    }

    const session = await response.json()
    console.log(session)
    return session
  } catch (error) {
    console.log(error)
    return false
  }
}
