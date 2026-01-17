<script lang="ts">
  import type { ContactWithDetails, Email, Note, Phone } from "$lib/contactModel"
  import LabelCombobox from "./LabelCombobox.svelte"

  interface Props {
    contact: ContactWithDetails
    availableLabels: string[]
  }

  let { contact = $bindable(), availableLabels }: Props = $props()

  // Ensure details array is initialized
  if (!contact.details) {
    contact.details = []
  }

  let emails = $derived(
    contact.details.filter((detail): detail is Email => detail.type === "email")
  )

  let phones = $derived(
    contact.details.filter((detail): detail is Phone => detail.type === "phone")
  )

  let notes = $derived(contact.details.filter((detail): detail is Note => detail.type === "note"))

  function addEmail() {
    contact.details!.push({
      type: "email",
      address: "",
      label: "",
      isPrimary: false
    })

    console.log(contact)
  }

  function removeEmail(email: Email) {
    const index = contact.details!.indexOf(email)
    contact.details!.splice(index, 1)
  }

  function addPhone() {
    contact.details!.push({
      type: "phone",
      number: "",
      label: "",
      isPrimary: false
    })

    console.log(contact)
  }

  function removePhone(phone: Phone) {
    const index = contact.details!.indexOf(phone)
    contact.details!.splice(index, 1)
  }

  function addNote() {
    contact.details!.push({
      type: "note",
      note: ""
    })
  }

  function removeNote(note: Note) {
    const index = contact.details!.indexOf(note)
    contact.details!.splice(index, 1)
  }
</script>

<div class="m-4 w-full max-w-lg">
  <label class="floating-label">
    <input
      type="text"
      placeholder="Name of contact"
      bind:value={contact.name}
      class="input input-lg w-full"
      required
      autofocus
      autocomplete="off"
    />
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
          <input
            type="email"
            placeholder="Email address"
            bind:value={email.address}
            class="input input-sm w-full"
            required
            autocomplete="off"
          />
          <span>Email address</span>
        </label>
        <div class="w-28">
          <LabelCombobox bind:label={email.label} {availableLabels} placeholder="Label" />
        </div>
      </div>

      <div class="flex flex-row justify-between">
        <label class="label cursor-pointer">
          <span class="label-text">Primary email</span>
          <input type="checkbox" class="checkbox" bind:checked={email.isPrimary} />
        </label>
        <button class="btn btn-link btn-warning btn-sm w-16" onclick={() => removeEmail(email)}
          >Delete</button
        >
      </div>
    </li>
  {/each}
</ul>

<ul class="list bg-base-200 border-base-300 rounded-box mt-4 w-full max-w-lg border">
  <li class="list-row flex justify-between">
    <div class="inline-block">Phone numbers</div>
    <button class="btn btn-outline btn-sm w-28" onclick={addPhone}>+ Add phone</button>
  </li>
  {#each phones as phone}
    <li class="list-row flex flex-col gap-1">
      <div class="flex flex-row">
        <label class="floating-label grow pr-4">
          <input
            type="tel"
            placeholder="Phone number"
            bind:value={phone.number}
            class="input input-sm w-full"
            required
            autocomplete="off"
          />
          <span>Phone number</span>
        </label>
        <div class="w-28">
          <LabelCombobox bind:label={phone.label} {availableLabels} placeholder="Label" />
        </div>
      </div>

      <div class="flex flex-row justify-between">
        <div></div>
        <button class="btn btn-link btn-warning btn-sm w-16" onclick={() => removePhone(phone)}
          >Delete</button
        >
      </div>
    </li>
  {/each}
</ul>

<ul class="list bg-base-200 border-base-300 rounded-box mt-4 w-full max-w-lg border">
  <li class="list-row flex justify-between">
    <div class="inline-block">Notes</div>
    <button class="btn btn-outline btn-sm w-28" onclick={addNote}>+ Add note</button>
  </li>
  {#each notes as note}
    <li class="list-row flex flex-col gap-1">
      <div class="flex flex-row">
        <label class="floating-label grow">
          <textarea
            placeholder="Note"
            bind:value={note.note}
            class="textarea input-sm w-full"
            required
          ></textarea>
          <span>Note</span>
        </label>
      </div>

      <div class="flex flex-row justify-between">
        <div></div>
        <button class="btn btn-link btn-warning btn-sm w-16" onclick={() => removeNote(note)}
          >Delete</button
        >
      </div>
    </li>
  {/each}
</ul>
