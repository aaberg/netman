import type { Actions, PageServerLoad } from "./$types"
import { redirect } from "@sveltejs/kit"
import type { CreateFollowUpTaskRequest } from "$lib/taskModel"
import { createTask } from "$lib/server/task"
import { accessToken } from "$lib/server/common"
import { getContactsForTenant } from "$lib/server/contact"

export const load: PageServerLoad = async ({ cookies, params }) => {
  const { tenant } = params
  const contacts = await getContactsForTenant(accessToken(cookies), tenant)
  return { contacts }
}

export const actions = {
  default: async ({ request, cookies, params }) => {
    const { tenant } = params
    const form = await request.formData()
    const taskData = form.get("task") as string
    const task = JSON.parse(taskData) as CreateFollowUpTaskRequest

    await createTask(accessToken(cookies), tenant, task)
    return redirect(303, `/app/${tenant}/tasks`)
  }
} satisfies Actions
