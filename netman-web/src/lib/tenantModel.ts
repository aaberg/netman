export interface MemberTenant {
  tenant: Tenant
  role: string
}

export interface Tenant {
  id: bigint
  name: string
  tenantType: string
}

export interface TenantSummary {
  tenantId: bigint
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
