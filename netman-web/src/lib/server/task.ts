import type { CreateFollowUpTaskRequest, TaskResource } from "$lib/taskModel"
import { basePath } from "$lib/server/common"

export const getTasks = async (accessToken: string, tenantId: string): Promise<TaskResource[]> => {
  const response = await fetch(`${basePath()}/api/tenants/${tenantId}/tasks`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${accessToken}`
    }
  })

  if (!response.ok) {
    throw new Error(
      `Error fetching tasks for tenant ${tenantId}. API responded with ${response.status} ${response.statusText}`
    )
  }

  return (await response.json()) as TaskResource[]
}

export const createTask = async (
  accessToken: string,
  tenantId: string,
  request: CreateFollowUpTaskRequest
): Promise<TaskResource> => {
  const body = JSON.stringify(request)
  const response = await fetch(`${basePath()}/api/tenants/${tenantId}/tasks`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${accessToken}`,
      "Content-Type": "application/json"
    },
    body
  })

  if (!response.ok) {
    throw new Error(`Error creating task. body of request: ${body}`)
  }

  const responseJson = await response.json()
  return responseJson as TaskResource
}
