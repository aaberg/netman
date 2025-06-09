import { redirect, type RequestEvent } from '@sveltejs/kit'
import { env } from "$env/dynamic/public"

const hankoApiUrl = env.PUBLIC_HANKO_API_URL

export async function handle({ event, resolve }) {
    const verified = await authenticatedUser(event)
    const path = event.url.pathname
    if ((path.startsWith("/dashboard") || path.startsWith("/profile")) && !verified) {
        throw redirect(303, "/auth/login")
    }

    return await resolve(event)
}

const authenticatedUser = async (event: RequestEvent) => {
    const { cookies } = event
    const cookieToken = cookies.get("hanko")

    const validationOptions = {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: `{"session_token": "${cookieToken}"}`,
    }

    try {
        const response = await fetch(`${hankoApiUrl}/sessions/validate`, validationOptions)

        if (!response.ok) {
            throw new Error('Hanko session validation failed, response ' + await response.text())
        }

        const verifiedResponse = await response.json();
        console.log(verifiedResponse)
        return verifiedResponse.is_valid
    } catch (error) {
        console.log(error)
        return false
    }
}