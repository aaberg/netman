<script lang="ts">
  import { enhance } from "$app/forms"
  import type { PageProps } from "./$types"
  import EditContact from "../../../../../../components/contact/EditContact.svelte"

  let { data }: PageProps = $props()
  let { tenant, contact } = data

  let contactState = $state(structuredClone(contact))

  let serializedContact = $derived(JSON.stringify(contactState))
  let isSubmitting = $state(false)
</script>

<h1>Edit Contact</h1>
<EditContact contact={contactState} />

<form
  class="mt-4 flex w-full max-w-lg gap-2"
  method="post"
  use:enhance={() => {
    isSubmitting = true
    return async ({ update }) => {
      await update()
      isSubmitting = false
    }
  }}
>
  <input type="hidden" name="contact" value={serializedContact} />
  <div class="grow">
    <button class="btn btn-primary w-full" type="submit" disabled={isSubmitting}>
      {#if isSubmitting}
        <span class="loading loading-spinner"></span>
        Saving...
      {:else}
        Save
      {/if}
    </button>
  </div>
  <div class="grow">
    <a class="btn btn-neutral w-full" href="/app/{tenant}/contacts/{contact.id}">Cancel</a>
  </div>
</form>
