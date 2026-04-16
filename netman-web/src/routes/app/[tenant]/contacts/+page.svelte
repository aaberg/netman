<script lang="ts">

	import type {PageProps} from "../../../../../.svelte-kit/types/src/routes/app/[tenant]/contacts/$types";

	let { data }: PageProps = $props()
	let { tenant } = data

	let search = $state("")

	const filteredContacts = $derived(
			data.contacts.filter((c) => c?.name?.toLowerCase().includes(search.toLowerCase()))
	)

	let filterTypes = $state([
		{ icon: "filter_list", label: "All Types", active: true },
		{ icon: "history", label: "Recent", active: false },
		{ icon: "star", label: "High Priority", active: false }
	]);

	// Active filter
	let activeFilter = $state("All Types");
	
	function setFilter(filter: string) {
		activeFilter = filter;
		// Update filter types reactively by creating a new array
		filterTypes = filterTypes.map(f => ({
			...f,
			active: f.label === filter
		}));
	}
	
	// Get status color class
	function getStatusColor(status: string) {
		const colors: Record<string, string> = {
			Overdue: "bg-error",
			Scheduled: "bg-secondary",
			None: "bg-neutral"
		};
		return colors[status] || "bg-neutral";
	}
	
	function getStatusTextColor(status: string) {
		const colors: Record<string, string> = {
			Overdue: "text-error-content",
			Scheduled: "text-secondary-content",
			None: "text-neutral-content"
		};
		return colors[status] || "text-neutral-content";
	}
</script>

<!-- Hero Section -->
<div class="flex flex-col md:flex-row md:items-end justify-between gap-8 mb-12">
	<div class="space-y-2">
		<span class="text-xs font-bold uppercase tracking-widest text-accent">Network Directory</span>
		<h1 class="text-5xl md:text-6xl font-extrabold tracking-tight">Your Inner Circle</h1>
	</div>
	<a href={`/app/${tenant}/contacts/new`} class="btn btn-neutral px-8 py-4 rounded-xl shadow-lg hover:scale-105 transition-transform">
		<span class="material-symbols-outlined">person_add</span>
		Add Contact
	</a>
</div>

<!-- Search & Filters -->
<div class="bg-base-200 p-2 rounded-2xl mb-12 flex flex-col md:flex-row gap-2">
	<div class="flex-grow relative">
		<span class="absolute left-4 top-1/2 -translate-y-1/2 text-base-content/60">
			<span class="material-symbols-outlined">search</span>
		</span>
		<input
			bind:value={search}
			class="input w-full bg-base-300 border-none rounded-xl py-4 pl-12 pr-4 focus:outline-none focus:ring-2 focus:ring-primary"
			placeholder="Search by name, role, or company..."
			type="text" />
	</div>
	<div class="flex gap-2 overflow-hidden md:overflow-visible">
		<div class="flex gap-2 min-w-full md:min-w-fit">
			{#each filterTypes as filter (filter.label)}
				<button
					class="btn btn-ghost rounded-xl font-semibold whitespace-nowrap min-w-[120px] flex-shrink-0 {filter.active ? 'bg-primary text-primary-content hover:bg-primary-focus' : 'bg-base-100 hover:bg-base-200'}"
					onclick={() => setFilter(filter.label)}>
					<span class="material-symbols-outlined text-sm">{filter.icon}</span>
					{filter.label}
				</button>
			{/each}
		</div>
	</div>
</div>

<!-- Contacts Grid -->
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
	{#each filteredContacts as contact (contact.id)}

		<!-- Regular Contact Card -->
		<div class="card bg-base-100 rounded-xl shadow-sm hover:shadow-lg hover:bg-base-200 transition-all duration-200">
			<div class="card-body p-6">
				<div class="flex justify-between items-start mb-6">
					<div class="relative">
						{#if (contact.imageUrl)}
							<div class="avatar">
								<div class="w-24 rounded-full">
									<img src="{contact.imageUrl}" alt="avatar" />
								</div>
							</div>
						{:else }
							<div class="avatar avatar-placeholder">
								<div class="bg-neutral text-neutral-content w-24 rounded-full">
									<span class="text-3xl">{contact.initials}</span>
								</div>
							</div>
						{/if}
					</div>
					<span class={`badge badge-sm ${getStatusColor(contact.followUpStatus)} ${getStatusTextColor(contact.followUpStatus)} text-xs font-bold uppercase tracking-widest`}>
						{contact.followUpIn}
					</span>
				</div>
				<div class="space-y-1 mb-6">
					<h3 class="text-xl font-bold">{contact.name}</h3>
					<p class="text-base-content/70 font-medium">
						{[contact.title, contact.organization].filter(Boolean).join(' • ') || 'No title or company'}
					</p>
				</div>
				<div class="divider my-0"></div>
				<div class="flex items-center justify-between pt-4">
						<div class="flex items-center gap-2 text-sm text-base-content/60">
							<span class="material-symbols-outlined text-sm">location_on</span>
							<span>{contact.location || 'No location'}</span>
						</div>

					<a href={`/app/${tenant}/contacts/${contact.id}/edit`} class="btn btn-sm btn-primary text-white btn-ghost bg-base-200 hover:bg-base-300">
						Edit
					</a>
				</div>
			</div>
		</div>
	{/each}
</div>
