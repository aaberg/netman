<script lang="ts">
  import { validateSession } from "$lib/security"
  import { onMount } from "svelte"
  import type { PageProps } from "./$types"

  let { data }: PageProps = $props()
  const pendingFollowUps = $derived(() => data.summary?.pendingFollowUps ?? [])

  onMount(() => {
    validateSession()
  })

  function formatDate(isoString: string): string {
    return new Date(isoString).toLocaleDateString()
  }

  function formatTime(isoString: string): string {
    return new Date(isoString).toLocaleTimeString()
  }
</script>

<h1 class="mb-2 text-2xl font-bold">Dashboard</h1>
<h2 class="mb-6 text-xl text-gray-600">{data.profile.name}</h2>

{#if data.summary}
  <div class="mb-8 grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
    <!-- Contacts Card -->
    <div class="card bg-base-100 border shadow-sm">
      <div class="card-body">
        <h2 class="card-title text-lg">Contacts</h2>
        <div class="text-primary text-3xl font-bold">{data.summary.numberOfContacts}</div>
        <p class="text-sm text-gray-500">Total contacts in this tenant</p>
      </div>
    </div>

    <!-- Pending Actions Card -->
    <div class="card bg-base-100 border shadow-sm">
      <div class="card-body">
        <h2 class="card-title text-lg">Pending Actions</h2>
        <div class="text-secondary text-3xl font-bold">{data.summary.numberOfPendingActions}</div>
        <p class="text-sm text-gray-500">Follow-ups needing attention</p>
      </div>
    </div>

    <!-- Quick Stats Card -->
    <div class="card bg-base-100 border shadow-sm">
      <div class="card-body">
        <h2 class="card-title text-lg">Quick Stats</h2>
        <div class="text-xl font-semibold">{pendingFollowUps.length} pending</div>
        <p class="text-sm text-gray-500">Follow-ups shown below</p>
      </div>
    </div>
  </div>

  <!-- Pending Follow-ups Section -->
  {#if pendingFollowUps.length > 0}
    <div class="card bg-base-100 mb-8 border shadow-sm">
      <div class="card-body">
        <h2 class="card-title mb-4 text-lg">Pending Follow-ups</h2>
        <div class="overflow-x-auto">
          <table class="table-compact table w-full">
            <thead>
              <tr>
                <th>Created</th>
                <th>Note</th>
                <th>Contact ID</th>
                <th>Task ID</th>
              </tr>
            </thead>
            <tbody>
              {#each pendingFollowUps as followUp (followUp.id)}
                <tr>
                  <td>
                    <div class="text-sm">{formatDate(followUp.created)}</div>
                    <div class="text-xs text-gray-500">{formatTime(followUp.created)}</div>
                  </td>
                  <td>
                    {#if followUp.note}
                      <div class="tooltip" data-tip={followUp.note}>
                        <span class="inline-block max-w-xs truncate">{followUp.note}</span>
                      </div>
                    {:else}
                      <span class="text-gray-400">No note</span>
                    {/if}
                  </td>
                  <td class="font-mono text-sm">{followUp.contactId}</td>
                  <td class="font-mono text-sm">{followUp.taskId}</td>
                </tr>
              {/each}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  {:else}
    <div role="alert" class="alert alert-success alert-soft">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 shrink-0 stroke-current" fill="none" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <span>No pending follow-ups found. All caught up!</span>
    </div>
  {/if}
{:else}
  <div class="alert alert-warning shadow-sm">
    <svg
      xmlns="http://www.w3.org/2000/svg"
      fill="none"
      viewBox="0 0 24 24"
      class="h-6 w-6 shrink-0 stroke-current"
      ><path
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
        d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
      ></path></svg
    >
    <span>Loading tenant summary...</span>
  </div>
{/if}
