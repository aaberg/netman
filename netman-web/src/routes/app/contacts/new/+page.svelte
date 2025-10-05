<script lang="ts">
    import type {Contact} from "$lib/contact";

    let contact: Contact = $state({
        id: null,
        name: "",
        initials: "",
        details: []
    })

    function addEmail() {
        contact.details.push({
            id: null,
            detail: {
                type: "email",
                address: "",
                label: "",
                isPrimary: false
            }
        })

        console.log(contact)
    }
</script>

<h1 class="text-3xl">New contact</h1>

<div class="m-4 w-full max-w-lg">
    <label class="floating-label">
        <input type="text" placeholder="Name of contact" bind:value={contact.name}
               class="input w-full input-md" required autofocus autocomplete="off"/>
        <span>Your name</span>
    </label>
</div>

<!--<fieldset class="fieldset bg-base-200 border-base-300 rounded-box w-full max-w-lg p-4 border">-->
<!--    <legend class="fieldset-legend">Emails</legend>-->

<!--    <label class="floating-label">-->
<!--        <input type="text" placeholder="Email" bind:value={contactName}-->
<!--               class="input w-full" required autofocus autocomplete="off"/>-->
<!--        <span>Email</span>-->
<!--    </label>-->
<!--    <div class="divider"></div>-->
<!--    <button class="btn btn-primary btn-sm w-52">+ Add email</button>-->
<!--</fieldset>-->

<div class="divider text-xs opacity-60"></div>

<ul class="list bg-base-200 border-base-300 rounded-box w-full max-w-lg p-4 border">
    <li class="list-item">
        <div class="flex justify-between">
            <div class="inline-block">Emails</div>
            <button class="btn btn-outline btn-sm w-28" onclick={addEmail}>+ Add email</button>
        </div>
    </li>
</ul>