<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import BuyerForm from "../components/BuyerForm.vue";
import SellerForm from "../components/SellerForm.vue";
import { useSaleItemStatusStore } from "@/stores/SaleItemStatus";

const accountType = ref("buyer");
const isSubmitting = ref(false);
const router = useRouter();
const statusStore = useSaleItemStatusStore();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;

// helper to safely trim strings
const safeTrim = (v) => (v ?? "").toString().trim();

const handleSubmit = async (payload) => {
  // payload shape differs buyer vs seller; build body accordingly per requirement
  try {
    isSubmitting.value = true;
    statusStore.clearStatusAndMethod();

    if (accountType.value === "buyer") {
      const form = new FormData();
      form.append("role", "buyer");
      form.append("nickname", safeTrim(payload.nickname));
      form.append("email", safeTrim(payload.email));
      // keep password as entered (no trim); change if business requires
      form.append("password", payload.password ?? "");
      form.append("fullname", safeTrim(payload.fullname));
      const res = await fetch(`${BASE_API_DOMAIN}/v2/user/register`, {
        method: "POST",
        body: form,
      });
  statusStore.setStatusAndMethod("register", res.status);
  // Some backends may return 3xx after successful creation; treat <400 as success
  if (res.status >= 400) throw new Error("Register failed");
    } else {
      const form = new FormData();
      form.append("role", "seller");
      // payload.seller is a plain object from SellerForm, not a ref
      form.append("nickname", safeTrim(payload.seller.nickname));
      form.append("email", safeTrim(payload.seller.contactEmail));
      // keep password as entered (no trim)
      form.append("password", payload.seller.password ?? "");
      form.append("fullname", safeTrim(payload.seller.fullname));
      const sanitizedMobile = safeTrim(payload.seller.mobile).replace(/-/g, "");
      form.append("mobileNumber", sanitizedMobile);
      form.append("bankAccountNumber", safeTrim(payload.seller.bankAccount));
      form.append("bankName", safeTrim(payload.seller.bankName));
      form.append("nationalIdNumber", safeTrim(payload.seller.nationalCard));
      if (payload.files?.front) form.append("front", payload.files.front);
      if (payload.files?.back) form.append("back", payload.files.back);

      const res = await fetch(`${BASE_API_DOMAIN}/v2/user/register`, {
        method: "POST",
        body: form,
      });
  statusStore.setStatusAndMethod("register", res.status);
  if (res.status >= 400) throw new Error("Register failed");
    }
    // success: redirect to sale item gallery
    router.push({ name: "SaleItemsGallery" });
  } catch (e) {
    // stay on page; message shown via status store consumer globally
    console.error(e);
  } finally {
    isSubmitting.value = false;
  }
};

const handleCancel = () => router.push({ name: "SaleItemsGallery" });
</script>

<template>
  <div class="min-h-screen grid place-items-center bg-gray-0">
    <div class="w-full max-w-2xl rounded-2xl bg-white p-6">
      <RouterLink :to="{ name: 'SaleItemsGallery' }">
        <img
          src="/src/assets/imgs/close-symbol.png"
          alt="close symbol"
          class="w-7 hover:cursor-pointer"
        />
      </RouterLink>
      <h1 class="text-3xl font-semibold text-center mb-2">Register</h1>
      <p class="text-center text-gray-500 mb-6">Register as Buyer or Seller</p>

      <div class="flex rounded-xl overflow-hidden border mb-6">
        <button
          class="flex-1 py-2 text-sm font-medium"
          :class="
            accountType === 'buyer'
              ? 'bg-black text-white'
              : 'bg-white text-gray-700'
          "
          @click="accountType = 'buyer'"
        >
          Buyer
        </button>
        <button
          class="flex-1 py-2 text-sm font-medium"
          :class="
            accountType === 'seller'
              ? 'bg-black text-white'
              : 'bg-white text-gray-700'
          "
          @click="accountType = 'seller'"
        >
          Seller
        </button>
      </div>

      <component
        :is="accountType === 'buyer' ? BuyerForm : SellerForm"
        :submitting="isSubmitting"
        @submit="handleSubmit"
        @cancel="handleCancel"
      />
    </div>
  </div>
</template>

<style></style>
