<script lang="ts">
  import {  validateSession } from "$lib/security"
  import {onMount} from "svelte";
  import type {PageProps} from "./$types";

  let { data }: PageProps = $props()

  onMount(() => {
      validateSession()
  })

</script>

<h1 class="text-2xl">Dashboard {data.tenant}</h1>
