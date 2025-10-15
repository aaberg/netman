<script lang="ts">
    import type {ContactDetail, ContactWithDetails, Email, Note, Phone} from "$lib/contactModel";
    import {applyAction, deserialize} from "$app/forms";
    import type {ActionResult} from "@sveltejs/kit";
    import type {PageProps} from "./$types";

    let { data } : PageProps = $props()
    let { tenant } = data

    let contact: ContactWithDetails = $state({
        contact: {
            id: null,
            name: "",
            initials: "",
        },
        details: []
    })

    let serializedContact = $derived(JSON.stringify(contact))
    
    let emails = $derived(
        contact.details.filter(
            (detail): detail is ContactDetail<Email> => detail.detail.type === "email"
        )
    )

    let phones = $derived(
        contact.details.filter(
            (detail): detail is ContactDetail<Phone> => detail.detail.type === "phone"
        )
    )

    let notes = $derived(
        contact.details.filter(
            (detail): detail is ContactDetail<Note> => detail.detail.type === "note"
        )
    )

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

    function removeEmail(email: ContactDetail<Email>) {
        const index = contact.details.indexOf(email)
        contact.details.splice(index, 1)
    }

    function addPhone() {
        contact.details.push({
            id: null,
            detail: {
                type: "phone",
                number: "",
                label: "",
                isPrimary: false
            }
        })

        console.log(contact)
    }

    function removePhone(phone: ContactDetail<Phone>) {
        const index = contact.details.indexOf(phone)
        contact.details.splice(index, 1)
    }

    function addNote() {
        contact.details.push({
            id: null,
            detail: {
                type: "note",
                note: ""
            }
        })
    }

    function removeNote(note: ContactDetail<Note>) {
        const index = contact.details.indexOf(note)
        contact.details.splice(index, 1)
    }

    async function handleSubmit(event: SubmitEvent & { currentTarget: HTMLFormElement }) {
        event.preventDefault()
        const payload = JSON.stringify(contact)

        const response = await fetch(event.currentTarget.action, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: payload
        })
        const result: ActionResult = deserialize(await response.text())
        await applyAction(result)
    }
</script>

<h1 class="text-3xl">New contact</h1>

<div class="m-4 w-full max-w-lg">
    <label class="floating-label">
        <input type="text" placeholder="Name of contact" bind:value={contact.contact.name}
               class="input w-full input-lg" required autofocus autocomplete="off"/>
        <span>Name of contact</span>
    </label>
</div>

<ul class="list bg-base-200 border-base-300 rounded-box w-full max-w-lg border">
    <li class="list-row flex justify-between">
        <div class="inline-block">Emails</div>
        <button class="btn btn-outline btn-sm w-28" onclick={addEmail}>+ Add email</button>
    </li>
    {#each emails as email}
        <li class="list-row flex flex-col gap-1">
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
                <button class="btn btn-link btn-warning btn-sm w-16" onclick={() => removeEmail(email)}>Delete</button>
            </div>

        </li>
    {/each}
</ul>

<ul class="list bg-base-200 border-base-300 rounded-box w-full max-w-lg border mt-4">
    <li class="list-row flex justify-between">
        <div class="inline-block">Phone numbers</div>
        <button class="btn btn-outline btn-sm w-28" onclick={addPhone}>+ Add phone</button>
    </li>
    {#each phones as phone}
        <li class="list-row flex flex-col gap-1">
            <div class="flex flex-row">
                <label class="floating-label grow pr-4">
                    <input type="tel" placeholder="Phone number" bind:value={phone.detail.number}
                           class="input w-full input-sm" required autocomplete="off"/>
                    <span>Phone number</span>
                </label>
                <label class="floating-label w-28">
                    <input type="text" placeholder="Label" bind:value={phone.detail.label}
                           class="input w-full input-sm" autocomplete="off"/>
                    <span>Label</span>
                </label>
            </div>

            <div class="flex flex-row justify-between">
                <div></div>
                <button class="btn btn-link btn-warning btn-sm w-16" onclick={() => removePhone(phone)}>Delete</button>
            </div>
        </li>
    {/each}
</ul>

<ul class="list bg-base-200 border-base-300 rounded-box w-full max-w-lg border mt-4">
    <li class="list-row flex justify-between">
        <div class="inline-block">Notes</div>
        <button class="btn btn-outline btn-sm w-28" onclick={addNote}>+ Add note</button>
    </li>
    {#each notes as note}
        <li class="list-row flex flex-col gap-1">
            <div class="flex flex-row">
                <label class="floating-label grow">
                    <textarea placeholder="Note" bind:value={note.detail.note}
                           class="textarea w-full input-sm" required></textarea>
                    <span>Note</span>
                </label>
            </div>

            <div class="flex flex-row justify-between">
                <div></div>
                <button class="btn btn-link btn-warning btn-sm w-16" onclick={() => removeNote(note)}>Delete</button>
            </div>
        </li>
    {/each}
</ul>

<form class="flex mt-4 w-full max-w-lg gap-2" method="post" use:enhance>
    <input type="hidden" name="contact" value={serializedContact} />
    <div class="grow"><button class="btn btn-primary w-full" type="submit" >Save</button></div>
    <div class="grow"><a class="btn btn-neutral w-full" href="/app/{tenant}/contacts">Cancel</a></div>
</form>


