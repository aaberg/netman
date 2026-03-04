import type { PageServerLoad } from "./$types"
import { accessToken } from "$lib/server/common"
import { getFollowUps, getActions } from "$lib/server/followUp"
import { getTenantSummary } from "$lib/server/tenantSummary"

export const load: PageServerLoad = async ({ cookies, params }) => {
  const { tenant } = params
  const token = accessToken(cookies)

  const followUpsPage = await getFollowUps(token, tenant)
  const actionsPage = await getActions(token, tenant)

  return { followUpsPage, actionsPage }
}
