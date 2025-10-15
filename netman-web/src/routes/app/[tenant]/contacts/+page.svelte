<script lang="ts">
    import SearchInput from "../../../../components/SearchInput.svelte";
    import type {PageProps} from "./$types";

    let { data } : PageProps = $props()

    let search = $state("");

    const filtered = $derived(
        data.contacts.filter((c) =>
            c?.name?.toLowerCase().includes(search.toLowerCase())
        )
    );
</script>

<div class="navbar shadow-sm">
    <div class="flex-1">
        <div class="text-2xl">Contact list</div>
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
        <h2 class="text-xl text-center">Welcome!</h2>
        <div class="text-base-content/60 text-center pt-8">Create some contacts to get started :)</div>
    {:else if filtered.length === 0}
        <div class="text-base-content/60 text-center pt-8">No contacts found</div>
    {:else}
        <table class="table w-full">
            <thead>
            <tr>
                <th>Name</th>
                <th>Primary email</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            {#each filtered as c}
                <tr>
                    <td>
                        <div class="flex items-center gap-3">
                            <div class="avatar avatar-placeholder">
                                <div class="bg-neutral mask mask-squircle w-12 h-12">{c.initials}</div>
                            </div>
                            <div>{c.name}</div>
                        </div>
                    </td>
                    <td>
                        <div>email not visible yet</div>
                    </td>
                    <td>
                        <a href="#" class="link">Edit</a>
                    </td>
                </tr>
            {/each}
            </tbody>
        </table>
    {/if}
</div>
