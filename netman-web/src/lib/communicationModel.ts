export interface CommunicationResource {
  id: string | null
  contactId: string
  type: CommunicationType
  content: string
  timestamp: string
  metadata: Record<string, string>
}

export interface RegisterCommunicationResource {
  type: CommunicationType
  content: string
  timestamp: string
  metadata: Record<string, string>
}

export type CommunicationType = "EMAIL" | "CALL" | "TEXT_MESSAGE"
