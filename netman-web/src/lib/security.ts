import { Hanko, type User } from "@teamhanko/hanko-elements"
import { env } from "$env/dynamic/public"
import { redirect } from "@sveltejs/kit"
import { goto } from "$app/navigation"

const hankoApiPath = env.PUBLIC_HANKO_API_URL

export function validateSession() {
  const hanko = new Hanko(hankoApiPath)
  hanko.validateSession().then((value) => {
    if (!value.is_valid) {
      goto("/auth/login")
    }
  })
}

export async function isAuthenticated(): Promise<Boolean> {
  const hanko = new Hanko(hankoApiPath)
  return (await hanko.validateSession()).is_valid
}

export async function fetchUserData(): Promise<User> {
  const hanko = new Hanko(hankoApiPath)
  return hanko.getUser()
}

export async function logout() {
  const hanko = new Hanko(hankoApiPath)
  await hanko.logout()
  goto("/")
}
