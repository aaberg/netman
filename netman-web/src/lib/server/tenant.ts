import type { MemberTenant, Tenant } from "$lib/tenantModel"
import { basePath } from "$lib/server/common"

export const getDefaultTenant = async (accessToken: string): Promise<MemberTenant> => {
  const response = await fetch(`${basePath()}/api/tenants/default`, {
    method: "GET",
    headers: [["Authorization", `Bearer ${accessToken}`]]
  })

  if (!response.ok) {
    throw new Error("error fetching default tenant")
  }

  return (await response.json()) as MemberTenant
}
