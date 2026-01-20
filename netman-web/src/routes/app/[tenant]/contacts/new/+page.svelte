<script lang="ts">
  import { enhance } from "$app/forms"
  import type { ContactWithDetails } from "$lib/contactModel"
  import type { PageProps } from "./$types"
  import EditContact from "../../../../../components/contact/EditContact.svelte"

  let { data }: PageProps = $props()
  let { tenant, labels } = data

  let contact: ContactWithDetails = $state({
    id: null,
    initials: "",
    name: "",
    details: []
  })

  let serializedContact = $derived(JSON.stringify(contact))
  let isSubmitting = $state(false)

  let availableLabels = $derived(labels.map((l) => l.label))
</script>

<h1 class="text-3xl">New contact</h1>

<EditContact {contact} {availableLabels} />

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
  <div class="grow"><a class="btn btn-neutral w-full" href="/app/{tenant}/contacts">Cancel</a></div>
</form>
