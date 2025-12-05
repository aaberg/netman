<script lang="ts">
  import type { PageProps } from "./$types"
  import { compareDetails, type Email, type Note, type Phone } from "$lib/contactModel"

  const { data }: PageProps = $props()
  const { tenant, contact } = data
  const details = contact.details.sort(compareDetails)
  console.log(details)

  // Type guards for discriminated rendering
  const isEmail = (d: Email | Phone | Note): d is Email =>
    (d as Email) && (d as Email).address !== undefined

  const isPhone = (d: Email | Phone | Note): d is Phone =>
    (d as Phone) && (d as Phone).number !== undefined

  const isNote = (d: Email | Phone | Note): d is Note =>
    (d as Note) && (d as Note).note !== undefined
</script>

<!-- Page container -->
<div class="container mx-auto max-w-4xl p-4 md:p-6">
  <!-- Back button -->
  <div class="mb-4">
    <a href={`/app/${tenant}/contacts`} class="btn btn-ghost gap-2" aria-label="Back to contacts">
      <!-- left arrow icon -->
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 24 24"
        fill="currentColor"
        class="h-5 w-5"
      >
        <path d="M10.5 19.5 3 12l7.5-7.5 1.06 1.06L5.62 11H21v1.5H5.62l5.94 5.94-1.06 1.06Z" />
      </svg>
      <span class="hidden sm:inline">Back to contacts</span>
      <span class="sm:hidden">Back</span>
    </a>
  </div>
  <!-- Header card -->
  <div class="card bg-base-100 border-base-200 border shadow-xl">
    <div class="card-body">
      <div class="flex items-center justify-between gap-4">
        <div class="flex gap-4">
          <!-- Avatar with initials -->
          <div class="avatar avatar-placeholder">
            <div class="bg-primary text-primary-content w-16 rounded-full">
              <span class="text-2xl font-bold" aria-hidden="true" aria-label="Initials"
                >{contact.initials}</span
              >
            </div>
          </div>
          <div>
            <h1 class="text-2xl font-bold md:text-3xl">{contact.name}</h1>
            <p class="text-base-content/60 text-sm">Contact</p>
          </div>
        </div>
        <div class="flex items-center gap-2">
          <a
            class="btn btn-primary btn-outline"
            aria-label="Edit contact"
            title="Edit contact"
            href="/app/{tenant}/contacts/{contact.id}/edit"
          >
            <!-- simple pencil icon -->
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="h-5 w-5"
            >
              <path
                d="M21.731 2.269a2.625 2.625 0 0 0-3.712 0l-1.157 1.157 3.712 3.712 1.157-1.157a2.625 2.625 0 0 0 0-3.712z"
              />
              <path d="M3 17.25V21h3.75L19.061 8.689l-3.712-3.712L3 17.25z" />
            </svg>
            <span class="hidden sm:inline">Edit</span>
          </a>
        </div>
      </div>
    </div>
  </div>

  <!-- Details section -->
  <div class="mt-6 grid grid-cols-1 gap-4 md:grid-cols-3">
    {#each details as d}
      {#if isEmail(d)}
        <div class="card bg-base-100 border-base-200 border shadow-sm">
          <div class="card-body">
            <div class="flex items-start gap-3">
              <div class="badge badge-primary badge-outline" aria-hidden="true">Email</div>
              {#if d.isPrimary}
                <div class="badge badge-success">Primary</div>
              {/if}
            </div>
            <div class="mt-2 flex items-center gap-2">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="currentColor"
                class="h-5 w-5 opacity-70"
              >
                <path
                  d="M1.5 6A2.25 2.25 0 0 1 3.75 3.75h16.5A2.25 2.25 0 0 1 22.5 6v12a2.25 2.25 0 0 1-2.25 2.25H3.75A2.25 2.25 0 0 1 1.5 18V6zm2.931-.75a.75.75 0 0 0-.681 1.061l7.5 6.75a.75.75 0 0 0 .998 0l7.5-6.75a.75.75 0 0 0-.998-1.122L12 11.69 4.25 5.939a.75.75 0 0 0-.319-.689.754.754 0 0 0-.5 0z"
                />
              </svg>
              <a
                href={`mailto:${d.address}`}
                class="link link-hover"
                aria-label={`Email ${contact.name}`}
              >
                {d.address}
              </a>
            </div>
            {#if d.label}
              <div class="mt-1 text-sm opacity-70">{d.label}</div>
            {/if}
          </div>
        </div>
      {:else if isPhone(d)}
        <div class="card bg-base-100 border-base-200 border shadow-sm">
          <div class="card-body">
            <div class="badge badge-info badge-outline w-fit" aria-hidden="true">Phone</div>
            <div class="mt-2 flex items-center gap-2">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="currentColor"
                class="h-5 w-5 opacity-70"
              >
                <path
                  d="M2.25 3A.75.75 0 0 1 3 2.25h4.5a.75.75 0 0 1 .75.75v3.75a.75.75 0 0 1-.75.75H5.25c.266 3.9 3.6 7.234 7.5 7.5V16.5a.75.75 0 0 1 .75.75V21a.75.75 0 0 1-.75.75H12A10.5 10.5 0 0 1 1.5 11.25V9a.75.75 0 0 1 .75-.75H3A.75.75 0 0 1 2.25 7.5V3z"
                />
              </svg>
              <a
                href={`tel:${d.number}`}
                class="link link-hover"
                aria-label={`Call ${contact.name}`}
              >
                {d.number}
              </a>
            </div>
            {#if d.label}
              <div class="mt-1 text-sm opacity-70">{d.label}</div>
            {/if}
          </div>
        </div>
      {:else if isNote(d)}
        <div class="card bg-base-100 border-base-200 border shadow-sm md:col-span-3">
          <div class="card-body">
            <div class="badge badge-secondary badge-outline w-fit" aria-hidden="true">Note</div>
            <p class="mt-2 leading-relaxed whitespace-pre-wrap">{d.note}</p>
          </div>
        </div>
      {/if}
    {/each}

    {#if details.length === 0}
      <div class="col-span-full">
        <div class="alert">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="stroke-info h-6 w-6"
            fill="none"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0z"
            />
          </svg>
          <span>No details available yet.</span>
        </div>
      </div>
    {/if}
  </div>
</div>
