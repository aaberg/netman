<script lang="ts">
    import {createProfile} from "$lib/profile";
    import {goto} from "$app/navigation";

    let name = $state("")
    let initials = $state("")

    function submit() {
        createProfile(name, initials).then(() => {
            goto("/flow/authorization/continue")
        })
    }

</script>

<div class="hero min-h-screen">
    <div class="hero-content text-center">
        <div class="max-w-lg">
            <h1 class="text-2xl mb-8">We just need a little more information</h1>
            <div>
                <div class="mb-4">
                    <label class="floating-label">
                        <input type="text" placeholder="Your name" bind:value={name} class="input w-full input-md"/>
                        <span>Your name</span>
                    </label>
                </div>
                <div class="mb-8">
                <label class="floating-label">
                        <input type="text" placeholder="Your initials" bind:value={initials} class="input input-md w-full"/>
                        <span>Your initials</span>
                    </label>
                </div>

                <button class="btn btn-primary" onclick={submit}>Submit</button>

            </div>
        </div>
    </div>
</div>