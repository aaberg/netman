import type { Actions, PageServerLoad } from "./$types"
import { redirect } from "@sveltejs/kit"
import { accessToken } from "$lib/server/common"
import { getContactsForTenant } from "$lib/server/contact"
import type { RegisterFollowUpRequest } from "$lib/followUpModel"
import { registerFollowUp } from "$lib/server/followUp"

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
    const followup = JSON.parse(taskData) as RegisterFollowUpRequest

    await registerFollowUp(accessToken(cookies), tenant, followup)
    return redirect(303, `/app/${tenant}/tasks`)
  }
} satisfies Actions
