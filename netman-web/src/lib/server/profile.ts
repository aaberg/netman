import type {UserProfile} from "$lib/profile";
import {basePath} from "./common";

export const getProfile = async (accessToken : String) : Promise<UserProfile | null> => {
    const response = await fetch(`${basePath()}/api/membership/profile`, {
        method: "GET",
        headers: [
            ["Authorization", `Bearer ${accessToken}`]
        ]
    })

    if (response.status == 404) {
        return null;
    }

    if (!response.ok) {
        throw new Error("error fetching profile")
    }
    return await response.json() as UserProfile;
}