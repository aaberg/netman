<script lang="ts">
  import CommunicationForm from "../../communications/CommunicationForm.svelte"
  import type { PageProps } from "./$types"
  
  let { data }: PageProps = $props()
  let { tenant, contact } = data
</script>

<svelte:head>
  <title>Register Communication</title>
</svelte:head>

<CommunicationForm
  contactId={contact.id}
  tenant={tenant}
  formAction={"/app/" + tenant + "/contacts/" + contact.id + "/communications/new"}
/>