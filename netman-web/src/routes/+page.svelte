<script lang="ts">
  import { isAuthenticated } from "$lib/security"
  import { onMount } from "svelte"

  let authenticated: boolean = false

  onMount(async () => {
    authenticated = await isAuthenticated()
  })
</script>

<!-- Top Navigation Bar with CTA -->
<nav class="bg-gray-900 border-b border-gray-800 sticky top-0 z-50">
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
    <div class="flex justify-between items-center h-16">
      <div class="flex items-center">
        <img src="/netvaerke-logo.jpg" alt="Netværke Logo" class="h-10 w-auto rounded">
        <span class="ml-3 text-xl font-semibold text-white">netværke</span>
      </div>
      <div class="flex items-center">
        <a href="/flow/authentication" class="btn btn-primary text-white font-semibold py-2 px-6 rounded-full hover:scale-105 transition-transform duration-300">
          {#if authenticated}
            Go to Dashboard
          {:else}
            Login
          {/if}
        </a>
      </div>
    </div>
  </div>
</nav>

<!-- Hero Section -->
<section class="py-16 lg:py-24 bg-gray-900">
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
    <div class="text-center mb-12">
      <h1 class="text-4xl lg:text-6xl font-bold text-white mb-6">
        Welcome to <span class="text-blue-400">netværke</span>
      </h1>
      <p class="text-xl text-gray-300 max-w-3xl mx-auto mb-8">
        The intelligent network manager designed for professionals who value meaningful connections
      </p>
    </div>
    
    <!-- Features Grid -->
    <div class="grid md:grid-cols-3 gap-8 mb-16">
      <div class="feature-card bg-gray-800 p-8 rounded-xl border border-gray-700">
        <div class="w-16 h-16 bg-blue-900/30 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.653-.125-1.273-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.653.125-1.273.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
          </svg>
        </div>
        <h3 class="text-xl font-semibold text-white mb-2 text-center">Smart Contact Management</h3>
        <p class="text-gray-300 text-center">Organize your professional network with intelligent tagging and search</p>
      </div>

      <div class="feature-card bg-gray-800 p-8 rounded-xl border border-gray-700">
        <div class="w-16 h-16 bg-blue-900/30 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 01-2-2H9a2 2 0 01-2 2v14a2 2 0 012 2z"></path>
          </svg>
        </div>
        <h3 class="text-xl font-semibold text-white mb-2 text-center">Follow-up Reminders</h3>
        <p class="text-gray-300 text-center">Never miss an important connection with automated follow-up tracking</p>
      </div>

      <div class="feature-card bg-gray-800 p-8 rounded-xl border border-gray-700">
        <div class="w-16 h-16 bg-blue-900/30 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path>
          </svg>
        </div>
        <h3 class="text-xl font-semibold text-white mb-2 text-center">Insightful Analytics</h3>
        <p class="text-gray-300 text-center">Gain valuable insights about your network growth and engagement patterns</p>
      </div>
    </div>
  </div>
</section>

<!-- Value Proposition Section -->
<section class="py-20 bg-gray-900">
  <div class="max-w-6xl mx-auto px-4 text-center">
    <h2 class="text-3xl lg:text-4xl font-bold text-white mb-4">
      Build <span class="text-blue-400">Meaningful</span> Professional Relationships
    </h2>
    <p class="text-xl text-gray-300 mb-12 max-w-3xl mx-auto">
      netværke helps you nurture your professional network with ease, ensuring you stay connected with the people who matter most to your career growth.
    </p>

    <div class="grid md:grid-cols-3 gap-8">
      <div class="bg-gray-800 p-8 rounded-xl hover:shadow-lg transition-shadow duration-300 border border-gray-700">
        <div class="w-16 h-16 bg-blue-900/30 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
          </svg>
        </div>
        <h3 class="text-xl font-semibold text-white mb-2">Contact Organization</h3>
        <p class="text-gray-300">Categorize and tag contacts for easy retrieval and management</p>
      </div>

      <div class="bg-gray-800 p-8 rounded-xl hover:shadow-lg transition-shadow duration-300 border border-gray-700">
        <div class="w-16 h-16 bg-blue-900/30 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
          </svg>
        </div>
        <h3 class="text-xl font-semibold text-white mb-2">Follow-up Tracking</h3>
        <p class="text-gray-300">Set reminders and track your follow-up conversations effortlessly</p>
      </div>

      <div class="bg-gray-800 p-8 rounded-xl hover:shadow-lg transition-shadow duration-300 border border-gray-700">
        <div class="w-16 h-16 bg-blue-900/30 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 01-2-2H9a2 2 0 01-2 2v14a2 2 0 012 2z"></path>
          </svg>
        </div>
        <h3 class="text-xl font-semibold text-white mb-2">Network Analytics</h3>
        <p class="text-gray-300">Get insights into your network growth and engagement patterns</p>
      </div>
    </div>
  </div>
</section>

<!-- Call to Action Section -->
<section class="py-16 bg-gradient-to-r from-blue-600 to-indigo-700">
  <div class="max-w-4xl mx-auto px-4 text-center">
    <h2 class="text-3xl lg:text-4xl font-bold text-white mb-6">
      Ready to transform your professional networking?
    </h2>
    <p class="text-xl text-blue-100 mb-8 max-w-2xl mx-auto">
      Join netværke today and start building meaningful relationships that drive your career forward.
    </p>
    <a href="/flow/authentication" class="btn btn-lg bg-white text-blue-600 hover:bg-gray-100 font-semibold py-4 px-10 rounded-full text-lg transition-transform duration-300 hover:scale-105">
      {#if authenticated}
        Go to Dashboard
      {:else}
        Sign Up Free
      {/if}
    </a>
  </div>
</section>

<style>
  .feature-card {
    transition: transform 0.3s ease, box-shadow 0.3s ease;
  }
  
  .feature-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.3), 0 10px 10px -5px rgba(0, 0, 0, 0.1);
  }
</style>