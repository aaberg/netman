<script lang="ts">
    import type {Contact, ContactDetail, Email} from "$lib/contact";

    let contact: Contact = $state({
        id: null,
        name: "",
        initials: "",
        details: []
    })
    
    let emails = $derived(
        contact.details.filter(
            (detail): detail is ContactDetail<Email> => detail.type === "email"
        )
    )

    function addEmail() {
        contact.details.push({
            id: null,
            type: "email",
            detail: {
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
    <li class="list-row flex justify-between">
        <div class="inline-block">Emails</div>
        <button class="btn btn-outline btn-sm w-28" onclick={addEmail}>+ Add email</button>
    </li>
    {#each emails as email}
        <li class="list-row flex flex-col gap-4">
            <div class="flex flex-row">
                <label class="floating-label grow pr-4">
                    <input type="email" placeholder="Email address" bind:value={email.detail.address}
                           class="input w-full input-sm" required autocomplete="off"/>
                    <span>Email address</span>
                </label>
                <label class="floating-label w-28">
                    <input type="text" placeholder="Label" bind:value={email.detail.label}
                           class="input w-full input-sm" autocomplete="off"/>
                    <span>Label</span>
                </label>
            </div>

            <div class="flex flex-row justify-between">
                <label class="label cursor-pointer">
                    <span class="label-text">Primary email</span>
                    <input type="checkbox" class="checkbox" bind:checked={email.detail.isPrimary}/>
                </label>
                <button class="btn btn-link btn-warning btn-sm w-16">Delete</button>
            </div>

        </li>
    {/each}
</ul>