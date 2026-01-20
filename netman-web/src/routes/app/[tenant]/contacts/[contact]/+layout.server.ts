import type { LayoutServerLoad } from "./$types"
import { accessToken, basePath } from "$lib/server/common"
import { compareDetails, type ContactWithDetails } from "$lib/contactModel"
import {getContactsById} from "$lib/server/contact";

export const load: LayoutServerLoad = async ({ cookies, params }) => {
  const { tenant, contact } = params

  const contactObj = await getContactsById(accessToken(cookies), tenant, contact)

  return {
    contact: contactObj
  }
}
