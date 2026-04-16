import { accessToken } from "$lib/server/common"
import { saveContact, uploadTemporaryContactImage } from "$lib/server/contact"
import { emptyContactForm, normalizeContactFormValue } from "$lib/contactForm"
import type { SaveContactRequest } from "$lib/contactModel"
import type { Cookies } from "@sveltejs/kit"

export const MAX_CONTACT_IMAGE_SIZE_BYTES = 1024 * 1024
export const CONTACT_SAVE_ERROR_MESSAGE = "Unable to save contact."

export interface ContactFormState {
  values: SaveContactRequest
  error: string | null
}

export const defaultContactFormState = (values: SaveContactRequest = emptyContactForm()): ContactFormState => ({
  values,
  error: null
})

export function parseContactFormData(data: FormData, contactId: string | null = null): SaveContactRequest {
  return {
    id: contactId,
    name: normalizeContactFormValue(data.get("name")) ?? "",
    email: normalizeContactFormValue(data.get("email")),
    phone: normalizeContactFormValue(data.get("phone")),
    title: normalizeContactFormValue(data.get("title")),
    organization: normalizeContactFormValue(data.get("organization")),
    location: normalizeContactFormValue(data.get("location")),
    notes: normalizeContactFormValue(data.get("notes")),
    tempFileId: null,
    tempFileMimeType: null,
    tempFileExtension: null
  }
}

export async function submitContactForm(
  data: FormData,
  cookies: Cookies,
  tenantId: string,
  contactId: string | null = null
): Promise<ContactFormState & { savedContactId?: string }> {
  const image = data.get("image")
  const values = parseContactFormData(data, contactId)

  if (!values.name) {
    return {
      values,
      error: "Name is required."
    }
  }

  if (image instanceof File && image.size > MAX_CONTACT_IMAGE_SIZE_BYTES) {
    return {
      values,
      error: "Image must be 1 MB or smaller."
    }
  }

  const token = accessToken(cookies)

  if (image instanceof File && image.size > 0) {
    const upload = await uploadTemporaryContactImage(token, tenantId, await image.arrayBuffer())
    values.tempFileId = upload.tempFileId
    values.tempFileMimeType = upload.mimeType
    values.tempFileExtension = upload.extension
  }

  const response = await saveContact(token, tenantId, values)

  return {
    values,
    error: null,
    savedContactId: response.id
  }
}
