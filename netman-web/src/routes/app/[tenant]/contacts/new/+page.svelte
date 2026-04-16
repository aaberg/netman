<script lang="ts">
  import ContactForm from "../../../../../components/contact/ContactForm.svelte"
  import type { PageProps } from "./$types"

  let { data, form }: PageProps = $props()
</script>

<ContactForm
  tenant={data.tenant}
  mode="create"
  values={form?.values ?? data.formState.values}
  imageUrl={data.imageUrl}
  error={form?.error ?? data.formState.error}
/>
