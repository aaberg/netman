import {fetchUserData} from "$lib/security";

export interface UserProfile {
    name: string;
    initials: string;
}

export interface ApiError {
    status: number;
    message: string;
}

export async function createProfile(name: string, initials: string) {
    const user = await fetchUserData()

    const profile: UserProfile = {name, initials}
    const response = await fetch(`/api/membership/profile/${user.user_id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(profile)
    })

    if (!response.ok) {
        const error:ApiError = {
            status: response.status,
            message: response.statusText
        }
        throw new Error(error.message)
    }
}