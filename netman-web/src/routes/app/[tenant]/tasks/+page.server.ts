import type { PageServerLoad } from "./$types"
import { accessToken } from "$lib/server/common"
import {getFollowUps} from "$lib/server/followUp";

export const load: PageServerLoad = async ({ cookies, params }) => {
  const { tenant } = params
  const token = accessToken(cookies)

  const followUpsPage = await getFollowUps(token, tenant)

  return { followUpsPage }
}
