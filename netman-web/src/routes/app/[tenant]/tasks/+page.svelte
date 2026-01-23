<script lang="ts">
  import type { PageProps } from "./$types"

  let { data }: PageProps = $props()
  let { tenant } = data
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

<!-- Tasks list -->
<div class="mt-4 w-full max-w-4xl">
  {#if data.tasks.length === 0}
    <h2 class="text-center text-xl">No tasks yet</h2>
    <div class="text-base-content/60 pt-8 text-center">
      Create some follow-up tasks to get started :)
    </div>
  {:else}
    <table class="table w-full">
      <thead>
        <tr>
          <th>Status</th>
          <th>Note</th>
          <th>Contact ID</th>
          <th>Trigger Time</th>
        </tr>
      </thead>
      <tbody>
        {#each data.tasks as task (task.id)}
          <tr>
            <td>
              <span
                class="badge"
                class:badge-warning={task.status === "Pending"}
                class:badge-error={task.status === "Due"}
                class:badge-success={task.status === "Completed"}
              >
                {task.status}
              </span>
            </td>
            <td>{task.data.note}</td>
            <td>
              <a
                href="/app/{tenant}/contacts/{task.data.contactId}"
                class="link link-hover text-sm"
              >
                {task.data.contactId}
              </a>
            </td>
            <td class="text-sm">
              {#if task.triggers && task.triggers.length > 0}
                {new Date(task.triggers[0].triggerTime).toLocaleString()}
              {/if}
            </td>
          </tr>
        {/each}
      </tbody>
    </table>
  {/if}
</div>
