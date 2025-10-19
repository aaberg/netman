import type {PageServerLoad} from "./$types";
import {accessToken, basePath} from "$lib/server/common";
import type {ContactWithDetails} from "$lib/contactModel";

export const load: PageServerLoad = async ({cookies, params}) =>  {
    const {tenant, contact} = params

    const response = await fetch(`${basePath()}/api/tenants/${tenant}/contacts/${contact}`, {
        method: "GET",
        headers: [
            ["Authorization", `Bearer ${ accessToken(cookies)} `]
        ]
    })

    if (!response.ok) {
        throw new Error("error fetching contact, status: " + response.status + " - " + response.statusText + "")
    }
    const contactWDetails = await response.json() as ContactWithDetails
    return {
        contactWDetails
    }
}