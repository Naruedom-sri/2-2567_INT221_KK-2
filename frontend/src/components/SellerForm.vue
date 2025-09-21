<script setup>
import { computed, onBeforeUnmount, ref } from "vue";

const props = defineProps({
  submitting: { type: Boolean, default: false },
  statusErr: { default: null },
});
const emit = defineEmits(["submit", "cancel"]);

const seller = ref({
  nickname: "",
  contactEmail: "",
  password: "",
  fullname: "",
  mobile: "",
  bankAccount: "",
  bankName: "",
  nationalCard: "",
});

const cardPhotos = ref({
  frontFile: null,
  backFile: null,
  frontPreview: "",
  backPreview: "",
});

const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\w\s]).{8,}$/;
const isValid = computed(() => {
  const s = seller.value;
  return (
    s.nickname.trim() &&
    s.contactEmail.trim() &&
    passwordPattern.test(s.password) &&
    s.fullname.trim().length >= 4 &&
    s.fullname.trim().length <= 40 &&
    s.mobile.trim() &&
    s.bankAccount.trim() &&
    s.bankName.trim() &&
    s.nationalCard.trim() &&
    cardPhotos.value.frontFile &&
    cardPhotos.value.backFile
  );
});

function onFrontChange(e) {
  const file = e?.target?.files?.[0];
  if (!file) return;
  // Revoke previous preview to avoid memory leaks
  if (cardPhotos.value.frontPreview)
    URL.revokeObjectURL(cardPhotos.value.frontPreview);
  cardPhotos.value.frontFile = file;
  cardPhotos.value.frontPreview = URL.createObjectURL(file);
}

function onBackChange(e) {
  const file = e?.target?.files?.[0];
  if (!file) return;
  // Revoke previous preview to avoid memory leaks
  if (cardPhotos.value.backPreview)
    URL.revokeObjectURL(cardPhotos.value.backPreview);
  cardPhotos.value.backFile = file;
  cardPhotos.value.backPreview = URL.createObjectURL(file);
}

onBeforeUnmount(() => {
  if (cardPhotos.value.frontPreview)
    URL.revokeObjectURL(cardPhotos.value.frontPreview);
  if (cardPhotos.value.backPreview)
    URL.revokeObjectURL(cardPhotos.value.backPreview);
});

function onSubmit() {
  if (!isValid.value || props.submitting) return;
  emit("submit", {
    seller: { ...seller.value },
    files: {
      front: cardPhotos.value.frontFile,
      back: cardPhotos.value.backFile,
    },
  });
}
</script>

<template>
  <form class="space-y-4" @submit.prevent="onSubmit">
    <div class="flex flex-col">
      <label for="nickname" class="mb-1 flex gap-2 items-center"
        >Nickname
        <span><img src="../assets/imgs/asterisk.png" class="w-3" /></span
      ></label>
      <input
        class="border rounded px-3 py-2"
        type="text"
        id="nickname"
        v-model="seller.nickname"
      />
    </div>
    <div class="flex flex-col">
      <label for="sellerEmail" class="mb-1 flex gap-2 items-center"
        >Email <span><img src="../assets/imgs/asterisk.png" class="w-3" /></span
      ></label>
      <input
        class="border rounded px-3 py-2"
        type="email"
        id="sellerEmail"
        v-model="seller.contactEmail"
      />
      <p v-if="props.statusErr === 409" class="mt-1 text-xs text-red-500">
        Email Already Exists
      </p>
    </div>
    <div class="flex flex-col">
      <label for="sellerPassword" class="mb-1 flex gap-2 items-center"
        >Password
        <span><img src="../assets/imgs/asterisk.png" class="w-3" /></span
      ></label>
      <input
        class="border rounded px-3 py-2"
        type="password"
        id="sellerPassword"
        v-model="seller.password"
      />
      <small class="text-xs text-gray-500 mt-1">
        Minimum 8 chars, including upper, lower, number, and special character
      </small>
    </div>
    <div class="flex flex-col">
      <label for="sellerFullname" class="mb-1 flex gap-2 items-center"
        >Fullname
        <span><img src="../assets/imgs/asterisk.png" class="w-3" /></span
      ></label>
      <input
        class="border rounded px-3 py-2"
        type="text"
        id="sellerFullname"
        v-model="seller.fullname"
      />
    </div>
    <div class="flex flex-col">
      <label for="sellerMobile" class="mb-1 flex gap-2 items-center"
        >Mobile
        <span><img src="../assets/imgs/asterisk.png" class="w-3" /></span
      ></label>
      <input
        class="border rounded px-3 py-2"
        type="tel"
        id="sellerMobile"
        v-model="seller.mobile"
      />
    </div>
    <div class="flex flex-col">
      <label for="sellerBankAccount" class="mb-1 flex gap-2 items-center"
        >Bank Account No
        <span><img src="../assets/imgs/asterisk.png" class="w-3" /></span
      ></label>
      <input
        class="border rounded px-3 py-2"
        type="text"
        id="sellerBankAccount"
        v-model="seller.bankAccount"
      />
    </div>
    <div class="flex flex-col">
      <label for="sellerBankName" class="mb-1 flex gap-2 items-center"
        >Bank Name
        <span><img src="../assets/imgs/asterisk.png" class="w-3" /></span
      ></label>
      <input
        class="border rounded px-3 py-2"
        type="text"
        id="sellerBankName"
        v-model="seller.bankName"
      />
    </div>
    <div class="flex flex-col">
      <label for="sellerNationalCard" class="mb-1 flex gap-2 items-center"
        >National Card No
        <span><img src="../assets/imgs/asterisk.png" class="w-3" /></span
      ></label>
      <input
        class="border rounded px-3 py-2"
        type="text"
        id="sellerNationalCard"
        v-model="seller.nationalCard"
      />
    </div>
    <div class="flex flex-row gap-12">
      <label class="flex items-center"
        >National Card Photo
        <span><img src="../assets/imgs/asterisk.png" class="w-4" /></span
      ></label>
      <div class="flex flex-row gap-5">
        <label
          class="relative mx-auto w-50 h-30 border-1 border-black/10 rounded-xl flex items-center justify-center text-center text-black/60 bg-white cursor-pointer overflow-hidden"
          title="Upload front side photo"
        >
          <input
            id="sellerNationalCardFront"
            type="file"
            accept="image/*"
            class="hidden"
            @change="onFrontChange"
          />
          <img
            v-if="cardPhotos.frontPreview"
            :src="cardPhotos.frontPreview"
            alt="Front side preview"
            class="absolute inset-0 w-full h-full object-cover"
          />
          <span v-else class="pointer-events-none">Front side</span>
        </label>
        <label
          class="relative mx-auto w-50 h-30 border-1 border-black/10 rounded-xl flex items-center justify-center text-center text-black/60 bg-white cursor-pointer overflow-hidden"
          title="Upload back side photo"
        >
          <input
            id="sellerNationalCardBack"
            type="file"
            accept="image/*"
            class="hidden"
            @change="onBackChange"
          />
          <img
            v-if="cardPhotos.backPreview"
            :src="cardPhotos.backPreview"
            alt="Back side preview"
            class="absolute inset-0 w-full h-full object-cover"
          />
          <span v-else class="pointer-events-none">Back side</span>
        </label>
      </div>
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
