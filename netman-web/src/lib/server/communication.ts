import type { CommunicationResource, RegisterCommunicationResource } from "$lib/communicationModel"
import { basePath } from "$lib/server/common"

export const getCommunications = async (
  accessToken: string,
  tenantId: string,
  contactId: string
): Promise<CommunicationResource[]> => {
  const response = await fetch(`${basePath()}/api/tenants/${tenantId}/contacts/${contactId}/communications`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${accessToken}`
    }
  })

  if (!response.ok) {
    throw new Error(`Error fetching communications for contact ${contactId}. API responded with ${response.status} ${response.statusText}`)
  }

  return (await response.json()) as CommunicationResource[]
}

export const registerCommunication = async (
  accessToken: string,
  tenantId: string,
  contactId: string,
  communication: RegisterCommunicationResource
): Promise<CommunicationResource> => {
  const body = JSON.stringify(communication)
  const response = await fetch(`${basePath()}/api/tenants/${tenantId}/contacts/${contactId}/communications`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${accessToken}`,
      "Content-Type": "application/json"
    },
    body
  })

  if (!response.ok) {
    throw new Error(`Error registering communication. Body of request: ${body}`)
  }

  return (await response.json()) as CommunicationResource
}
