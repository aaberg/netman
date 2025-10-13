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
        throw new Error(`error fetching profile. API responded with status ${response.status} - ${response.statusText}`)
    }
    return await response.json() as UserProfile;
}

export const createProfile = async (accessToken : String, profile : UserProfile) : Promise<void> => {
    const response = await fetch(`${basePath()}/api/membership/profile`, {
        method: "PUT",
        headers: {
            "Authorization": `Bearer ${accessToken}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(profile)
    })

    if (!response.ok) {
        throw new Error(`error creating profile. API responded with status ${response.status} - ${response.statusText}`)
    }
}