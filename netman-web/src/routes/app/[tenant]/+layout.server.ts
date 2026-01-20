import type { LayoutServerLoad } from "./$types"
import { getProfile } from "$lib/server/profile"
import { accessToken } from "$lib/server/common"
import { redirect } from "@sveltejs/kit"

export const load: LayoutServerLoad = async ({ params, cookies }) => {

  const profile = await getProfile(accessToken(cookies))
  if (profile == null) {
    return redirect(303, "/auth/newprofile")
  }

  return {
    tenant: params.tenant,
    profile
  }
}
