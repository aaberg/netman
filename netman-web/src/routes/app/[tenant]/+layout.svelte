<script lang="ts">
  import "../../../app.css"
  import { logout } from "$lib/security"
  import type { LayoutProps } from "./$types"

  let { children, data }: LayoutProps = $props()
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
        <a class="btn btn-ghost btn-sm rounded-lg font-bold" href="/app/{data.tenant}/contacts">Contacts</a>
        <a class="btn btn-ghost btn-sm rounded-lg opacity-60" href="/app/{data.tenant}/tasks">Follow-ups</a>
        <a class="btn btn-ghost btn-sm rounded-lg opacity-60" href="#">AI Insights</a>
      </nav>
    </div>
  </header>

  <!-- Main Content -->
  <main class="max-w-7xl mx-auto px-6 pt-24 pb-32">
  {@render children()}
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
</div>



<!--<div class="navbar bg-base-100 shadow-sm">-->
<!--  <div class="flex-1">-->
<!--    <label for="my-drawer" class="btn btn-ghost drawer-button lg:hidden">-->
<!--      <svg-->
<!--        xmlns="http://www.w3.org/2000/svg"-->
<!--        fill="none"-->
<!--        viewBox="0 0 24 24"-->
<!--        class="inline-block h-5 w-5 stroke-current"-->
<!--      >-->
<!--        <path-->
<!--          stroke-linecap="round"-->
<!--          stroke-linejoin="round"-->
<!--          stroke-width="2"-->
<!--          d="M4 6h16M4 12h16M4 18h16"-->
<!--        ></path>-->
<!--      </svg>-->
<!--    </label>-->
<!--  </div>-->
<!--  <div class="flex-1">-->
<!--    <div>Netværke</div>-->
<!--  </div>-->
<!--</div>-->

<!--<div class="drawer lg:drawer-open">-->
<!--  <input id="my-drawer" type="checkbox" class="drawer-toggle" />-->
<!--  <div class="drawer-content flex flex-col items-center p-4">-->
<!--    {@render children()}-->
<!--  </div>-->
<!--  <div class="drawer-side">-->
<!--    <label for="my-drawer" aria-label="close sidebar" class="drawer-overlay"></label>-->
<!--    <div class="bg-base-200 min-h-full w-80 p-4">-->
<!--      <ul class="menu text-base-content">-->
<!--        <li><a href="/app/{data.tenant}/dashboard">Dashboard</a></li>-->
<!--        <li><a href="/app/{data.tenant}/contacts">Contact list</a></li>-->
<!--        <li><a href="/app/{data.tenant}/tasks">Follow-up hub</a></li>-->
<!--        <li><a href="/app/{data.tenant}/settings">Settings</a></li>-->
<!--      </ul>-->
<!--      <div class="divider"></div>-->
<!--      <div class="pl-4">-->
<!--        <div class="avatar avatar-placeholder">-->
<!--          <div class="bg-neutral text-neutral-content w-8 rounded-full">-->
<!--            <span class="text-xs">{data.profile.initials}</span>-->
<!--          </div>-->
<!--        </div>-->
<!--        {data.profile.name}-->
<!--      </div>-->

<!--      <ul class="menu menu-horizontal text-base-content">-->
<!--        <li><a href="/app/{data.tenant}/profile">Profile</a></li>-->
<!--        <li><button onclick={() => logout()}>Logout</button></li>-->
<!--      </ul>-->
<!--    </div>-->
<!--  </div>-->
<!--</div>-->
