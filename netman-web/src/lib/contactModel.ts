export interface InteractionResource {
  id: string | null
  contactId: string
  type: "EMAIL" | "CALL" | "TEXT_MESSAGE"
  content: string
  timestamp: string
  metadata: Record<string, string>
}

export interface ContactWithDetails {
  id: string
  name: string
  initials: string | null
  email: string | null
  phone: string | null
  title: string | null
  organization: string | null
  notes: string | null
  interactions: InteractionResource[]
  imageUrl: string | null
  location: string | null
}

export interface ContactListItem {
  id: string
  name: string
  initials: string
  title: string
  organization: string
  followUpStatus: "Scheduled" | "Overdue" | "None"
  followUpIn: string
  imageUrl: string | null
  location: string | null
}

export interface SaveContactRequest {
  id: string | null
  name: string
  email: string | null
  phone: string | null
  title: string | null
  organization: string | null
  location: string | null
  notes: string | null
  tempFileId: string | null
  tempFileMimeType: string | null
  tempFileExtension: string | null
}

export interface ContactSavedResponse {
  id: string
}

export interface TemporaryImageUploadResponse {
  tempFileId: string
  mimeType: string
  extension: string
  previewUrl: string
  previewUrlExpiresAt: string
}
