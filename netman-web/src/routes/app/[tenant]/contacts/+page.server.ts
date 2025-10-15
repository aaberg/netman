import type { PageServerLoad } from "./$types";
import { accessToken } from "$lib/server/common";
import { getContactsForTenant } from "$lib/server/contact";

export const load: PageServerLoad = async ({ cookies, params }) => {
    const { tenant } = params;
    const token = accessToken(cookies);

    const contacts = await getContactsForTenant(token, tenant);

    return { contacts };
};
