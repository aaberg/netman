<script lang="ts">
  import type { SaveContactRequest } from "$lib/contactModel"
  import { onDestroy } from "svelte"

  interface Props {
    tenant: string
    mode: "create" | "edit"
    values: SaveContactRequest
    imageUrl: string | null
    error?: string | null
  }

  let { tenant, mode, values, imageUrl, error = null }: Props = $props()

  let selectedImageUrl = $state<string | null>(null)

  const pageTitle = mode === "create" ? "New Contact" : "Edit Contact"
  const submitLabel = mode === "create" ? "Create Contact" : "Save Changes"

  function buildInitials(name: string) {
    return name
      .trim()
      .split(/\s+/)
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part[0]?.toUpperCase() ?? "")
      .join("")
  }

  function handleImageChange(event: Event) {
    if (selectedImageUrl) {
      URL.revokeObjectURL(selectedImageUrl)
      selectedImageUrl = null
    }

    const input = event.currentTarget as HTMLInputElement
    const file = input.files?.[0]

    if (file) {
      selectedImageUrl = URL.createObjectURL(file)
    }
  }

  onDestroy(() => {
    if (selectedImageUrl) {
      URL.revokeObjectURL(selectedImageUrl)
    }
  })
</script>

<form method="POST" enctype="multipart/form-data" class="mx-auto max-w-6xl pb-28">
  <div class="mb-8 flex flex-col gap-4 md:mb-10 md:flex-row md:items-center md:justify-between">
    <div class="space-y-2">
      <a href={`/app/${tenant}/contacts`} class="link link-hover text-sm font-medium text-base-content/70">
        Back to contacts
      </a>
      <h1 class="text-4xl font-extrabold tracking-tight md:text-5xl">{pageTitle}</h1>
    </div>

    <div class="hidden gap-3 md:flex">
      <a href={`/app/${tenant}/contacts`} class="btn btn-ghost rounded-xl">Cancel</a>
      <button type="submit" class="btn btn-neutral rounded-xl px-6">{submitLabel}</button>
    </div>
  </div>

  {#if error}
    <div class="alert alert-error mb-8 rounded-2xl">
      <span>{error}</span>
    </div>
  {/if}

  <div class="grid grid-cols-1 gap-8 lg:grid-cols-12 lg:gap-10">
    <aside class="lg:col-span-4 xl:col-span-3">
      <div class="lg:sticky lg:top-28">
        <label class="card card-border bg-base-200 rounded-3xl shadow-sm transition hover:shadow-md">
          <div class="card-body items-center gap-5 p-6 text-center">
            {#if selectedImageUrl || imageUrl}
              <div class="avatar">
                <div class="w-44 rounded-3xl bg-base-300">
                  <img src={selectedImageUrl ?? imageUrl ?? undefined} alt="Contact avatar preview" />
                </div>
              </div>
            {:else}
              <div class="avatar avatar-placeholder">
                <div class="w-44 rounded-3xl bg-neutral text-neutral-content">
                  <span class="text-5xl font-bold">{buildInitials(values.name)}</span>
                </div>
              </div>
            {/if}

            <div class="space-y-2">
              <div class="text-sm font-semibold uppercase tracking-[0.2em] text-base-content/60">
                Profile photo
              </div>
              <div class="text-sm text-base-content/70">
                Upload a square image to personalize this contact.
              </div>
            </div>

            <input
              type="file"
              name="image"
              accept="image/*"
              class="file-input file-input-bordered w-full rounded-xl"
              onchange={handleImageChange}
            />
          </div>
        </label>
      </div>
    </aside>

    <div class="space-y-8 lg:col-span-8 xl:col-span-9">
      <section class="card bg-base-100 rounded-3xl shadow-sm">
        <div class="card-body gap-6 p-6 md:p-8">
          <div>
            <h2 class="text-2xl font-bold tracking-tight">Identity</h2>
          </div>

          <label class="form-control w-full gap-2">
            <span class="label-text text-xs font-bold uppercase tracking-[0.2em] text-base-content/60">Full name</span>
            <input
              type="text"
              name="name"
              value={values.name}
              placeholder="e.g. Alexander Sterling"
              class="input input-bordered w-full rounded-xl"
              required
            />
          </label>

          <div class="grid grid-cols-1 gap-6 md:grid-cols-2">
            <label class="form-control w-full gap-2">
              <span class="label-text text-xs font-bold uppercase tracking-[0.2em] text-base-content/60">Job title</span>
              <input
                type="text"
                name="title"
                value={values.title ?? ""}
                placeholder="e.g. Managing Director"
                class="input input-bordered w-full rounded-xl"
              />
            </label>

            <label class="form-control w-full gap-2">
              <span class="label-text text-xs font-bold uppercase tracking-[0.2em] text-base-content/60">Company</span>
              <input
                type="text"
                name="organization"
                value={values.organization ?? ""}
                placeholder="e.g. Paradigm Capital"
                class="input input-bordered w-full rounded-xl"
              />
            </label>
          </div>
        </div>
      </section>

      <section class="card bg-base-200 rounded-3xl shadow-sm">
        <div class="card-body gap-6 p-6 md:p-8">
          <div>
            <h2 class="text-2xl font-bold tracking-tight">Contact Channels</h2>
          </div>

          <div class="grid grid-cols-1 gap-6 md:grid-cols-2">
            <label class="form-control w-full gap-2">
              <span class="label-text text-xs font-bold uppercase tracking-[0.2em] text-base-content/60">Email address</span>
              <input
                type="email"
                name="email"
                value={values.email ?? ""}
                placeholder="name@company.com"
                class="input input-bordered w-full rounded-xl"
              />
            </label>

            <label class="form-control w-full gap-2">
              <span class="label-text text-xs font-bold uppercase tracking-[0.2em] text-base-content/60">Phone number</span>
              <input
                type="tel"
                name="phone"
                value={values.phone ?? ""}
                placeholder="+1 (555) 000-0000"
                class="input input-bordered w-full rounded-xl"
              />
            </label>
          </div>

          <label class="form-control w-full gap-2">
            <span class="label-text text-xs font-bold uppercase tracking-[0.2em] text-base-content/60">Office location</span>
            <input
              type="text"
              name="location"
              value={values.location ?? ""}
              placeholder="e.g. London, Mayfair"
              class="input input-bordered w-full rounded-xl"
            />
          </label>
        </div>
      </section>

      <section class="card bg-base-100 rounded-3xl shadow-sm">
        <div class="card-body gap-4 p-6 md:p-8">
          <label class="form-control w-full gap-2">
            <span class="label-text text-xs font-bold uppercase tracking-[0.2em] text-base-content/60">
              Contextual notes
            </span>
            <textarea
              name="notes"
              rows="7"
              placeholder="Document meeting context, shared interests, or follow-up triggers..."
              class="textarea textarea-bordered w-full rounded-2xl"
            >{values.notes ?? ""}</textarea>
          </label>
          <p class="text-sm text-base-content/60">This information is stored with the contact record.</p>
        </div>
      </section>
    </div>
  </div>

  <div class="fixed inset-x-0 bottom-0 z-40 border-t border-base-300 bg-base-100/95 p-4 backdrop-blur md:hidden">
    <div class="mx-auto flex max-w-6xl gap-3">
      <a href={`/app/${tenant}/contacts`} class="btn btn-ghost flex-1 rounded-xl">Cancel</a>
      <button type="submit" class="btn btn-neutral flex-1 rounded-xl">{submitLabel}</button>
    </div>
  </div>
</form>
