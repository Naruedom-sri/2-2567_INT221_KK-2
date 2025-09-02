<script setup>
import { computed, ref, watch } from "vue";
const props = defineProps({
  submitting: { type: Boolean, default: false },
  statusErr: { default: null },
});
const buyer = ref({
  nickname: "",
  email: "",
  password: "",
  fullname: "",
});

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

const emit = defineEmits(["submit", "cancel"]);

function onSubmit() {
  if (!isValid.value || props.submitting) return;
  emit("submit", { ...buyer.value, accountType: "BUYER" });
}
</script>

<template>
  <form class="space-y-4" @submit.prevent="onSubmit">
    <div class="flex flex-col">
      <label for="name" class="mb-1 flex gap-2 items-center"
        >Nickname
        <span><img src="../assets/imgs/asterisk.png" class="w-3" /></span
      ></label>
      <input
        class="border rounded px-3 py-2"
        type="text"
        id="name"
        v-model.trim="buyer.nickname"
      />
    </div>

    <div class="flex flex-col">
      <label for="email" class="mb-1 flex gap-2 items-center"
        >Email <span><img src="../assets/imgs/asterisk.png" class="w-3" /></span
      ></label>
      <input
        class="border rounded px-3 py-2"
        type="email"
        id="email"
        v-model.trim="buyer.email"
      />
      <p v-if="props.statusErr === 409" class="mt-1 text-xs text-red-500">
        Email Already Exists
      </p>
    </div>

    <div class="flex flex-col">
      <label for="password" class="mb-1 flex gap-2 items-center"
        >Password
        <span><img src="../assets/imgs/asterisk.png" class="w-3" /></span
      ></label>
      <input
        class="border rounded px-3 py-2"
        type="password"
        id="password"
        v-model.trim="buyer.password"
      />
      <small class="text-xs text-gray-500 mt-1">
        Minimum 8 chars, including upper, lower, number, and special character
      </small>
    </div>

    <div class="flex flex-col">
      <label for="fullname" class="mb-1 flex gap-2 items-center"
        >Fullname
        <span><img src="../assets/imgs/asterisk.png" class="w-3" /></span
      ></label>
      <input
        class="border rounded px-3 py-2"
        type="text"
        id="fullname"
        v-model.trim="buyer.fullname"
      />
    </div>

    <div class="flex justify-center gap-10">
      <button
        type="submit"
        class="mt-2 px-4 py-2 rounded bg-green-500 text-white hover:cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
        :disabled="!isValid || props.submitting"
      >
        {{ props.submitting ? "Submitting..." : "Submit" }}
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
