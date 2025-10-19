<script lang="ts">
    import type {PageProps} from "./$types";
    import EditContact from "../../../../../../components/contact/EditContact.svelte";

    const { data }: PageProps = $props()
    const { tenant, contactWDetails } = data
</script>

<h1>Edit Contact</h1>
<EditContact {tenant} contact={contactWDetails} />