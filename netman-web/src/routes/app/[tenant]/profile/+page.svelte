<script lang="ts">
  import HankoProfile from "../../../../components/HankoProfile.svelte"
  import type { PageProps } from "./$types"

  let { data }: PageProps = $props()
</script>

<h1 class="text-2xl">Profile</h1>
<div>
  <HankoProfile />
</div>

<div class="mt-8">
  <button class="btn btn-outline" onclick={() => navigator.clipboard.writeText(data.accessToken)}>
    <svg
      xmlns="http://www.w3.org/2000/svg"
      class="h-4 w-4"
      fill="none"
      viewBox="0 0 24 24"
      stroke="currentColor"
    >
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
        d="M8 5H6a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2v-1M8 5a2 2 0 002 2h2a2 2 0 002-2M8 5a2 2 0 012-2h2a2 2 0 012 2m0 0h2a2 2 0 012 2v3m2 4H10m0 0l3-3m-3 3l3 3"
      />
    </svg>
    Copy token
  </button>
</div>
