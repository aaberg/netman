import type { PageServerLoad } from "./$types"

export const load: PageServerLoad = async ({ params }) => {
  const { tenant, followupId } = params
  
  return { tenant, followupId }
}