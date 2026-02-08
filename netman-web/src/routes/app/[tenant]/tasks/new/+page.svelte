<script lang="ts">
  import { enhance } from "$app/forms"
  import type { PageProps } from "./$types"
  import type { RegisterFollowUpRequest, TimeSpanType } from "$lib/followUpModel"

  let { data }: PageProps = $props()
  let { tenant, contacts } = data

  let selectedContactId = $state("")
  let note = $state("")
  let timeSpecMode = $state<"relative" | "absolute">("relative")
  let triggerTime = $state("")
  let relativeSpan = $state(7)
  let relativeSpanType = $state<TimeSpanType>("DAYS")
  let isSubmitting = $state(false)

  let followUpRequest: RegisterFollowUpRequest = $derived({
    contactId: selectedContactId,
    note: note,
    frequency: "Single",
    timeSpecification:
      timeSpecMode === "relative"
        ? {
            type: "Relative",
            span: relativeSpan,
            spanType: relativeSpanType
          }
        : {
            type: "Absolute",
            triggerTime: triggerTime !== "" ? new Date(triggerTime).toISOString() : ""
          }
  })

  let serializedTask = $derived(JSON.stringify(followUpRequest))
  let isValid = $derived(
    selectedContactId !== "" &&
      note.trim() !== "" &&
      (timeSpecMode === "relative" ? relativeSpan > 0 : triggerTime !== "")
  )
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

  <div class="form-control mt-4">
    <div class="label">
      <span class="label-text">Time Specification</span>
    </div>
    <div class="flex gap-4">
      <label class="label cursor-pointer gap-2">
        <input
          type="radio"
          name="timeSpecMode"
          class="radio"
          value="relative"
          bind:group={timeSpecMode}
        />
        <span class="label-text">Relative (from now)</span>
      </label>
      <label class="label cursor-pointer gap-2">
        <input
          type="radio"
          name="timeSpecMode"
          class="radio"
          value="absolute"
          bind:group={timeSpecMode}
        />
        <span class="label-text">Absolute (specific date/time)</span>
      </label>
    </div>
  </div>

  {#if timeSpecMode === "relative"}
    <div class="form-control mt-4">
      <label class="label" for="relativeSpan">
        <span class="label-text">Time from now</span>
      </label>
      <div class="flex gap-2">
        <input
          id="relativeSpan"
          type="number"
          min="1"
          max="999"
          class="input input-bordered w-32"
          bind:value={relativeSpan}
        />
        <select class="select select-bordered flex-1" bind:value={relativeSpanType}>
          <option value="DAYS">Days</option>
          <option value="WEEKS">Weeks</option>
          <option value="MONTHS">Months</option>
          <option value="YEARS">Years</option>
        </select>
      </div>
    </div>
  {:else}
    <div class="form-control mt-4">
      <label class="label" for="triggerTime">
        <span class="label-text">Trigger Time</span>
      </label>
      <input
        id="triggerTime"
        type="datetime-local"
        class="input input-bordered w-full"
        bind:value={triggerTime}
      />
    </div>
  {/if}

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
