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
