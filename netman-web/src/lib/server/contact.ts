import type { Contact, ContactWithDetails } from "$lib/contactModel";
import { basePath } from "$lib/server/common";

export const saveContactOnTenant = async (
    accessToken: string,
    tenantId: string,
    contact: ContactWithDetails
): Promise<ContactWithDetails> => {
    const body = JSON.stringify(contact);
    const response = await fetch(`${basePath()}/api/tenants/${tenantId}/contacts`, {
        method: "POST",
        headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json"
        },
        body
    });
    if (!response.ok) {
        throw new Error(`Error saving profile. body of request: ${body}`);
    }

    const responseJson = await response.json();

    return responseJson as ContactWithDetails;
};

export const getContactsForTenant = async (
    accessToken: string,
    tenantId: string
): Promise<Contact[]> => {
    const response = await fetch(`${basePath()}/api/tenants/${tenantId}/contacts`, {
        method: "GET",
        headers: {
            Authorization: `Bearer ${accessToken}`
        }
    });

    if (!response.ok) {
        throw new Error(
            `Error fetching contacts for tenant ${tenantId}. API responded with ${response.status} ${response.statusText}`
        );
    }

    return (await response.json()) as Contact[];
};