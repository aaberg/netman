<script lang="ts">
  import type { PageProps } from "./$types"

  let { data }: PageProps = $props()
  let { tenant, followUpsPage } = data
  let followUps = followUpsPage.items ?? []
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
  {#if followUps.length === 0}
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
          <th>Contact</th>
          <th>Trigger Time</th>
        </tr>
      </thead>
      <tbody>
        {#each followUps as f (f.id)}
          <tr>
            <td>
              <span
                class="badge"
                class:badge-warning={f.status === "Pending"}
                class:badge-success={f.status === "Completed"}
              >
                {f.status}
              </span>
            </td>
            <td>{f.note}</td>
            <td>
              <a href="/app/{tenant}/contacts/{f.contact.id}" class="link link-hover text-sm">
                {f.contact.name}
              </a>
            </td>
            <td class="text-sm">
              {new Date(f.triggerTime).toLocaleString()}
            </td>
          </tr>
        {/each}
      </tbody>
    </table>
  {/if}
</div>
