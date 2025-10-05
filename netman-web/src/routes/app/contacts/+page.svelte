<script>
import SearchInput from "../../../components/SearchInput.svelte";

let search = $state("");
</script>

<div class="navbar shadow-sm">
    <div class="flex-1">
        <div class="text-2xl">Contact list</div>
    </div>

    <div class="flex gap-2">
        <SearchInput bind:value={search} />
        <a class="btn btn-neutral btn-sm" href="contacts/new">+ New contact</a>
    </div>
</div>
