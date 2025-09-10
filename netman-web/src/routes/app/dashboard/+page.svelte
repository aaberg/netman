<script lang="ts">
  import { logout, validateSession } from "$lib/security"
  import {onMount} from "svelte";

  onMount(() => {
      validateSession()
  })

</script>

<h1 class="text-2xl">Dashboard</h1>
