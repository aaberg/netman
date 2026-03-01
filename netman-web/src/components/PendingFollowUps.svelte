<script lang="ts">
  import type { FollowUpResource } from '$lib/tenantModel'

  export let followUps: ReadonlyArray<FollowUpResource> = []
  function formatDate(isoString: string): string {
    return new Date(isoString).toLocaleDateString()
  }

  function formatTime(isoString: string): string {
    return new Date(isoString).toLocaleTimeString()
  }
</script>

{#if followUps.length > 0}
  <div class="card bg-base-100 mb-8 border shadow-sm">
    <div class="card-body">
      <h2 class="card-title mb-4 text-lg">Pending Follow-ups</h2>
      <div class="overflow-x-auto">
        <table class="table-compact table w-full">
          <thead>
            <tr>
              <th>Created</th>
              <th>Contact</th>
              <th>Note</th>
            </tr>
          </thead>
          <tbody>
            {#each followUps as followUp (followUp.id)}
              <tr>
                <td>
                  <div class="text-sm">{formatDate(followUp.created)}</div>
                  <div class="text-xs text-gray-500">{formatTime(followUp.created)}</div>
                </td>
                <td class="font-mono text-sm">{followUp.contactName}</td>
                <td>
                  {#if followUp.note}
                    <div class="tooltip" data-tip={followUp.note}>
                      <span class="inline-block max-w-xs truncate">{followUp.note}</span>
                    </div>
                  {:else}
                    <span class="text-gray-400">No note</span>
                  {/if}
                </td>
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
    </div>
  </div>
{:else}
  <div role="alert" class="alert alert-success alert-soft mb-8">
    <svg
      xmlns="http://www.w3.org/2000/svg"
      class="h-6 w-6 shrink-0 stroke-current"
      fill="none"
      viewBox="0 0 24 24"
    >
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
        d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
      />
    </svg>
    <span>No pending follow-ups found. All caught up!</span>
  </div>
{/if}
