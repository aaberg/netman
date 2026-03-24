<script lang="ts">

    // Contact data
    const contacts = [
        {
            id: 1,
            name: "Elena Rodriguez",
            title: "VP of Product",
            company: "TechNova Systems",
            image: "https://lh3.googleusercontent.com/aida-public/AB6AXuC-4kPb1rATHaI-ypHgQrrg0FPO4OrvNFAMud4h8vICu5OGp9YH31P66Odn8cU2lRcIHZZk_F-tfFljUKyrsM5AY4gi1GcTzoHCWU1qzfazZYtJ7_YkYusqaahIXeSFWkpKJVOUpljKwWJ_GkTeJ_EUst8x3OhzY6P_Q3GMi9mDmKhw9ukjDu22q9UQpT2ocfaj87dxgO6Mj1N_z_O3wLIbYTPsXcaJpeYvw1pxtc30XRr7EvsyVD6slbaWUE4wibylv1KcG43K9c0",
            status: "overdue",
            statusText: "Overdue",
            statusColor: "error",
            action: "Follow-up"
        },
        {
            id: 2,
            name: "Julian Thorne",
            title: "Managing Director",
            company: "Blue Basin Capital",
            image: "https://lh3.googleusercontent.com/aida-public/AB6AXuCuu1jqz2DDJUaXB9zXPkPu4XQxP65cRUzDKpXXdBMF6gd51GAiZiA4I37fLkT9xIcJyVKiqpGf_EMM_zswtHHjvQPWPGN16xmFv0l1TkBpFoUFxisnBpZNir3PVF2YCTrwGawOBPZ582NNpQiOY9gLsbxNYOANrGo3VG-zW-Z0ZnEq1ebF8Dd6uKoxEprcCVSd9KOeisvvS9npg5p4x38eXy0zul_sixwmGLkgZIAcVi9Khzti846xv-dHERq21WHtvHsCB_PyKqY",
            status: "tomorrow",
            statusText: "Tomorrow",
            statusColor: "secondary",
            meeting: "Q3 Review",
            action: "Details"
        },
        {
            id: 3,
            name: "Sarah Jenkins",
            title: "Creative Lead",
            company: "Studio Paradiso",
            image: "https://lh3.googleusercontent.com/aida-public/AB6AXuCHoZpC3OFV66qxv2EnCGK6DEBiw3hKWaFezOCzWc8KSuW-i9U69mcPYbGGIwkE5cw6WIkr1QGkZpEal5wMwJJLqyekbCZlBdMHY9Gc71u032Ky29n0qfCTfVEpWx6Bt5pZZOmQnHOwLMysv4eRSh1NUcvYc35TsuRmA5i-g_0bQoKBUjyQPMt2By06Bd2LCO-JZmq7ZItQtC5rKf7RQ1BB0l11P27QjiBhVnT76MEDa_pyD3N_5L_vsqj1oqWde3FDiOKQNMuLPeQ",
            status: "connected",
            statusText: "Connected",
            statusColor: "neutral",
            location: "London, UK",
            action: "Details"
        },
        {
            id: 4,
            name: "Marcus Vane",
            title: "Chief Technology Officer",
            company: "Global Frontier",
            image: "https://lh3.googleusercontent.com/aida-public/AB6AXuCWUIxPVnbntq0qkuK99T3E_ojO2fphKNkHmETXydgUkeVxTICJRgp33E4HKaa-b95-BLXA2epekaVZvY65fgiE_WIkuq5GEA9pC37_LH8e9i9z1biRcvDlsx3gfb1WQtlq8YtubqBhZcLVX_0gYoJVKvEKQI6Rra91xN8LGr5XSMJ5c26rzG3e3y9-FOLDJuTuzaH2WZirfW06p3g2AajkUagUvxPNXYkDlhb3ZMJuSEnLIZtC_M0BlRD3AQMgObiS3e991tzpsq4",
            status: "vip",
            statusText: "VIP Referral",
            statusColor: "tertiary",
            referral: "Referral via Elena Rodriguez. Interested in scalable infrastructure and sustainable logistics.",
            isVip: true
        },
        {
            id: 5,
            name: "Dr. Amit Shah",
            title: "Head of Research",
            company: "BioLink Labs",
            image: "https://lh3.googleusercontent.com/aida-public/AB6AXuBH-FvRSfp9ZI6AxF5wNuHJHstLW3ntXXCX4HnfnE52WVH8FVuVlSejDOXKNBvBLp5JWTls4M4kmoeRPdAcvVwBkHiMc1IaPjak3NwOH8NfXrbrXk2zE_znK0U_QmUcApvMT6moV90a34Z1G3wgTEfWH4bhpL-DepDaVT_ci8kKQF-4xAyCVSXDcLS2zd04uLQ2FgF8HY-HPOsqc94Aa20ztB_quaiYtWHpCJcIHcS5WujY0SFlrUFTfmeJ_TgA6hOQY_kbY-EG0dI",
            status: "upcoming",
            statusText: "In 3 Days",
            statusColor: "secondary",
            meeting: "Collaboration",
            action: "Details"
        }
    ];

    // Filter types - use reactive statement
    let filterTypes = [
        { icon: "filter_list", label: "All Types", active: true },
        { icon: "history", label: "Recent", active: false },
        { icon: "star", label: "High Priority", active: false }
    ];

    // Active filter
    let activeFilter = "All Types";

    function setFilter(filter: string) {
        activeFilter = filter;
        // Update filter types reactively by creating a new array
        filterTypes = filterTypes.map(f => ({
            ...f,
            active: f.label === filter
        }));
    }

    // Get status color class
    function getStatusColor(statusColor: string) {
        const colors = {
            error: "bg-error",
            secondary: "bg-secondary",
            neutral: "bg-neutral",
            tertiary: "bg-accent"
        };
        return colors[statusColor] || "bg-neutral";
    }

    function getStatusTextColor(statusColor: string) {
        const colors = {
            error: "text-error-content",
            secondary: "text-secondary-content",
            neutral: "text-neutral-content",
            tertiary: "text-accent-content"
        };
        return colors[statusColor] || "text-neutral-content";
    }
</script>

<div class="min-h-screen bg-base-100 text-base-content font-sans">
    <!-- Top App Bar -->
    <header class="navbar bg-base-200 fixed top-0 z-50 px-6 py-4 shadow-sm">
        <div class="flex-1">
            <div class="flex items-center gap-4">
                <div class="w-10 h-10 rounded-full bg-primary-content flex items-center justify-center">
                    <img src="https://lh3.googleusercontent.com/aida-public/AB6AXuAGAbm5ww31jHSTvS1HGxGWBQIlAxQqZ-BqraJ_UPB0BL5pywvIqLWMizeb29l7Fw3Wn3kE17axb5g4v4NLorFMAG07m5XQ9Xwj_ZQl8pV7IQ3bjyz22ltpuyJTXaWEg1OfDavK8Z-or974CxVnNk4dWmy9UhX5HeAL842F-ZL8Om6tfv6qu0iCoYjz1tmFRkyYBp07tVv_RtWGPM5gs9du86zvnwMfnEPGnjfcYgUNO4MYWWbV4-QLxwdQ3wiBR_6ExS93KeQGJ1M"
                         alt="User profile" class="w-full h-full object-cover rounded-full" />
                </div>
                <span class="font-bold text-xl tracking-tight">Netværke</span>
            </div>
        </div>
        <div class="flex-none">
            <nav class="hidden md:flex gap-4">
                <a class="btn btn-ghost btn-sm rounded-lg font-bold" href="#">Contacts</a>
                <a class="btn btn-ghost btn-sm rounded-lg opacity-60" href="#">Follow-ups</a>
                <a class="btn btn-ghost btn-sm rounded-lg opacity-60" href="#">AI Insights</a>
            </nav>
        </div>
    </header>

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto px-6 pt-24 pb-32">
        <!-- Hero Section -->
        <div class="flex flex-col md:flex-row md:items-end justify-between gap-8 mb-12">
            <div class="space-y-2">
                <span class="text-xs font-bold uppercase tracking-widest text-accent">Network Directory</span>
                <h1 class="text-5xl md:text-6xl font-extrabold tracking-tight">Your Inner Circle</h1>
            </div>
            <button class="btn btn-neutral px-8 py-4 rounded-xl shadow-lg hover:scale-105 transition-transform">
                <span class="material-symbols-outlined">person_add</span>
                Add Contact
            </button>
        </div>

        <!-- Search & Filters -->
        <div class="bg-base-200 p-2 rounded-2xl mb-12 flex flex-col md:flex-row gap-2">
            <div class="flex-grow relative">
				<span class="absolute left-4 top-1/2 -translate-y-1/2 text-base-content/60">
					<span class="material-symbols-outlined">search</span>
				</span>
                <input
                        class="input w-full bg-base-300 border-none rounded-xl py-4 pl-12 pr-4 focus:outline-none focus:ring-2 focus:ring-primary"
                        placeholder="Search by name, role, or company..."
                        type="text" />
            </div>
            <div class="flex gap-2 overflow-hidden md:overflow-visible">
                <div class="flex gap-2 min-w-full md:min-w-fit">
                    {#each filterTypes as filter (filter.label)}
                        <button
                                class="btn btn-ghost rounded-xl font-semibold whitespace-nowrap min-w-[120px] flex-shrink-0 {filter.active ? 'bg-primary text-primary-content hover:bg-primary-focus' : 'bg-base-100 hover:bg-base-200'}"
                                on:click={() => setFilter(filter.label)}>
                            <span class="material-symbols-outlined text-sm">{filter.icon}</span>
                            {filter.label}
                        </button>
                    {/each}
                </div>
            </div>
        </div>

        <!-- Contacts Grid -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {#each contacts as contact (contact.id)}

                <!-- Regular Contact Card -->
                <div class="card bg-base-100 rounded-xl shadow-sm hover:shadow-md transition-shadow duration-300">
                    <div class="card-body p-6">
                        <div class="flex justify-between items-start mb-6">
                            <div class="relative">
                                {#if contact.id === 1}
                                    <div class="absolute inset-0 bg-primary/20 rounded-full blur-xl -z-10"></div>
                                {/if}
                                <img
                                        src={contact.image}
                                        alt={contact.name}
                                        class="w-16 h-16 rounded-full object-cover border-2 border-base-200" />
                            </div>
                            <span class={`badge badge-sm ${getStatusColor(contact.statusColor)} ${getStatusTextColor(contact.statusColor)} text-xs font-bold uppercase tracking-widest`}>
								{contact.statusText}
							</span>
                        </div>
                        <div class="space-y-1 mb-6">
                            <h3 class="text-xl font-bold">{contact.name}</h3>
                            <p class="text-base-content/70 font-medium">{contact.title} • {contact.company}</p>
                        </div>
                        <div class="divider my-0"></div>
                        <div class="flex items-center justify-between pt-4">
                            {#if contact.meeting}
                                <div class="flex items-center gap-2 text-sm text-base-content/60">
                                    <span class="material-symbols-outlined text-sm">calendar_today</span>
                                    <span>{contact.meeting}</span>
                                </div>
                            {:else if contact.location}
                                <div class="flex items-center gap-2 text-sm text-base-content/60">
                                    <span class="material-symbols-outlined text-sm">location_on</span>
                                    <span>{contact.location}</span>
                                </div>
                            {/if}
                            <button class={`btn btn-sm ${contact.action === 'Follow-up' ? 'btn-accent text-white' : 'btn-ghost bg-base-200 hover:bg-base-300'}`}>
                                {contact.action}
                            </button>
                        </div>
                    </div>
                </div>
            {/each}
        </div>
    </main>

    <!-- Bottom Navigation (Mobile) -->
    <nav class="md:hidden fixed bottom-0 left-0 right-0 z-50">
        <div class="bg-base-100/80 backdrop-blur-xl mx-6 mb-6 rounded-2xl shadow-lg flex justify-around items-center p-2">
            <a class="btn btn-primary btn-sm flex-col h-auto min-h-0 py-2 px-4 scale-110" href="#">
                <span class="material-symbols-outlined text-lg">contacts</span>
                <span class="text-[10px] font-semibold uppercase tracking-widest mt-1">Contacts</span>
            </a>
            <a class="btn btn-ghost btn-sm flex-col h-auto min-h-0 py-2 px-4 opacity-70" href="#">
                <span class="material-symbols-outlined text-lg">event_repeat</span>
                <span class="text-[10px] font-semibold uppercase tracking-widest mt-1">Follow-ups</span>
            </a>
            <a class="btn btn-ghost btn-sm flex-col h-auto min-h-0 py-2 px-4 opacity-70" href="#">
                <span class="material-symbols-outlined text-lg">auto_awesome</span>
                <span class="text-[10px] font-semibold uppercase tracking-widest mt-1">AI Insights</span>
            </a>
            <a class="btn btn-ghost btn-sm flex-col h-auto min-h-0 py-2 px-4 opacity-70" href="#">
                <span class="material-symbols-outlined text-lg">settings</span>
                <span class="text-[10px] font-semibold uppercase tracking-widest mt-1">Settings</span>
            </a>
        </div>
    </nav>

    <!-- Load Material Symbols -->
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet" />
</div>

<style></style>