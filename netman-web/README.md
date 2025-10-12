# NetMan Web (Svelte/SvelteKit)

This folder contains the Svelte/SvelteKit frontend for NetMan.

Looking for setup and project-wide instructions? See the repository root README:
- ../README.md
- Or view it on your hosting platform (GitHub/GitLab/etc.) at the repo root.

## Quick commands

```powershell
npm install
npm run dev        # start dev server (usually http://localhost:5173)
npm run build      # production build
npm run preview    # preview prod build locally
npm run test       # unit/component tests (Vitest)
npm run test:e2e   # end-to-end tests (Playwright)
```

Note: When running the web app against a locally running API, ensure the API base URL is configured (e.g., PUBLIC_API_BASE_URL). See the root README for details on configuration, Docker Compose, and environment variables.
