import type {Actions} from "./$types";
import {redirect} from "@sveltejs/kit";
import type {ContactWithDetails} from "$lib/contactModel";
import {saveContactOnTenant} from "$lib/server/contact";
import {accessToken} from "$lib/server/common";

export const actions = {
    default: async  ({request, cookies, params} ) => {
        const { tenant } = params
        const form = await request.formData()
        const contactData = form.get('contact') as string
        const contact = JSON.parse(contactData) as ContactWithDetails

        await saveContactOnTenant(accessToken(cookies), tenant, contact)
        return redirect(303, `/app/${tenant}/contacts`)
    }
} satisfies Actions