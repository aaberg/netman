<script lang="ts">
  import { env } from "$env/dynamic/public"
  import { register } from "@teamhanko/hanko-elements"
  import { onMount } from "svelte"

  const hankoApiUrl = env.PUBLIC_HANKO_API_URL || ""

  onMount(async () => {
    register(hankoApiUrl).catch((error) => {
      // TODO handle error
    })
  })
</script>

<hanko-profile></hanko-profile>
