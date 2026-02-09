import type { TenantSummary } from "$lib/tenantModel"
import { basePath } from "$lib/server/common"

export const getTenantSummary = async (
  accessToken: string,
  tenantId: string
): Promise<TenantSummary> => {
  const response = await fetch(`${basePath()}/api/tenants/${tenantId}/summary`, {
    method: "GET",
    headers: [["Authorization", `Bearer ${accessToken}`]]
  })

  if (!response.ok) {
    throw new Error(
      `Failed to fetch tenant summary. API responded with ${response.status} ${response.statusText}`
    )
  }

  return (await response.json()) as TenantSummary
}
