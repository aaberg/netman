<script lang="ts">
  interface Props {
    label: string
    availableLabels: string[]
    placeholder?: string
  }

  let { label = $bindable(), availableLabels, placeholder = "Label" }: Props = $props()

  let isOpen = $state(false)
  let searchTerm = $state("")

  // Filter labels based on search term
  let filteredLabels = $derived(
    availableLabels.filter((l) => l.toLowerCase().includes(searchTerm.toLowerCase()))
  )

  function selectLabel(selectedLabel: string) {
    label = selectedLabel
    searchTerm = ""
    isOpen = false
  }

  function handleInput(event: Event) {
    const target = event.target as HTMLInputElement
    label = target.value
    searchTerm = target.value
    isOpen = true
  }

  function handleFocus() {
    searchTerm = label
    isOpen = true
  }

  function handleBlur() {
    // Delay closing to allow click on dropdown items
    setTimeout(() => {
      isOpen = false
      searchTerm = ""
      // Ensure empty string instead of null/undefined
      if (label === null || label === undefined) {
        label = ""
      }
    }, 200)
  }

  function handleKeydown(event: KeyboardEvent) {
    if (event.key === "Escape") {
      isOpen = false
      searchTerm = ""
    }
  }
</script>

<div class="relative w-full">
  <label class="floating-label w-full">
    <input
      type="text"
      {placeholder}
      value={label}
      oninput={handleInput}
      onfocus={handleFocus}
      onblur={handleBlur}
      onkeydown={handleKeydown}
      class="input input-sm w-full pr-8"
      autocomplete="off"
    />
    <span>{placeholder}</span>
    <!-- Down arrow icon -->
    <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-2">
      <svg
        class="h-4 w-4 text-gray-400"
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 20 20"
        fill="currentColor"
        aria-hidden="true"
      >
        <path
          fill-rule="evenodd"
          d="M5.23 7.21a.75.75 0 011.06.02L10 11.168l3.71-3.938a.75.75 0 111.08 1.04l-4.25 4.5a.75.75 0 01-1.08 0l-4.25-4.5a.75.75 0 01.02-1.06z"
          clip-rule="evenodd"
        />
      </svg>
    </div>
  </label>

  {#if isOpen && filteredLabels.length > 0}
    <ul
      class="menu bg-base-100 border-base-300 rounded-box absolute z-10 mt-1 max-h-60 w-full overflow-auto border shadow-lg"
    >
      {#each filteredLabels as filteredLabel, i (i)}
        <li>
          <button
            type="button"
            class="text-left"
            onmousedown={(e) => {
              e.preventDefault()
              selectLabel(filteredLabel)
            }}
          >
            {filteredLabel}
          </button>
        </li>
      {/each}
    </ul>
  {/if}
</div>
