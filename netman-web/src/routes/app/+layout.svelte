<script lang="ts">
    import "../../app.css"
    import {fetchUserData, logout, validateSession} from "$lib/security"
    import {onMount} from "svelte";
    import {getProfile, type UserProfile} from "$lib/getProfile";

    let { children } = $props()
    let name = $state("")
    let initials = $state("")
    onMount(async () => {
        validateSession()

        const profile = await getProfile()
        name = profile.name
        initials = profile.initials
    })

</script>

<div class="navbar bg-base-100 shadow-sm">
    <div class="flex-1">
        <label for="my-drawer" class="btn btn-ghost drawer-button lg:hidden">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" class="inline-block h-5 w-5 stroke-current"> <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path> </svg>
        </label>
    </div>
    <div class="flex-1">
        <div>Netman</div>
    </div>
</div>

<div class="drawer lg:drawer-open">
    <input id="my-drawer" type="checkbox" class="drawer-toggle" />
    <div class="drawer-content flex flex-col items-center p-4">
        {@render children()}
    </div>
    <div class="drawer-side">
        <label for="my-drawer" aria-label="close sidebar" class="drawer-overlay"></label>
        <div class="min-h-full w-80 p-4 bg-base-200" >
            <ul class="menu text-base-content">
                <li><a href="/app/dashboard">Dashboard</a></li>
                <li><a href="/app/contactlist">Contact list</a></li>
                <li><a href="/app/settings">Settings</a></li>
            </ul>
            <div class="divider"></div>
            <div class="pl-4">
                <div class="avatar avatar-placeholder">
                    <div class="bg-neutral text-neutral-content w-8 rounded-full">
                        <span class="text-xs">{initials}</span>
                    </div>
                </div>
                {name}
            </div>

            <ul class="menu menu-horizontal text-base-content">
                <li><a href="/app/profile">Profile</a></li>
                <li><button onclick={() => logout()}>Logout</button></li>
            </ul>
        </div>
    </div>
</div>