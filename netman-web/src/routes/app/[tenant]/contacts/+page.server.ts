import type { PageServerLoad } from "./$types"
import { accessToken } from "$lib/server/common"
import {getContactList} from "$lib/server/contact"

export const load: PageServerLoad = async ({ cookies, params }) => {
  const { tenant } = params
  const token = accessToken(cookies)

  const contacts = await getContactList(token, tenant)

  return { contacts }
}
