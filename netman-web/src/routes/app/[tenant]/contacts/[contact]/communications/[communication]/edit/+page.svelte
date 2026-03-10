<script lang="ts">
  import CommunicationForm from "../../CommunicationForm.svelte"
  import type { PageProps } from "./$types"
  
  let { data }: PageProps = $props()
  let { tenant, contact, communication } = data
</script>

<svelte:head>
  <title>Edit Communication</title>
</svelte:head>

<CommunicationForm
  communication={communication}
  contactId={contact.id}
  tenant={tenant}
  formAction={"/app/" + tenant + "/contacts/" + contact.id + "/communications/" + communication.id + "/edit"}
/>