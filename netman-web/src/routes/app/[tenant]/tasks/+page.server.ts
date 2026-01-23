import type { PageServerLoad } from "./$types"
import { accessToken } from "$lib/server/common"
import { getTasks } from "$lib/server/task"

export const load: PageServerLoad = async ({ cookies, params }) => {
  const { tenant } = params
  const token = accessToken(cookies)

  const tasks = await getTasks(token, tenant)

  return { tasks }
}
