<script lang="ts">
  import SearchInput from "../../../../components/SearchInput.svelte"
  import type { PageProps } from "./$types"
  import ContactInfoIcon from "../../../../components/ContactInfoIcon.svelte"

  let { data }: PageProps = $props()
  let { tenant } = data

  let search = $state("")

  const filtered = $derived(
    data.contacts.filter((c) => c?.name?.toLowerCase().includes(search.toLowerCase()))
  )
</script>

<div class="navbar shadow-sm">
  <div class="flex-1">
    <h1 class="text-2xl">Contact list</h1>
  </div>

  <div class="flex gap-2">
    <SearchInput bind:value={search} />
    <a class="btn btn-neutral btn-sm" href="contacts/new">
      <span>+</span>
      <span class="sr-only sm:not-sr-only">New contact</span>
    </a>
  </div>
</div>

<!-- Contacts list -->
<div class="mt-4 w-full max-w-2xl">
  {#if data.contacts.length === 0}
    <h2 class="text-center text-xl">Welcome!</h2>
    <div class="text-base-content/60 pt-8 text-center">
      Register a contact or two to get started :)
    </div>
  {:else if filtered.length === 0}
    <div class="text-base-content/60 pt-8 text-center">No contacts found</div>
  {:else}
    <table class="table w-full">
      <thead>
        <tr>
          <th>Name</th>
          <th>Contact Information</th>
        </tr>
      </thead>
      <tbody>
        {#each filtered as c}
          <tr>
            <td>
              <a href="/app/{tenant}/contacts/{c.id}" class="link link-hover">
                <div class="flex items-center gap-3">
                  <div class="avatar avatar-placeholder">
                    <div class="bg-neutral mask mask-squircle h-12 w-12">{c.initials}</div>
                  </div>
                  <div>{c.name}</div>
                </div>
              </a>
            </td>
            <td>
              <div>
                <span class="inline-block pr-2"><ContactInfoIcon icon={c.contactInfoIcon} /></span
                >{c.contactInfo}
              </div>
            </td>
          </tr>
        {/each}
      </tbody>
    </table>
  {/if}
</div>
