import type { Actions } from "./$types"
import { registerCommunication } from "$lib/server/communication"
import { accessToken } from "$lib/server/common"
import { fail, redirect } from "@sveltejs/kit"

export const actions: Actions = {
  default: async ({ request, cookies, params }) => {
    const { tenant, contact } = params
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
      await registerCommunication(
        accessToken(cookies),
        tenant,
        contact,
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
      console.error("Error registering communication:", error)
      return fail(500, {
        error: "Failed to register communication. Please try again."
      })
    }
  }
}