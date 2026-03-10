import type { PageServerLoad, Actions } from "./$types"
import { updateCommunication, getCommunications } from "$lib/server/communication"
import { accessToken } from "$lib/server/common"
import { fail, redirect } from "@sveltejs/kit"

export const load: PageServerLoad = async ({ cookies, params }) => {
  const { tenant, contact, communication: communicationId } = params
  
  const communications = await getCommunications(accessToken(cookies), tenant, contact)
  const communication = communications.find(comm => comm.id === communicationId)
  
  if (!communication) {
    throw redirect(303, `/app/${tenant}/contacts/${contact}`)
  }
  
  return { communication }
}

export const actions: Actions = {
  default: async ({ request, cookies, params }) => {
    const { tenant, contact, communication: communicationId } = params
    const formData = await request.formData()
    
    const communicationType = formData.get("communicationType") as string
    const content = formData.get("content") as string
    const subject = formData.get("subject") as string
    const conversationLength = formData.get("conversationLength") as string
    
    // Validate required fields
    if (!communicationType || !content) {
      return fail(400, {
        error: "Communication type and content are required"
      })
    }
    
    // Build metadata
    const metadata: Record<string, string> = {}
    if (communicationType === "EMAIL" && subject) {
      metadata.subject = subject
    } else if (communicationType === "CALL" && conversationLength) {
      metadata.conversationLength = conversationLength
    }
    
    try {
      await updateCommunication(
        accessToken(cookies),
        tenant,
        contact,
        communicationId,
        {
          type: communicationType as "EMAIL" | "CALL" | "TEXT_MESSAGE",
          content: content,
          timestamp: new Date().toISOString(),
          metadata: metadata
        }
      )
      
      // Redirect back to contact page
      throw redirect(303, `/app/${tenant}/contacts/${contact}`)
    } catch (error) {
      console.error("Error updating communication:", error)
      return fail(500, {
        error: "Failed to update communication. Please try again."
      })
    }
  }
}