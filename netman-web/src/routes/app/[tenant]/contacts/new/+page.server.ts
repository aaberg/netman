import { defaultContactFormState, parseContactFormData, submitContactForm } from "$lib/server/contactForm"
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
      return fail(500, {
        values: parseContactFormData(data),
        error: error instanceof Error ? error.message : "Unable to save contact."
      })
    }

    throw redirect(303, `/app/${params.tenant}/contacts`)
  }
} satisfies Actions
