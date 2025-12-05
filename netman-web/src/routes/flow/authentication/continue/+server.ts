import type { RequestHandler } from "./$types"
import { getProfile } from "$lib/server/profile"
import { accessToken } from "$lib/server/common"
import { redirect } from "@sveltejs/kit"
import { getDefaultTenant } from "$lib/server/tenant"

export const GET: RequestHandler = async ({ cookies }) => {
  const profile = await getProfile(accessToken(cookies))

  if (profile === null) {
    return redirect(303, "/auth/newprofile")
  }

  const defaultTenant = await getDefaultTenant(accessToken(cookies))
  return redirect(303, `/app/${defaultTenant.tenant.id}/dashboard`)
}
