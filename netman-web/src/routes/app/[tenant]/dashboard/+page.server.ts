import type { PageServerLoad } from "./$types"
import { getTenantSummary } from "$lib/server/tenantSummary"
import { accessToken } from "$lib/server/common"
import { error } from "@sveltejs/kit"

export const load: PageServerLoad = async ({ params, cookies }) => {
  try {
    const tenantId = BigInt(params.tenant)
    const summary = await getTenantSummary(accessToken(cookies), tenantId)
    return { summary }
  } catch (e) {
    console.error("Failed to load tenant summary:", e)
    throw error(500, "Failed to load tenant summary")
  }
}
