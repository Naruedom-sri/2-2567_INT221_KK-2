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

const handleSubmit = async (payload) => {
  // payload shape differs buyer vs seller; build body accordingly per requirement
  try {
    isSubmitting.value = true;
    statusStore.clearStatusAndMethod();

    if (accountType.value === "buyer") {
      const body = {
        accountType: "BUYER",
        nickname: payload.nickname,
        email: payload.email,
        password: payload.password,
        fullname: payload.fullname,
      };
      const res = await fetch(`${BASE_API_DOMAIN}/v1/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
      });
  statusStore.setStatusAndMethod("register", res.status);
      if (res.status !== 201) throw new Error("Register failed");
    } else {
      // seller: include additional fields and photos
      const form = new FormData();
      form.append("accountType", "SELLER");
      form.append("nickname", payload.seller.value.nickname);
      form.append("email", payload.seller.value.contactEmail);
      form.append("password", payload.seller.value.password);
      form.append("fullname", payload.seller.value.fullname);
      form.append("mobile", payload.seller.value.mobile);
      form.append("bankAccount", payload.seller.value.bankAccount);
      form.append("bankName", payload.seller.value.bankName);
      form.append("nationalId", payload.seller.value.nationalCard);
      if (payload.files?.front) form.append("nationalIdFront", payload.files.front);
      if (payload.files?.back) form.append("nationalIdBack", payload.files.back);

      const res = await fetch(`${BASE_API_DOMAIN}/v1/register`, {
        method: "POST",
        body: form,
      });
  statusStore.setStatusAndMethod("register", res.status);
      if (res.status !== 201) throw new Error("Register failed");
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
      <RouterLink 
        :to="{ name: 'SaleItemsGallery' }"
      >
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
