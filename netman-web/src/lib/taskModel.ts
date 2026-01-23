export type TaskStatus = "Pending" | "Completed" | "Canceled" | "Due"

export type TriggerStatus = "Pending" | "Triggered" | "Canceled"

export interface FollowUpTask {
  type: "followup"
  contactId: string
  note: string
}

export interface TaskResource {
  id?: string
  tenantId: number
  data: FollowUpTask
  status: TaskStatus
  created?: string
  triggers: TriggerResource[]
}

export interface TriggerResource {
  id?: string
  triggerType: string
  triggerTime: string
  targetTaskId?: string
  status: TriggerStatus
  statusTime?: string
}

export interface CreateFollowUpTaskRequest {
  data: FollowUpTask
  status?: TaskStatus
  trigger?: CreateTriggerRequest
}

export interface CreateTriggerRequest {
  triggerType: string
  triggerTime: string
}
