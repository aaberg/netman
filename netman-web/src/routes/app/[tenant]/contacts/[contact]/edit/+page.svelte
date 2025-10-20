<script lang="ts">
    import type {PageProps} from "./$types";
    import EditContact from "../../../../../../components/contact/EditContact.svelte";

    let { data }: PageProps = $props()
    let { tenant, contactWDetails } = data

    let contact = $state(structuredClone(contactWDetails))

    let serializedContact = $derived(JSON.stringify(contact))
</script>

<h1>Edit Contact</h1>
<EditContact contact={contact} />

<form class="flex mt-4 w-full max-w-lg gap-2" method="post" use:enhance>
    <input type="hidden" name="contact" value={serializedContact} />
    <div class="grow"><button class="btn btn-primary w-full" type="submit" >Save</button></div>
    <div class="grow"><a class="btn btn-neutral w-full" href="/app/{tenant}/contacts">Cancel</a></div>
</form>