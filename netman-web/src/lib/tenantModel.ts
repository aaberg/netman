export interface MemberTenant {
  tenant: Tenant
  role: string
}

export interface Tenant {
  id: string
  name: string
  tenantType: string
}

export interface TenantSummary {
  tenantId: string
  numberOfContacts: number
  numberOfPendingActions: number
  pendingFollowUps: FollowUpResource[]
}

export interface FollowUpResource {
  id: string
  contactId: string
  taskId: string
  note: string | null
  created: string
}
