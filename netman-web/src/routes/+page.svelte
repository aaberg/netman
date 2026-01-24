<script lang="ts">
  import { isAuthenticated } from "$lib/security"
  import {onMount} from "svelte";

  let authenticated: Boolean = false;

  onMount(async () => {
    authenticated = await isAuthenticated()
  })

</script>

<div class="hero bg-base-200 min-h-screen">
  <div class="hero-content text-center">
    <div class="max-w-lg">
      <h1 class="text-5xl font-bold">Welcome to netman!</h1>
      <p class="py-6">The personal network manager that doesn't suck</p>
      <a class="btn btn-primary" href="/flow/authentication">
        {#if authenticated}
          Go to dashboard
        {:else}
          Login
        {/if}
      </a>
    </div>
  </div>
</div>
