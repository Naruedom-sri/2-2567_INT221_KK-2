<script setup>
import { ref } from "vue";

// local state for the buyer form
const buyer = ref({
  name: "",
  email: "",
  password: "",
  fullname: "",
});

// optional: emit submit event to parent if needed later
const emit = defineEmits(["submit", "cancel"]);
// const onSubmit = () => emit('submit', { ...buyer })

function onSubmit() {
  console.log("Monkey")
  emit("submit", { ...buyer });
}
</script>

<template>
  <form class="space-y-4">
    <div class="flex flex-col">
      <label for="name" class="mb-1">Nickname</label>
      <input
        class="border rounded px-3 py-2"
        type="text"
        id="name"
        v-model="buyer.name"
      />
    </div>

    <div class="flex flex-col">
      <label for="email" class="mb-1">Email</label>
      <input
        class="border rounded px-3 py-2"
        type="email"
        id="email"
        v-model="buyer.email"
      />
    </div>

    <div class="flex flex-col">
      <label for="password" class="mb-1">Password</label>
      <input
        class="border rounded px-3 py-2"
        type="password"
        id="password"
        v-model="buyer.password"
      />
    </div>

    <div class="flex flex-col">
      <label for="fullname" class="mb-1">Fullname</label>
      <input
        class="border rounded px-3 py-2"
        type="text"
        id="fullname"
        v-model="buyer.fullname"
      />
    </div>

    <div class="flex justify-center gap-10">
    <button
      type="button"
      class="mt-2 px-4 py-2 rounded bg-green-500 text-white hover:cursor-pointer"
      @click="onSubmit"
    >
      Submit
    </button>
    <button
      type="button"
      class="mt-2 px-4 py-2 rounded bg-black/30 text-white hover:cursor-pointer"
      @click="emit('cancel')"
    >
      Cancel
    </button>
    </div>
    
  </form>
</template>

<style scoped></style>
