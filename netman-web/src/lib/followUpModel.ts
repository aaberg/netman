import type {ContactWithDetails} from "$lib/contactModel";


export interface RegisterFollowUpRequest {
  contactId: string
  note: string
  triggerTime: string
  frequency: "Single" | "Weekly" | "Biweekly" | "Monthly" | "Quarterly" | "SemiAnnually" | "Annually"
}

export interface FollowUpActionResource {
  id: string,
  tenantId: string,
  status: "Pending" | "Completed",
  created: string,
  triggerTime: string,
  frequency: "Single" | "Weekly" | "Biweekly" | "Monthly" | "Quarterly" | "SemiAnnually" | "Annually",
  contact: ContactWithDetails,
  note: string
}