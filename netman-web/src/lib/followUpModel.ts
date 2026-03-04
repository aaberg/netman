import type { ContactWithDetails } from "$lib/contactModel"

export type TimeSpanType = "DAYS" | "WEEKS" | "MONTHS" | "YEARS"

export type FollowUpTimeSpecification =
  | {
      type: "Absolute"
      triggerTime: string
    }
  | {
      type: "Relative"
      span: number
      spanType: TimeSpanType
    }

export interface RegisterFollowUpRequest {
  contactId: string
  note: string
  timeSpecification: FollowUpTimeSpecification
  frequency:
    | "Single"
    | "Weekly"
    | "Biweekly"
    | "Monthly"
    | "Quarterly"
    | "SemiAnnually"
    | "Annually"
}

export interface FollowUpActionResource {
  id: string
  tenantId: string
  status: "Pending" | "Completed"
  created: string
  triggerTime: string
  frequency:
    | "Single"
    | "Weekly"
    | "Biweekly"
    | "Monthly"
    | "Quarterly"
    | "SemiAnnually"
    | "Annually"
  contact: ContactWithDetails
  note: string
}

export interface FollowUpResource {
  id: string
  tenantId: string
  status: "Pending" | "Completed"
  created: string
  contactName: string
  note: string
}

export type Frequency =
  | "Single"
  | "Weekly"
  | "Biweekly"
  | "Monthly"
  | "Quarterly"
  | "SemiAnnually"
  | "Annually"

export interface CreateFollowUpCommand {
  contactId: string
  note: string
}

export type Command = CreateFollowUpCommand

export interface ActionResource {
  id: string
  tenantId: string
  type: string
  status: "Pending" | "Completed"
  created: string
  triggerTime: string
  frequency: Frequency
  command: Command
}
