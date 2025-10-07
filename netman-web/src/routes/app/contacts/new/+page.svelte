<script lang="ts">
    import type {Contact, ContactDetail, Email, Note, Phone} from "$lib/contact";

    let contact: Contact = $state({
        id: null,
        name: "",
        initials: "",
        details: [],
        note: ""
    })
    
    let emails = $derived(
        contact.details.filter(
            (detail): detail is ContactDetail<Email> => detail.type === "email"
        )
    )

    let phones = $derived(
        contact.details.filter(
            (detail): detail is ContactDetail<Phone> => detail.type === "phone"
        )
    )

    let notes = $derived(
        contact.details.filter(
            (detail): detail is ContactDetail<Note> => detail.type === "note"
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

    function removeEmail(email: ContactDetail<Email>) {
        const index = contact.details.indexOf(email)
        contact.details.splice(index, 1)
    }

    function addPhone() {
        contact.details.push({
            id: null,
            type: "phone",
            detail: {
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
            type: "note",
            detail: {
                note: ""
            }
        })
    }

    function removeNote(note: ContactDetail<Note>) {
        const index = contact.details.indexOf(note)
        contact.details.splice(index, 1)
    }
</script>

<h1 class="text-3xl">New contact</h1>

<div class="m-4 w-full max-w-lg">
    <label class="floating-label">
        <input type="text" placeholder="Name of contact" bind:value={contact.name}
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

<div class="flex mt-4 w-lg gap-2">
    <div class="grow"><button class="btn btn-primary w-full">Save</button></div>
    <div class="grow"><button class="btn btn-neutral w-full">Cancel</button></div>

</div>


