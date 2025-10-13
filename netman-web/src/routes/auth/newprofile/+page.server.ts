import type {Actions} from "./$types"
import type {CreateProfileRequest, UserProfile} from "$lib/profile"
import {createProfile} from "$lib/server/profile"
import {accessToken} from "$lib/server/common"
import {redirect} from "@sveltejs/kit"

export const actions = {
    default: async ({request, cookies}) => {
        const data = await request.formData()

        const profile : CreateProfileRequest = {
            name: data.get('name') as string
        }

        await createProfile(accessToken(cookies), profile)
        throw redirect(303, "/flow/authentication/continue")
    }

} satisfies Actions