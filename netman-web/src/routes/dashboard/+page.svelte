<script lang="ts">
  import { logout, validateSession } from "$lib/security"

  validateSession()
</script>

<h1>Dashboard</h1>
<button class="btn btn-primary" on:click={() => logout()}> Logout</button>
