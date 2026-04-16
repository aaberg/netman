import { contactToFormValues } from "$lib/contactForm"
import { getContactsById } from "$lib/server/contact"
import { accessToken } from "$lib/server/common"
import {
  CONTACT_SAVE_ERROR_MESSAGE,
  defaultContactFormState,
  parseContactFormData,
  submitContactForm
} from "$lib/server/contactForm"
import { fail, redirect } from "@sveltejs/kit"
import type { Actions, PageServerLoad } from "./$types"

export const load: PageServerLoad = async ({ cookies, params }) => {
  const contact = await getContactsById(accessToken(cookies), params.tenant, params.contactId)

  return {
    contact,
    formState: defaultContactFormState(contactToFormValues(contact)),
    imageUrl: contact.imageUrl
  }
}

export const actions = {
  default: async ({ request, cookies, params }) => {
    const data = await request.formData()

    try {
      const result = await submitContactForm(data, cookies, params.tenant, params.contactId)

      if (result.error) {
        return fail(400, result)
      }
    } catch (error) {
      console.error("Failed to save contact", error)
      return fail(500, {
        values: parseContactFormData(data, params.contactId),
        error: CONTACT_SAVE_ERROR_MESSAGE
      })
    }

    throw redirect(303, `/app/${params.tenant}/contacts`)
  }
} satisfies Actions
