export interface MemberTenant {
  tenant: Tenant
  role: string
}

export interface Tenant {
  id: bigint
  name: string
  tenantType: string
}
