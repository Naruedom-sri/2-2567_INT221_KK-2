<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import BuyerForm from "../components/BuyerForm.vue";
import SellerForm from "../components/SellerForm.vue";
import { useStatusStore } from "@/stores/statusStore";
const accountType = ref("buyer");
const isSubmitting = ref(false);
const router = useRouter();
const statusStore = useStatusStore();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;

const safeTrim = (v) => (v ?? "").toString().trim();

const handleSubmit = async (payload) => {
  try {
    isSubmitting.value = true;
    statusStore.clearEntityAndMethodAndStatusAndMessage();

    if (accountType.value === "buyer") {
      const form = new FormData();
      form.append("role", "buyer");
      form.append("nickname", safeTrim(payload.nickname));
      form.append("email", safeTrim(payload.email));
      form.append("password", safeTrim(payload.password));
      form.append("fullName", safeTrim(payload.fullname));
      const res = await fetch(`${BASE_API_DOMAIN}/v2/users/register`, {
        method: "POST",
        body: form,
      });
      if (res.status !== 201) {
        statusStore.setEntityAndMethodAndStatusAndMessage(
          "user",
          "register",
          res.status,
          "Register failed."
        );
        throw new Error("Register failed");
      }
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "user",
        "register",
        res.status,
        "The user account has been successfully registered."
      );
    } else {
      const form = new FormData();
      form.append("role", "seller");
      form.append("nickname", safeTrim(payload.seller.nickname));
      form.append("email", safeTrim(payload.seller.contactEmail));
      form.append("password", safeTrim(payload.seller.password));
      form.append("fullName", safeTrim(payload.seller.fullname));
      const sanitizedMobile = safeTrim(payload.seller.mobile).replace(/-/g, "");
      form.append("mobileNumber", sanitizedMobile);
      form.append("bankAccountNumber", safeTrim(payload.seller.bankAccount));
      form.append("bankName", safeTrim(payload.seller.bankName));
      form.append("nationalIdNumber", safeTrim(payload.seller.nationalCard));
      if (payload.files?.front) form.append("front", payload.files.front);
      if (payload.files?.back) form.append("back", payload.files.back);

      const res = await fetch(`${BASE_API_DOMAIN}/v2/users/register`, {
        method: "POST",
        body: form,
      });
      if (res.status !== 201) {
        statusStore.setEntityAndMethodAndStatusAndMessage(
          "user",
          "register",
          res.status,
          "Register failed."
        );
        throw new Error("Register failed");
      }
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "user",
        "register",
        res.status,
        "The user account has been successfully registered."
      );
    }
    router.push({ name: "SaleItemsGallery" });
  } catch (e) {
    console.error(e);
    isSubmitting.value = false;
  }
};

const handleCancel = () => {
  statusStore.clearEntityAndMethodAndStatusAndMessage();
  router.back();
};
</script>

<template>
  <div class="min-h-screen grid place-items-center bg-gray-0">
    <div class="w-full max-w-2xl rounded-2xl bg-white p-6">
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
        :statusErr="statusStore.getStatus()"
        @submit="handleSubmit"
        @cancel="handleCancel"
      />
    </div>
  </div>
</template>

<style></style>
