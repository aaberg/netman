<script lang="ts">
  import { onMount } from "svelte"
  import { env } from "$env/dynamic/public"
  import { register } from "@teamhanko/hanko-elements"
  import { goto } from "$app/navigation"

  const hankoApiUrl = env.PUBLIC_HANKO_API_URL || ""

  const redirectAfterLogin = () => {
    goto("/flow/authorization/continue")
  }

  onMount(async () => {
    register(hankoApiUrl).catch((error) => {
      // handle error TODO
    })
  })
</script>

<hanko-auth on:onSessionCreated={redirectAfterLogin}></hanko-auth>
