<script lang="ts">
  import { isAuthenticated } from "$lib/security"

  const authenticated = isAuthenticated()
</script>

<div class="hero bg-base-200 min-h-screen">
  <div class="hero-content text-center">
    <div class="max-w-lg">
      <h1 class="text-5xl font-bold">Welcome to netman!</h1>
      <p class="py-6">The personal network manager that doesn't suck</p>
      <a class="btn btn-primary" href="/app/dashboard">
        {#await authenticated}
          Login
        {:then value}
          {value ? "Go to dashboard" : "Login"}
        {/await}
      </a>
    </div>
  </div>
</div>
