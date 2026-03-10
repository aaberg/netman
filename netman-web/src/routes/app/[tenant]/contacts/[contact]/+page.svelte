<script lang="ts">
  import type { PageProps } from "./$types"
  import { compareDetails, type Email, type Note, type Phone } from "$lib/contactModel"

  const { data }: PageProps = $props()
  const { tenant, contact, communications } = data
  const details = (contact.details || []).sort(compareDetails)

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
  <div class="card bg-base-300 border-base-200 border shadow-xl">
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
          <a
            class="btn btn-secondary btn-outline"
            aria-label="Register communication"
            title="Register communication"
            href="/app/{tenant}/contacts/{contact.id}/communications/new"
          >
            <!-- plus icon -->
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="h-5 w-5"
            >
              <path d="M12 4.5v15m7.5-7.5h-15" />
            </svg>
            <span class="hidden sm:inline">Register Communication</span>
            <span class="sm:hidden">Add</span>
          </a>
        </div>
      </div>
    </div>
  </div>

  <!-- Details section -->
  <div class="mt-6 grid grid-cols-1 gap-4 md:grid-cols-3">
    {#each details as d, index (index)}
      {#if isEmail(d)}
        <div class="card bg-base-200 border-base-200 border shadow-sm">
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
        <div class="card bg-base-200 border-base-200 border shadow-sm">
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
        <div class="card bg-base-200 border-base-200 border shadow-sm md:col-span-3">
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

  <div class="divider"></div>
  <ul class="list bg-base-200 rounded-box shadow-md">
    <li class="p-4 pb-2 text-s opacity-60 tracking-wide">Previous communcations with this contact</li>
    <!-- List of communications -->
    {#if communications.length === 0}
      <li class="p-4">
        <div class="">
          <svg xmlns="http://www.w3.org/2000/svg" class="stroke-info h-6 w-6" fill="none" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0z" />
          </svg>
          <span>No communications yet.</span>
        </div>
      </li>
    {:else}
      {#each communications as comm (comm.id)}
        <li class="list-row">
          <div class="flex items-start gap-3">
            <!-- Communication type icon -->
            {#if comm.type === 'EMAIL'}
              <div class="avatar">
                <div class="w-8 h-8 rounded-full flex items-center justify-center">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="-4 -4 34 34" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                  </svg>
                </div>
              </div>
            {:else if comm.type === 'CALL'}
              <div class="avatar">
                <div class="w-8 h-8 rounded-full flex items-center justify-center">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="-4 -4 34 34" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                  </svg>
                </div>
              </div>
            {:else if comm.type === 'TEXT_MESSAGE'}
              <div class="avatar">
                <div class="w-8 h-8 rounded-full flex items-center justify-center">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="-4 -4 34 34" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                  </svg>
                </div>
              </div>
            {/if}
            
            <div class="flex-1">
              <!-- Communication type badge -->
              <div class="flex items-center justify-between gap-2 mb-1">
                <span class="text-xs text-base-content/60">{new Date(comm.timestamp).toLocaleString()}</span>
                <div class="flex items-center gap-2">
                  <a
                    class="btn btn-xs btn-ghost"
                    aria-label="Edit communication"
                    title="Edit communication"
                    href="/app/{tenant}/contacts/{contact.id}/communications/{comm.id}/edit"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="h-3 w-3">
                      <path d="M21.731 2.269a2.625 2.625 0 0 0-3.712 0l-1.157 1.157 3.712 3.712 1.157-1.157a2.625 2.625 0 0 0 0-3.712z"/>
                      <path d="M3 17.25V21h3.75L19.061 8.689l-3.712-3.712L3 17.25z"/>
                    </svg>
                  </a>
                </div>
              </div>
              
              <!-- Communication content -->
              <p class="text-sm leading-relaxed whitespace-pre-wrap">{comm.content}</p>
              
               Metadata if available
              {#if comm.metadata !== undefined && Object.keys(comm.metadata).length > 0}
                <div class="mt-2 text-xs text-base-content/70">
                  {#each Object.entries(comm.metadata) as [key, value] (key)}
                    <div><strong>{key}:</strong> {value}</div>
                  {/each}
                </div>
              {/if}
            </div>
          </div>
        </li>
      {/each}
    {/if}
  </ul>
</div>
