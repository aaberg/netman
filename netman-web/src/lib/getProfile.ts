import {fetchUserData, getBearerToken} from "$lib/security";

export interface UserProfile {
    name: string;
    initials: string;
}

export interface ApiError {
    status: number;
    message: string;
}

export async function getProfile(): Promise<UserProfile> {

    const user = await fetchUserData()
    const bearerToken = getBearerToken()

    const response = await fetch(`/api/membership/profile/${user.user_id}`);

    if (!response.ok) {
        // const error:ApiError = {
        //     status: response.status,
        //     message: response.statusText
        // }
        throw new Error("er")
    }

    return await response.json() as UserProfile;
}