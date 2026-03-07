import type { PageServerLoad } from "./$types"
import { getCommunications } from "$lib/server/communication"
import { accessToken } from "$lib/server/common"

export const load: PageServerLoad = async ({ cookies, params }) => {
  const { tenant, contact } = params

  const communications = await getCommunications(accessToken(cookies), tenant, contact)
  return { communications }
}
