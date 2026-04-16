import type {
  ContactListItem,
  ContactSavedResponse,
  ContactWithDetails,
  SaveContactRequest,
  TemporaryImageUploadResponse
} from "$lib/contactModel"
import { basePath } from "$lib/server/common"

export const saveContact = async (
  accessToken: string,
  tenantId: string,
  contact: SaveContactRequest
): Promise<ContactSavedResponse> => {
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

  return responseJson as ContactSavedResponse
}

export const getContactList = async (
  accessToken: string,
  tenantId: string
): Promise<ContactListItem[]> => {
  const response = await fetch(`${basePath()}/api/tenants/${tenantId}/contacts`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${accessToken}`
    }
  })

  if (!response.ok) {
    throw new Error(
      `Error fetching contact list for tenant ${tenantId}. API responded with ${response.status} ${response.statusText}`
    )
  }
  return (await response.json()) as ContactListItem[]
}

export async function getContactsById(
  accessToken: string,
  tenantId: string,
  contactId: string
): Promise<ContactWithDetails> {
  const response = await fetch(`${basePath()}/api/tenants/${tenantId}/contacts/${contactId}`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${accessToken}`
    }
  })

  if (!response.ok) {
    throw new Error(`Error fetching contact with id ${contactId} from tenant with id ${tenantId}.`)
  }
  return (await response.json()) as ContactWithDetails
}

export async function uploadTemporaryContactImage(
  accessToken: string,
  tenantId: string,
  image: ArrayBuffer
): Promise<TemporaryImageUploadResponse> {
  const response = await fetch(`${basePath()}/api/tenants/${tenantId}/contacts/images/temp`, {
    method: "PUT",
    headers: {
      Authorization: `Bearer ${accessToken}`,
      "Content-Type": "application/octet-stream"
    },
    body: image
  })

  if (!response.ok) {
    throw new Error(
      `Error uploading temporary contact image for tenant ${tenantId}. API responded with ${response.status} ${response.statusText}`
    )
  }

  return (await response.json()) as TemporaryImageUploadResponse
}
