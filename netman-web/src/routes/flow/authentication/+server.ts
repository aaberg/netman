import type { RequestHandler } from './$types'
import {redirect} from "@sveltejs/kit";

export const GET: RequestHandler = async ({ url, cookies }) => {
    const token = cookies.get("hanko")
    if (token == undefined) {
        return redirect(303, "/auth/login")
    }

    return redirect(303, "continue")
}