<script setup>
import { ref } from "vue";
// import BuyerForm from "../components/BuyerForm.vue";
// import SellerForm from "../components/SellerForm.vue";

const accountType = ref("buyer"); 
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
        :is="accountType === 'buyer' ? 'BuyerForm' : 'SellerForm'"
      />
    </div>
  </div>
</template>

<style></style>
