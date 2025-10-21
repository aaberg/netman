<script lang="ts">
    import { enhance } from '$app/forms';
    import type {ContactWithDetails} from "$lib/contactModel";
    import type {PageProps} from "./$types";
    import EditContact from "../../../../../components/contact/EditContact.svelte";

    let { data } : PageProps = $props()
    let { tenant } = data

    let contact: ContactWithDetails = $state({
        contact: {
            id: null,
            name: "",
            initials: "",
        },
        details: []
    })

    let serializedContact = $derived(JSON.stringify(contact))
</script>

<h1 class="text-3xl">New contact</h1>

<EditContact {contact} />

<form class="flex mt-4 w-full max-w-lg gap-2" method="post" use:enhance>
    <input type="hidden" name="contact" value={serializedContact} />
    <div class="grow"><button class="btn btn-primary w-full" type="submit" >Save</button></div>
    <div class="grow"><a class="btn btn-neutral w-full" href="/app/{tenant}/contacts">Cancel</a></div>
</form>


