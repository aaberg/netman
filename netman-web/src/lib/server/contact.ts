import type { Contact, ContactWithDetails } from "$lib/contactModel"
import { basePath } from "$lib/server/common"

export const saveContact = async (
  accessToken: string,
  tenantId: string,
  contact: ContactWithDetails
): Promise<ContactWithDetails> => {
  const body = JSON.stringify(contact)
  const response = await fetch(`${basePath()}/api/tenants/${tenantId}/contacts`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${accessToken}`,
      "Content-Type": "application/json"
    },
    body
  })
  if (!response.ok) {
    throw new Error(`Error saving profile. body of request: ${body}`)
  }

  const responseJson = await response.json()

  return responseJson as ContactWithDetails
}

export const getContactsForTenant = async (
  accessToken: string,
  tenantId: string
): Promise<Contact[]> => {
  const response = await fetch(`${basePath()}/api/tenants/${tenantId}/contacts`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${accessToken}`
    }
  })

  if (!response.ok) {
    throw new Error(
      `Error fetching contacts for tenant ${tenantId}. API responded with ${response.status} ${response.statusText}`
    )
  }

  return (await response.json()) as Contact[]
}

export async function getContactsById(
  accessToken: string,
  tenantId: string,
  contactId: string
): Promise<ContactWithDetails> {
  const response = await fetch(`${basePath()}/api/tenants/${tenantId}/contacts/${contactId}`)

  if (!response.ok) {
    throw new Error(`Error fetching contact with id ${contactId} from tenant with id ${tenantId}.`)
  }
  return (await response.json()) as ContactWithDetails
}

export async function getLabelsForTenant(
  accessToken: string,
  tenantId: string
): Promise<{ id: string; label: string; tenantId: number }[]> {
  const response = await fetch(`${basePath()}/api/tenants/${tenantId}/labels`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${accessToken}`
    }
  })

  if (!response.ok) {
    throw new Error(
      `Error fetching labels for tenant ${tenantId}. API responded with ${response.status} ${response.statusText}`
    )
  }

  return (await response.json()) as { id: string; label: string; tenantId: number }[]
}
