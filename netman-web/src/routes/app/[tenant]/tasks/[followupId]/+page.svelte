<script lang="ts">
  import type { PageProps } from "./$types"
  
  let { data }: PageProps = $props()
  let { tenant, followupId } = data
</script>

<div class="navbar shadow-sm">
  <div class="flex-1">
    <div class="text-2xl">Follow-up Details</div>
  </div>
</div>

<div class="p-4">
  <div class="card bg-base-100 border shadow-sm">
    <div class="card-body">
      <h2 class="card-title">Follow-up ID: {followupId}</h2>
      <p class="text-gray-600">This is a placeholder page for follow-up details.</p>
      <p class="mt-4">Tenant: {tenant}</p>
      
      <div class="mt-6">
        <a href="/app/{tenant}/tasks" class="btn btn-outline btn-primary">
          Back to Follow-up Hub
        </a>
      </div>
    </div>
  </div>
</div>