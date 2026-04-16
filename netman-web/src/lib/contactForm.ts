import type { ContactWithDetails, SaveContactRequest } from "$lib/contactModel"

export const emptyContactForm = (): SaveContactRequest => ({
  id: null,
  name: "",
  email: null,
  phone: null,
  title: null,
  organization: null,
  location: null,
  notes: null,
  tempFileId: null,
  tempFileMimeType: null,
  tempFileExtension: null
})

export const contactToFormValues = (contact: ContactWithDetails): SaveContactRequest => ({
  id: contact.id,
  name: contact.name,
  email: contact.email,
  phone: contact.phone,
  title: contact.title,
  organization: contact.organization,
  location: contact.location,
  notes: contact.notes,
  tempFileId: null,
  tempFileMimeType: null,
  tempFileExtension: null
})

export const normalizeContactFormValue = (value: FormDataEntryValue | null): string | null => {
  if (typeof value !== "string") {
    return null
  }

  const trimmed = value.trim()
  return trimmed.length > 0 ? trimmed : null
}
