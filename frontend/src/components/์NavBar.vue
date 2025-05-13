<script setup>
import { ref } from "vue";
const isSearch = ref(false);
</script>

<template>
  <div
    class="container-header flex justify-between items-center w-full h-[60px] pl-10 pr-20 bg-[rgba(22,22,23,255)] text-white"
  >
    <div class="logo">
      <RouterLink :to="{ name: 'LandingPage' }">
        <img
          src="/src/assets/imgs/itbms-logo.png"
          alt="logo"
          class="w-40 h-20 object-cover"
        />
      </RouterLink>
    </div>
    <div class="link flex h-full text-center gap-3">
      <RouterLink
        :to="{ name: 'Home' }"
        class="w-20 flex justify-center items-center border-4 border-[rgba(22,22,23,255)] hover:border-b-blue-500 duration-200"
        >Home</RouterLink
      >
      <RouterLink
        :to="{ name: 'SaleItemsGallery' }"
        class="w-20 flex justify-center items-center border-4 border-[rgba(22,22,23,255)] hover:border-b-blue-500 duration-200"
        >Product</RouterLink
      >
      <button
        class="w-20 flex justify-center items-center border-4 border-[rgba(22,22,23,255)] hover:border-b-blue-500 duration-200"
      >
        Brand
      </button>
    </div>
    <div class="symbol flex gap-3.5">
      <div
        class="search-bar flex"
        :class="
          isSearch
            ? 'px-4 border border-white rounded-2xl duration-200 ease-in'
            : 'duration-100  ease-in'
        "
      >
        <input
          type="text"
          placeholder="Search...."
          class="w-0"
          :class="
            isSearch
              ? 'w-sm outline-0 text-white duration-200 ease-in'
              : 'duration-100 ease-in'
          "
        />
        <img
          @click="!isSearch ? (isSearch = true) : (isSearch = false)"
          src="/src/assets/imgs/search-symbol.png"
          alt="search-symbol"
          class="w-7 object-cover hover:cursor-pointer"
        />
      </div>
      <img
        src="/src/assets/imgs/account-symbol.png"
        alt="account"
        class="w-7 object-cover hover:cursor-pointer"
        :class="
          isSearch
            ? 'border border-[rgba(22,22,23,255)] rounded-2xl'
            : 'duration-100  ease-in'
        "
      />
      <img
        src="/src/assets/imgs/cart-symbol.png"
        alt="cart"
        class="w-7 object-cover hover:cursor-pointer"
        :class="
          isSearch
            ? 'border border-[rgba(22,22,23,255)] rounded-2xl'
            : 'duration-100  ease-in'
        "
      />
    </div>
  </div>
</template>

<style scoped></style>
