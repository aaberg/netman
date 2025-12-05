---
applies_to: [netman-web/**]
---

# Frontend (Svelte/SvelteKit) Instructions

This file provides specific guidance for working on the Svelte/SvelteKit frontend application.

## Technology Stack

- **Framework**: SvelteKit 2.16
- **UI Library**: Svelte 5.0
- **Language**: TypeScript 5.x
- **Styling**: Tailwind CSS 4.0 with DaisyUI 5.1
- **Build Tool**: Vite 6.2
- **Testing**: Vitest 3.0 (unit), Playwright 1.49 (E2E)
- **Authentication**: Hanko Elements 2.1

## Code Organization

```
netman-web/
├── src/
│   ├── components/       # Reusable Svelte components
│   ├── lib/             # Shared utilities and models
│   │   ├── server/      # Server-side only code
│   │   └── ...
│   ├── routes/          # SvelteKit routes (file-based routing)
│   ├── app.d.ts         # TypeScript declarations
│   └── app.html         # HTML template
├── static/              # Static assets
├── e2e/                 # End-to-end tests
└── package.json
```

## Development Commands

```bash
# Install dependencies
npm install

# Start dev server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Run unit tests
npm run test:unit

# Run E2E tests (requires running application)
npm run test:e2e

# Lint code
npm run lint

# Format code
npm run format

# Type check
npm run check
```

## Code Style

### Prettier Configuration
The project uses Prettier with these settings:
- Spaces (not tabs), 2-space indentation
- No semicolons
- Print width: 100 characters
- No trailing commas
- Svelte and Tailwind CSS plugins enabled

### Naming Conventions
- Components: PascalCase (e.g., `ContactInfoIcon.svelte`)
- Files: camelCase for utilities (e.g., `contactModel.ts`)
- Variables/Functions: camelCase (e.g., `getUserProfile`)
- Constants: UPPER_SNAKE_CASE (e.g., `API_BASE_URL`)

### TypeScript Usage
- Always use TypeScript for type safety
- Define interfaces for props and data structures
- Avoid `any` type; use `unknown` if type is truly unknown
- Use strict type checking

## Svelte 5 Features

This project uses Svelte 5, so prefer modern patterns:

### Runes
Use Svelte 5 runes for reactive state:
```typescript
// State
let count = $state(0)

// Derived state
let doubled = $derived(count * 2)

// Effects
$effect(() => {
  console.log(`Count is ${count}`)
})

// Props (in components)
let { title, subtitle = 'Default' } = $props()
```

### Component Structure
```svelte
<script lang="ts">
  // Imports
  import { someUtil } from '$lib/utils'
  
  // Props
  interface Props {
    title: string
    items?: string[]
  }
  
  let { title, items = [] }: Props = $props()
  
  // State
  let count = $state(0)
  
  // Derived state
  let doubled = $derived(count * 2)
  
  // Functions
  function handleClick() {
    count++
  }
  
  // Effects
  $effect(() => {
    // Side effects
  })
</script>

<div>
  <h1>{title}</h1>
  <button onclick={handleClick}>Count: {count}</button>
</div>

<style>
  /* Component-scoped styles (use sparingly, prefer Tailwind) */
</style>
```

## SvelteKit Patterns

### Routes
- Use file-based routing in `src/routes`
- `+page.svelte`: Page component
- `+page.ts` or `+page.server.ts`: Page load functions
- `+layout.svelte`: Layout component
- `+layout.ts` or `+layout.server.ts`: Layout load functions
- `+server.ts`: API endpoints

### Load Functions
```typescript
// +page.ts (client-side or server-side)
export const load = async ({ fetch, params }) => {
  const response = await fetch(`/api/items/${params.id}`)
  const item = await response.json()
  
  return { item }
}

// +page.server.ts (server-side only)
export const load = async ({ locals }) => {
  // Access server-only resources
  const data = await database.query()
  return { data }
}
```

### Form Actions
```typescript
// +page.server.ts
export const actions = {
  default: async ({ request }) => {
    const data = await request.formData()
    // Process form data
    return { success: true }
  }
}
```

## Styling with Tailwind CSS and DaisyUI

### Tailwind CSS
- Use utility classes directly in components
- Follow mobile-first responsive design
- Use consistent spacing scale

### DaisyUI
- Leverage DaisyUI components (buttons, cards, modals, etc.)
- Use semantic color classes (`btn-primary`, `alert-error`)
- Refer to [DaisyUI documentation](https://daisyui.com/)

**Example:**
```svelte
<button class="btn btn-primary">Click me</button>
<div class="card bg-base-100 shadow-xl">
  <div class="card-body">
    <h2 class="card-title">Card Title</h2>
    <p>Card content</p>
  </div>
</div>
```

## API Integration

### Environment Variables
Use `PUBLIC_` prefix for client-accessible variables:
```typescript
import { PUBLIC_API_BASE_URL } from '$env/static/public'

const response = await fetch(`${PUBLIC_API_BASE_URL}/api/contacts`)
```

### Fetch Wrapper
Consider creating a fetch wrapper for consistent error handling:
```typescript
async function apiRequest(endpoint: string, options?: RequestInit) {
  const response = await fetch(`${PUBLIC_API_BASE_URL}${endpoint}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers
    }
  })
  
  if (!response.ok) {
    throw new Error(`API error: ${response.statusText}`)
  }
  
  return response.json()
}
```

## Testing

### Unit/Component Tests (Vitest)
- Test files: `*.spec.ts` or `*.test.ts`
- Use Testing Library for component testing
- Mock external dependencies

**Example:**
```typescript
import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/svelte'
import Button from './Button.svelte'

describe('Button', () => {
  it('renders with correct text', () => {
    render(Button, { props: { text: 'Click me' } })
    expect(screen.getByText('Click me')).toBeInTheDocument()
  })
})
```

### E2E Tests (Playwright)
- Test files in `e2e/` directory
- Ensure application is running before tests
- Test critical user flows

**Example:**
```typescript
import { test, expect } from '@playwright/test'

test('user can login', async ({ page }) => {
  await page.goto('/')
  await page.click('text=Login')
  await page.fill('[name="email"]', 'user@example.com')
  await page.fill('[name="password"]', 'password123')
  await page.click('button[type="submit"]')
  await expect(page).toHaveURL('/dashboard')
})
```

## Authentication with Hanko

The project uses Hanko for authentication:
```svelte
<script lang="ts">
  import { register } from '@teamhanko/hanko-elements'
  import { PUBLIC_HANKO_API_URL } from '$env/static/public'
  
  $effect(() => {
    register(PUBLIC_HANKO_API_URL)
  })
</script>

<hanko-auth />
```

## Type Safety

### Interface Definitions
Define types for all data structures:
```typescript
// contactModel.ts
export interface Contact {
  id: string
  name: string
  email: string
  phone?: string
}

export interface ContactFormData {
  name: string
  email: string
  phone?: string
}
```

### Component Props
Always type component props:
```typescript
interface Props {
  contact: Contact
  onSave?: (contact: Contact) => void
  disabled?: boolean
}

let { contact, onSave, disabled = false }: Props = $props()
```

## Performance Best Practices

- Use `$derived` for computed values (more efficient than reactive statements)
- Lazy load components when appropriate
- Optimize images (use WebP, proper sizing)
- Use SvelteKit's preloading for navigation
- Minimize bundle size (tree-shaking, code splitting)

## Accessibility

- Use semantic HTML elements
- Provide alt text for images
- Ensure keyboard navigation works
- Use ARIA labels when needed
- Test with screen readers
- Maintain sufficient color contrast

## Common Patterns

### Error Handling
```svelte
<script lang="ts">
  let error = $state<string | null>(null)
  let loading = $state(false)
  
  async function loadData() {
    loading = true
    error = null
    try {
      const data = await fetch('/api/data')
      // Process data
    } catch (e) {
      error = e instanceof Error ? e.message : 'An error occurred'
    } finally {
      loading = false
    }
  }
</script>

{#if loading}
  <p>Loading...</p>
{:else if error}
  <div class="alert alert-error">{error}</div>
{:else}
  <!-- Content -->
{/if}
```

### Form Handling
```svelte
<script lang="ts">
  let formData = $state({ name: '', email: '' })
  
  function handleSubmit(e: SubmitEvent) {
    e.preventDefault()
    // Process form
  }
</script>

<form onsubmit={handleSubmit}>
  <input type="text" bind:value={formData.name} />
  <input type="email" bind:value={formData.email} />
  <button type="submit">Submit</button>
</form>
```

## Common Pitfalls to Avoid

- Don't use deprecated Svelte 4 features (`let:` directives, `export let` for props)
- Don't forget to handle loading and error states
- Don't expose sensitive data in public environment variables
- Don't skip TypeScript types
- Don't ignore accessibility
- Don't use inline styles when Tailwind classes suffice

## Resources

- [SvelteKit Documentation](https://svelte.dev/docs/kit)
- [Svelte 5 Runes](https://svelte.dev/docs/svelte/what-are-runes)
- [Tailwind CSS Documentation](https://tailwindcss.com/docs)
- [DaisyUI Documentation](https://daisyui.com/)
- [Vitest Documentation](https://vitest.dev/)
- [Playwright Documentation](https://playwright.dev/)
