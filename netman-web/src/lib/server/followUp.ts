import type { FollowUpActionResource, RegisterFollowUpRequest, FollowUpResource, ActionResource } from "$lib/followUpModel"
import { basePath } from "$lib/server/common"
import type { Page } from "$lib/page"

export const registerFollowUp = async (
  accessToken: string,
  tenantId: string,
  followUpRequest: RegisterFollowUpRequest
): Promise<FollowUpActionResource> => {
  const response = await fetch(`${basePath()}/api/tenants/${tenantId}/scheduled-follow-ups/v2`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${accessToken}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify(followUpRequest)
  })

  if (!response.ok) {
    throw new Error(`Failed to register follow-up: ${response.status} ${response.statusText}`)
  }

  const responseJson = await response.json()
  return responseJson as FollowUpActionResource
}

export const getFollowUps = async (
  accessToken: string,
  tenantId: string,
  status?: string
): Promise<Page<FollowUpResource>> => {
  let url = `${basePath()}/api/tenants/${tenantId}/followups`
  const params = new URLSearchParams()
  if (status) {
    params.append('status', status)
  }
  if (params.toString()) {
    url += '?' + params.toString()
  }

  const response = await fetch(url, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${accessToken}`
    }
  })

  if (!response.ok) {
    throw new Error(
      `Failed to fetch follow-ups from API: ${response.status} ${response.statusText}`
    )
  }

  return (await response.json()) as Page<FollowUpResource>
}

export const getActions = async (
  accessToken: string,
  tenantId: string
): Promise<Page<ActionResource>> => {
  const response = await fetch(`${basePath()}/api/tenants/${tenantId}/actions`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${accessToken}`
    }
  })

  if (!response.ok) {
    throw new Error(
      `Failed to fetch actions from API: ${response.status} ${response.statusText}`
    )
  }

  return (await response.json()) as Page<ActionResource>
}
