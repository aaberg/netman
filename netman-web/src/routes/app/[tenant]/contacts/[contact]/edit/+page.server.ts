import type { Actions, PageServerLoad } from "./$types"
import type { ContactWithDetails } from "$lib/contactModel"
import { getLabelsForTenant, saveContact } from "$lib/server/contact"
import { accessToken } from "$lib/server/common"
import { redirect } from "@sveltejs/kit"

export const load: PageServerLoad = async ({ cookies, params }) => {
  const { tenant } = params
  const labels = await getLabelsForTenant(accessToken(cookies), tenant)
  return { labels }
}

export const actions = {
  default: async ({ request, cookies, params }) => {
    const { tenant } = params
    const form = await request.formData()
    const contactData = form.get("contact") as string
    const contact = JSON.parse(contactData) as ContactWithDetails

    if (contact.id == undefined) {
      throw new Error("contact can't be updated, it doesn't exist")
    }

    await saveContact(accessToken(cookies), tenant, contact)
    return redirect(303, `/app/${tenant}/contacts/${contact.id}`)
  }
} satisfies Actions
