<script setup>
import { ref } from "vue";

// Emits for parent consumers
const emit = defineEmits(["submit", "cancel"]);

// Seller form state
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

// National card photo state
const cardPhotos = ref({
  frontFile: null,
  backFile: null,
  frontPreview: "",
  backPreview: "",
});

function onFrontChange(e) {
  const file = e?.target?.files?.[0];
  if (!file) return;
  cardPhotos.frontFile = file;
  cardPhotos.frontPreview = URL.createObjectURL(file);
}

function onBackChange(e) {
  const file = e?.target?.files?.[0];
  if (!file) return;
  cardPhotos.backFile = file;
  cardPhotos.backPreview = URL.createObjectURL(file);
}

function onSubmit() {
  // Minimal emit with current form values and files
  emit("submit", {
    seller,
    files: { front: cardPhotos.frontFile, back: cardPhotos.backFile },
  });
}
</script>

<template>
  <form class="space-y-4">
    <div class="flex flex-col">
      <label for="nickname" class="mb-1">Nickname</label>
      <input
        class="border rounded px-3 py-2"
        type="text"
        id="nickname"
        v-model="seller.nickname"
      />
    </div>
    <div class="flex flex-col">
      <label for="sellerEmail" class="mb-1">Email</label>
      <input
        class="border rounded px-3 py-2"
        type="email"
        id="sellerEmail"
        v-model="seller.contactEmail"
      />
    </div>
    <div class="flex flex-col">
      <label for="sellerPassword" class="mb-1">Password</label>
      <input
        class="border rounded px-3 py-2"
        type="password"
        id="sellerPassword"
        v-model="seller.password"
      />
    </div>
    <div class="flex flex-col">
      <label for="sellerFullname" class="mb-1">Fullname</label>
      <input
        class="border rounded px-3 py-2"
        type="text"
        id="sellerFullname"
        v-model="seller.fullname"
      />
    </div>
    <div class="flex flex-col">
      <label for="sellerMobile" class="mb-1">Mobile</label>
      <input
        class="border rounded px-3 py-2"
        type="tel"
        id="sellerMobile"
        v-model="seller.mobile"
      />
    </div>
    <div class="flex flex-col">
      <label for="sellerBankAccount" class="mb-1">Bank Account No</label>
      <input
        class="border rounded px-3 py-2"
        type="text"
        id="sellerBankAccount"
        v-model="seller.bankAccount"
      />
    </div>
    <div class="flex flex-col">
      <label for="sellerBankName" class="mb-1">Bank Name</label>
      <input
        class="border rounded px-3 py-2"
        type="text"
        id="sellerBankName"
        v-model="seller.bankName"
      />
    </div>
    <div class="flex flex-col">
      <label for="sellerNationalCard" class="mb-1">National Card No</label>
      <input
        class="border rounded px-3 py-2"
        type="text"
        id="sellerNationalCard"
        v-model="seller.nationalCard"
      />
    </div>
    <div class="flex flex-row gap-12">
      <label class="flex items-center">National Card Photo</label>
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
