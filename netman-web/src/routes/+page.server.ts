import { env } from '$env/dynamic/public'
import type { PageServerLoad } from '../../.svelte-kit/types/src/routes/$types';

export const load: PageServerLoad = () => {
    return {
        authUrl: env.PUBLIC_HANKO_API_URL
    }
}