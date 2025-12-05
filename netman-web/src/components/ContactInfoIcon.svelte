<script lang="ts">
  // Displays a small icon based on the provided label string.
  // Supported: "Email", "Phone", "Note"
  export let icon: string

  const ariaLabel =
    icon === "Email" || icon === "Phone" || icon === "Note" ? `${icon} icon` : "icon"
</script>

{#if icon === "Email"}
  <!-- Envelope / Email icon -->
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 24 24"
    fill="currentColor"
    class="h-4 w-4"
    role="img"
    aria-label={ariaLabel}
  >
    <path
      d="M2 6.5A2.5 2.5 0 0 1 4.5 4h15A2.5 2.5 0 0 1 22 6.5v11A2.5 2.5 0 0 1 19.5 20h-15A2.5 2.5 0 0 1 2 17.5v-11Zm2.3-.5 7.2 5.1a1 1 0 0 0 1.16 0L19.86 6H4.3Zm15.2 2.22-5.92 4.2a3 3 0 0 1-3.36 0L4.3 8.22V17.5c0 .28.22.5.5.5h14.7c.28 0 .5-.22.5-.5V8.22Z"
    />
  </svg>
{:else if icon === "Phone"}
  <!-- Phone handset icon -->
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 24 24"
    fill="currentColor"
    class="h-4 w-4"
    role="img"
    aria-label={ariaLabel}
  >
    <path
      d="M6.62 10.79a15.05 15.05 0 0 0 6.59 6.59l1.93-1.93a1 1 0 0 1 1.02-.24c1.12.37 2.33.57 3.58.57a1 1 0 0 1 1 1V20a2 2 0 0 1-2 2C10.85 22 2 13.15 2 2a2 2 0 0 1 2-2h2.22a1 1 0 0 1 1 1c0 1.25.2 2.46.57 3.58a1 1 0 0 1-.24 1.02l-1.93 1.93Z"
    />
  </svg>
{:else if icon === "Note"}
  <!-- Note / document icon -->
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 24 24"
    fill="currentColor"
    class="h-4 w-4"
    role="img"
    aria-label={ariaLabel}
  >
    <path
      d="M6 2a2 2 0 0 0-2 2v16c0 1.1.9 2 2 2h9.5c.53 0 1.04-.21 1.41-.59l2.5-2.5c.38-.37.59-.88.59-1.41V4a2 2 0 0 0-2-2H6Zm11 14.59L15.59 18H14a1 1 0 0 1-1-1v-1.59a1 1 0 0 1 .29-.71l3.3-3.3a1 1 0 0 1 1.41 0L19 12.7a1 1 0 0 1 0 1.41l-2 2.48ZM8 7h8a1 1 0 1 1 0 2H8a1 1 0 1 1 0-2Zm0 4h5a1 1 0 1 1 0 2H8a1 1 0 1 1 0-2Z"
    />
  </svg>
{/if}

<style>
  /* Inherit text color so the icon can be tinted via parent text utilities */
  :global(svg) {
    display: inline-block;
    vertical-align: middle;
  }
</style>
