<script lang="ts">
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
</script>

<h1 class="text-3xl">New contact</h1>

<EditContact {tenant} {contact} />


