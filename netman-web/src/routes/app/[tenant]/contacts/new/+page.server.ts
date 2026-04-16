import {
  CONTACT_SAVE_ERROR_MESSAGE,
  defaultContactFormState,
  parseContactFormData,
  submitContactForm
} from "$lib/server/contactForm"
import { fail, redirect } from "@sveltejs/kit"
import type { Actions, PageServerLoad } from "./$types"

export const load: PageServerLoad = async () => {
  return {
    formState: defaultContactFormState(),
    imageUrl: null
  }
}

export const actions = {
  default: async ({ request, cookies, params }) => {
    const data = await request.formData()

    try {
      const result = await submitContactForm(data, cookies, params.tenant)

      if (result.error) {
        return fail(400, result)
      }
    } catch (error) {
      console.error("Failed to save contact", error)
      return fail(500, {
        values: parseContactFormData(data),
        error: CONTACT_SAVE_ERROR_MESSAGE
      })
    }

    throw redirect(303, `/app/${params.tenant}/contacts`)
  }
} satisfies Actions
