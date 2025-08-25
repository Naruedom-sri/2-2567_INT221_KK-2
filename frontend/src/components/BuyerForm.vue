<script setup>
import { computed, ref } from "vue";

// Props: parent can indicate submitting state to disable the button and prevent duplicates
const props = defineProps({
  submitting: { type: Boolean, default: false },
});

// local state for the buyer form
const buyer = ref({
  nickname: "",
  email: "",
  password: "",
  fullname: "",
});

// validation rules
const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\w\s]).{8,}$/;
const isValid = computed(() => {
  const b = buyer.value;
  return (
    b.nickname.trim().length > 0 &&
    b.email.trim().length > 0 &&
    passwordPattern.test(b.password) &&
    b.fullname.trim().length >= 4 &&
    b.fullname.trim().length <= 40
  );
});

// Emits to parent
const emit = defineEmits(["submit", "cancel"]);

function onSubmit() {
  if (!isValid.value || props.submitting) return;
  // emit plain object
  emit("submit", { ...buyer.value, accountType: "BUYER" });
}
</script>

<template>
  <form class="space-y-4" @submit.prevent="onSubmit">
    <div class="flex flex-col">
      <label for="name" class="mb-1">Nickname</label>
      <input
        class="border rounded px-3 py-2"
        type="text"
        id="name"
        v-model="buyer.nickname"
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
      <small class="text-xs text-gray-500 mt-1">
        Minimum 8 chars, including upper, lower, number, and special character
      </small>
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
        type="submit"
        class="mt-2 px-4 py-2 rounded bg-green-500 text-white hover:cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
        :disabled="!isValid || props.submitting"
      >
        {{ props.submitting ? 'Submitting...' : 'Submit' }}
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
