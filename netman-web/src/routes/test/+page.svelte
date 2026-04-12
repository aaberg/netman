<script lang="ts">
	import { enhance } from "$app/forms";
</script>

<div class="min-h-screen bg-base-100 font-sans text-base-content">
	<!-- Top Navigation Bar -->
	<header class="fixed top-0 left-0 right-0 z-50 bg-base-100/80 backdrop-blur-md flex justify-between items-center px-6 py-4 w-full">
		<div class="flex items-center gap-4">
			<button class="btn btn-ghost btn-circle hover:bg-base-200">
				<span class="material-symbols-outlined">close</span>
			</button>
			<h1 class="text-2xl font-bold tracking-tight">New Connection</h1>
		</div>
		<div class="flex items-center gap-3">
			<button class="btn btn-ghost px-5 py-2 text-sm font-semibold hover:bg-base-200">
				Cancel
			</button>
			<button class="btn btn-primary px-6 py-2 text-sm font-bold shadow-lg">
				Save
			</button>
		</div>
	</header>
	
	<main class="max-w-4xl mx-auto pt-24 pb-32 px-6">
		<div class="grid grid-cols-1 md:grid-cols-12 gap-10">
			<!-- Profile Photo Section -->
			<div class="md:col-span-4 lg:col-span-4">
				<div class="sticky top-28">
					<div class="relative group cursor-pointer">
						<div class="w-full aspect-square rounded-2xl bg-base-200 flex flex-col items-center justify-center overflow-hidden transition-all duration-300 group-hover:bg-base-300">
							<span class="material-symbols-outlined text-5xl text-base-content/60 mb-2">add_a_photo</span>
							<span class="text-xs font-semibold uppercase tracking-widest text-base-content/60">Upload Photo</span>
						</div>
						<!-- Connection Pulse -->
						<div class="absolute inset-0 rounded-2xl border-2 border-primary opacity-20 scale-105 blur-sm"></div>
					</div>
					<div class="mt-8 space-y-4">
						<div class="p-5 bg-base-200 rounded-xl">
							<h3 class="text-xs font-bold uppercase tracking-widest text-base-content/60 mb-3">Quick Actions</h3>
							<div class="space-y-3">
								<button class="btn btn-ghost w-full text-sm font-medium justify-start hover:text-primary p-1">
									<span class="material-symbols-outlined text-lg">scan_delete</span>
									Scan Business Card
								</button>
								<button class="btn btn-ghost w-full text-sm font-medium justify-start hover:text-primary p-1">
									<span class="material-symbols-outlined text-lg">link</span>
									Import LinkedIn
								</button>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<!-- Main Form Section -->
			<div class="md:col-span-8 lg:col-span-8 space-y-12">
				<!-- Identity Section -->
				<section>
					<h2 class="text-2xl font-bold tracking-tight mb-6">Identity</h2>
					<div class="grid grid-cols-1 gap-6">
						<div class="form-control">
							<label class="label" for="fullName">
								<span class="label-text text-xs font-bold uppercase tracking-widest text-base-content/60">Full Name</span>
							</label>
							<input type="text" placeholder="e.g. Alexander Sterling" class="input input-bordered w-full bg-base-200 border-none focus:outline-none focus:ring-2 focus:ring-primary" id="fullName" />
						</div>
						<div class="grid grid-cols-1 md:grid-cols-2 gap-6">
							<div class="form-control">
								<label class="label" for="jobTitle">
									<span class="label-text text-xs font-bold uppercase tracking-widest text-base-content/60">Job Title</span>
								</label>
								<input type="text" placeholder="e.g. Managing Director" class="input input-bordered w-full bg-base-200 border-none focus:outline-none focus:ring-2 focus:ring-primary" id="jobTitle" />
							</div>
							<div class="form-control">
								<label class="label" for="company">
									<span class="label-text text-xs font-bold uppercase tracking-widest text-base-content/60">Company</span>
								</label>
								<input type="text" placeholder="e.g. Paradigm Capital" class="input input-bordered w-full bg-base-200 border-none focus:outline-none focus:ring-2 focus:ring-primary" id="company" />
							</div>
						</div>
					</div>
				</section>
				
				<!-- Contact Details Section -->
				<section class="p-8 bg-base-100 rounded-2xl shadow-lg">
					<h2 class="text-2xl font-bold tracking-tight text-primary mb-6">Contact Channels</h2>
					<div class="space-y-6">
						<div class="grid grid-cols-1 md:grid-cols-2 gap-6">
							<div class="form-control">
								<label class="label" for="email">
									<span class="label-text text-xs font-bold uppercase tracking-widest text-base-content/60">Email Address</span>
								</label>
								<label class="input input-bordered flex items-center gap-2 bg-base-200 border-none focus-within:outline-none focus-within:ring-2 focus-within:ring-primary">
									<span class="material-symbols-outlined text-base-content/60">mail</span>
									<input type="email" placeholder="name@company.com" class="grow bg-transparent focus:outline-none" id="email" />
								</label>
							</div>
							<div class="form-control">
								<label class="label" for="phone">
									<span class="label-text text-xs font-bold uppercase tracking-widest text-base-content/60">Phone Number</span>
								</label>
								<label class="input input-bordered flex items-center gap-2 bg-base-200 border-none focus-within:outline-none focus-within:ring-2 focus-within:ring-primary">
									<span class="material-symbols-outlined text-base-content/60">call</span>
									<input type="tel" placeholder="+1 (555) 000-0000" class="grow bg-transparent focus:outline-none" id="phone" />
								</label>
							</div>
						</div>
						<div class="form-control">
							<label class="label" for="location">
								<span class="label-text text-xs font-bold uppercase tracking-widest text-base-content/60">Office Location</span>
							</label>
							<label class="input input-bordered flex items-center gap-2 bg-base-200 border-none focus-within:outline-none focus-within:ring-2 focus-within:ring-primary">
								<span class="material-symbols-outlined text-base-content/60">location_on</span>
								<input type="text" placeholder="e.g. London, Mayfair" class="grow bg-transparent focus:outline-none" id="location" />
							</label>
						</div>
					</div>
				</section>
				
				<!-- Strategic Segmentation -->
				<section>
					<div class="flex items-center justify-between mb-6">
						<h2 class="text-2xl font-bold tracking-tight text-primary">Strategic Tags</h2>
						<span class="badge badge-primary badge-sm">AI Suggested</span>
					</div>
					<div class="flex flex-wrap gap-3">
						<button class="btn btn-primary btn-sm">
							<span class="material-symbols-outlined text-sm" style="font-variation-settings: 'FILL' 1;">star</span>
							High Priority
						</button>
						<button class="btn btn-ghost btn-sm hover:bg-base-300">
							Potential Partner
						</button>
						<button class="btn btn-ghost btn-sm hover:bg-base-300">
							Investor
						</button>
						<button class="btn btn-ghost btn-sm hover:bg-base-300">
							Industry Keynote
						</button>
						<button class="btn btn-ghost btn-sm border border-dashed border-base-content/20 hover:bg-base-200">
							<span class="material-symbols-outlined text-lg">add</span>
						</button>
					</div>
				</section>
				
				<!-- Intelligence & Notes -->
				<section>
					<label class="label" for="notes">
						<span class="label-text text-xs font-bold uppercase tracking-widest text-base-content/60">Contextual Notes & Intelligence</span>
					</label>
					<textarea placeholder="Document meeting context, shared interests, or follow-up triggers..." class="textarea textarea-bordered w-full bg-base-200 border-none focus:outline-none focus:ring-2 focus:ring-primary resize-none" rows="6" id="notes"></textarea>
					<p class="mt-3 text-xs text-base-content/60 flex items-center gap-1.5">
						<span class="material-symbols-outlined text-sm">lock</span>
						This information is encrypted and visible only to your executive profile.
					</p>
				</section>
			</div>
		</div>
	</main>
	
	<!-- Bottom Action Anchor (Mobile Only) -->
	<div class="fixed bottom-0 left-0 right-0 p-6 md:hidden z-50">
		<button class="btn btn-primary w-full py-4 rounded-2xl font-bold shadow-xl">
			<span class="material-symbols-outlined">person_add</span>
			Create Connection
		</button>
	</div>
</div>

<style>
	.material-symbols-outlined {
		font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
	}
</style>