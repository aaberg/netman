import type {ContactWithDetails} from "$lib/contactModel";
import {basePath} from "$lib/server/common";

export const saveContactOnTenant = async (accessToken: String, tenantId: string, contact: ContactWithDetails) : Promise<ContactWithDetails> => {
    const body = JSON.stringify(contact)
    const response = await fetch(`${basePath()}/api/tenants/${tenantId}/contacts`, {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${accessToken}`,
            "Content-Type": "application/json"
        },
        body
    })
    if (!response.ok) {
        throw new Error(`Error saving profile. body of request: ${body}`)
    }

    const responseJson = await response.json();


    return responseJson as ContactWithDetails
}