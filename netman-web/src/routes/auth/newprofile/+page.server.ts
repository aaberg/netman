import type {Actions} from "./$types"
import type {UserProfile} from "$lib/profile"
import {createProfile} from "$lib/server/profile"
import {accessToken} from "$lib/server/common"
import {redirect} from "@sveltejs/kit"

export const actions = {
    default: async ({request, cookies}) => {
        const data = await request.formData()

        const profile : UserProfile = {
            name: data.get('name') as string,
            initials: data.get('initials') as string
        }

        await createProfile(accessToken(cookies), profile)
        throw redirect(303, "/flow/authentication/continue")
    }

} satisfies Actions