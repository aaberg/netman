<script lang="ts">
  import type { CommunicationType, RegisterCommunicationResource, CommunicationResource } from "$lib/communicationModel"
  import { goto } from "$app/navigation"
  
  export let communication: CommunicationResource | RegisterCommunicationResource | null = null
  export let contactId: string | null = null
  export let tenant: string
  export let formAction: string
  
  let communicationType: CommunicationType = communication?.type || "EMAIL"
  let content: string = communication?.content || ""
  let subject: string = communication?.metadata?.subject || ""
  let conversationLength: string = communication?.metadata?.conversationLength || ""
  
  function handleSubmit() {
    const metadata: Record<string, string> = {}
    
    if (communicationType === "EMAIL" && subject) {
      metadata.subject = subject
    } else if (communicationType === "CALL" && conversationLength) {
      metadata.conversationLength = conversationLength
    }
    
    const communicationData: RegisterCommunicationResource = {
      type: communicationType,
      content: content,
      timestamp: new Date().toISOString(),
      metadata: metadata
    }
    
    // Submit to form action
    // This will be handled by the +page.server.ts
  }
</script>

<div class="container mx-auto max-w-2xl p-4 md:p-6">
  <!-- Back button -->
  <div class="mb-4">
    <a href={`/app/${tenant}/contacts/${contactId || ''}`} class="btn btn-ghost gap-2" aria-label="Back to contact">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="h-5 w-5">
        <path d="M10.5 19.5 3 12l7.5-7.5 1.06 1.06L5.62 11H21v1.5H5.62l5.94 5.94-1.06 1.06Z" />
      </svg>
      <span class="hidden sm:inline">Back to contact</span>
      <span class="sm:hidden">Back</span>
    </a>
  </div>

  <!-- Form card -->
  <div class="card bg-base-300 border-base-200 border shadow-xl">
    <div class="card-body">
      <h2 class="card-title text-xl font-bold">
        {#if communication}
          Edit Communication
        {:else}
          Register New Communication
        {/if}
      </h2>

      <form method="POST" action={formAction} class="space-y-4">
        <!-- Communication Type -->
        <div class="form-control">
          <label class="label">
            <span class="label-text font-medium">Communication Type</span>
          </label>
          <div class="join">
            <input
              type="radio"
              name="communicationType"
              value="EMAIL"
              bind:group={communicationType}
              class="join-item btn"
              aria-label="Email"
              checked={communicationType === "EMAIL"}
            />
            <input
              type="radio"
              name="communicationType"
              value="CALL"
              bind:group={communicationType}
              class="join-item btn"
              aria-label="Call"
              checked={communicationType === "CALL"}
            />
            <input
              type="radio"
              name="communicationType"
              value="TEXT_MESSAGE"
              bind:group={communicationType}
              class="join-item btn"
              aria-label="Text Message"
              checked={communicationType === "TEXT_MESSAGE"}
            />
          </div>
        </div>

        <!-- Content -->
        <div class="form-control">
          <label class="label">
            <span class="label-text font-medium">Follow-up Details</span>
          </label>
          <textarea
            name="content"
            bind:value={content}
            class="textarea textarea-bordered h-32"
            placeholder="Describe what was discussed or followed up..."
            required
          ></textarea>
        </div>

        <!-- Conditional Metadata Fields -->
        {#if communicationType === "EMAIL"}
          <div class="form-control">
            <label class="label">
              <span class="label-text font-medium">Email Subject</span>
            </label>
            <input
              type="text"
              name="subject"
              bind:value={subject}
              class="input input-bordered"
              placeholder="Email subject..."
            />
          </div>
        {:else if communicationType === "CALL"}
          <div class="form-control">
            <label class="label">
              <span class="label-text font-medium">Conversation Length (minutes)</span>
            </label>
            <input
              type="number"
              name="conversationLength"
              bind:value={conversationLength}
              class="input input-bordered"
              placeholder="Duration in minutes..."
              min="1"
            />
          </div>
        {/if}

        <!-- Form actions -->
        <div class="flex justify-end gap-2 mt-6">
          <button type="button" class="btn btn-ghost" onclick={() => goto(`/app/${tenant}/contacts/${contactId || ''}`)}>
            Cancel
          </button>
          <button type="submit" class="btn btn-primary">
            {#if communication}
              Update Communication
            {:else}
              Register Communication
            {/if}
          </button>
        </div>
      </form>
    </div>
  </div>
</div>