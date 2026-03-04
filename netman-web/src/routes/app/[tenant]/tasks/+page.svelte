<script lang="ts">
  import type { PageProps } from "./$types"

  let { data }: PageProps = $props()
  let { tenant, followUpsPage, actionsPage } = data
  let followUps = followUpsPage.items ?? []
  let actions = actionsPage.items ?? []

  function formatDate(dateString: string): string {
    return new Date(dateString).toLocaleString()
  }

  function getStatusBadgeClasses(status: string): string {
    const baseClasses = "badge"
    if (status === "Pending") {
      return `${baseClasses} badge-warning`
    } else if (status === "Completed") {
      return `${baseClasses} badge-success`
    }
    return baseClasses
  }
</script>

<div class="navbar shadow-sm">
  <div class="flex-1">
    <div class="text-2xl">Follow-up hub</div>
  </div>

  <div class="flex gap-2">
    <a class="btn btn-neutral btn-sm" href="/app/{tenant}/tasks/new">
      <span>+</span>
      <span class="sr-only sm:not-sr-only">Add follow-up task</span>
    </a>
  </div>
</div>

<!-- Follow-ups Cards Grid -->
<div class="mt-4 w-full max-w-6xl">
  <div class="mb-4">
    <h2 class="text-xl font-semibold">Follow-ups</h2>
    <p class="text-sm text-gray-600 mt-1">These are your pending follow-ups</p>
  </div>
  
  {#if followUps.length === 0}
    <div class="text-center py-8">
      <h2 class="text-xl">No follow-ups found</h2>
      <div class="text-base-content/60 pt-4">
        Create some follow-up tasks to get started :)
      </div>
    </div>
  {:else}
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      {#each followUps as followUp (followUp.id)}
        <a href="/app/{tenant}/tasks/{followUp.id}" class="card bg-base-100 border border-base-300 hover:border-primary hover:shadow-lg transition-all duration-200 cursor-pointer">
          <div class="card-body">
            <div class="flex justify-between items-start mb-2">
              <h3 class="card-title text-lg">{followUp.contactName}</h3>
              <span class={getStatusBadgeClasses(followUp.status)}>
                {followUp.status}
              </span>
            </div>
            
            <p class="text-sm text-gray-600 mb-3">
              {#if followUp.note}
                {followUp.note}
              {:else}
                <span class="text-gray-400">No note</span>
              {/if}
            </p>
            
            <div class="text-xs text-gray-500 mt-auto">
              Created: {formatDate(followUp.created)}
            </div>
          </div>
        </a>
      {/each}
    </div>
  {/if}
</div>

<!-- Scheduled Actions Section -->
<div class="mt-8 w-full max-w-6xl">
  <div class="mb-4">
    <h2 class="text-xl font-semibold">Scheduled actions</h2>
    <p class="text-sm text-gray-600 mt-1">Upcoming automated actions and follow-up tasks</p>
  </div>
  
  {#if actions.length === 0}
    <div class="text-center py-4">
      <p class="text-gray-600">No scheduled actions found</p>
    </div>
  {:else}
    <div class="overflow-x-auto">
      <table class="table w-full">
        <thead>
          <tr>
            <th>Status</th>
            <th>Trigger Time</th>
            <th>Frequency</th>
            <th>Type</th>
            <th>Contact ID</th>
          </tr>
        </thead>
        <tbody>
          {#each actions as action (action.id)}
            <tr>
              <td>
                <span class={getStatusBadgeClasses(action.status)}>
                  {action.status}
                </span>
              </td>
              <td class="font-medium text-primary">{formatDate(action.triggerTime)}</td>
              <td class="font-medium text-secondary">{action.frequency}</td>
              <td class="font-medium">{action.type}</td>
              <td class="font-mono text-sm">{action.command.contactId}</td>
            </tr>
          {/each}
        </tbody>
      </table>
    </div>
  {/if}
</div>
