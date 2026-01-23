<script lang="ts">
  import { enhance } from "$app/forms"
  import type { CreateFollowUpTaskRequest } from "$lib/taskModel"
  import type { PageProps } from "./$types"

  let { data }: PageProps = $props()
  let { tenant, contacts } = data

  let selectedContactId = $state("")
  let note = $state("")
  let isSubmitting = $state(false)

  let taskRequest: CreateFollowUpTaskRequest = $derived({
    data: {
      type: "followup",
      contactId: selectedContactId,
      note: note
    },
    status: "Pending"
  })

  let serializedTask = $derived(JSON.stringify(taskRequest))
  let isValid = $derived(selectedContactId !== "" && note.trim() !== "")
</script>

<h1 class="text-3xl">Add follow-up task</h1>

<div class="mt-4 w-full max-w-lg">
  <div class="form-control">
    <label class="label" for="contact">
      <span class="label-text">Contact</span>
    </label>
    <select id="contact" class="select select-bordered w-full" bind:value={selectedContactId}>
      <option value="" disabled>Select a contact</option>
      {#each contacts as contact (contact.id)}
        <option value={contact.id}>{contact.name}</option>
      {/each}
    </select>
  </div>

  <div class="form-control mt-4">
    <label class="label" for="note">
      <span class="label-text">Note</span>
    </label>
    <textarea
      id="note"
      class="textarea textarea-bordered h-24"
      placeholder="Enter follow-up notes..."
      bind:value={note}
    ></textarea>
  </div>

  <form
    class="mt-6 flex w-full gap-2"
    method="post"
    use:enhance={() => {
      isSubmitting = true
      return async ({ update }) => {
        await update()
        isSubmitting = false
      }
    }}
  >
    <input type="hidden" name="task" value={serializedTask} />
    <div class="grow">
      <button class="btn btn-primary w-full" type="submit" disabled={!isValid || isSubmitting}>
        {#if isSubmitting}
          <span class="loading loading-spinner"></span>
          Creating...
        {:else}
          Create Task
        {/if}
      </button>
    </div>
    <div class="grow">
      <a class="btn btn-neutral w-full" href="/app/{tenant}/tasks">Cancel</a>
    </div>
  </form>
</div>
