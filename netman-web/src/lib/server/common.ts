import { env } from "$env/dynamic/private"
import type {Cookies} from "@sveltejs/kit";

export const basePath = () : string => {
    return env.NETMAN_API_URL
}

export const accessToken = (cookies: Cookies) : string => cookies.get("hanko")!