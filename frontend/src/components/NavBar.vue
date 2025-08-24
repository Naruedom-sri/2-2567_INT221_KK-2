<script setup>
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
const router = useRouter();
const route = useRoute();
const isSearch = ref(false);
const searchSaleItem = ref("");
const emit = defineEmits(["searchSaleItem"]);

const handleSearchClick = (isClear) => {
  if (isClear) {
    searchSaleItem.value = "";
    sessionStorage.setItem("searchContent", searchSaleItem.value);
  } else if (route.name === "SaleItemsGallery") {
    emit("searchSaleItem", searchSaleItem.value);
    isSearch.value = !isSearch.value;
  } else {
    sessionStorage.setItem("searchContent", searchSaleItem.value);
    router.push({ name: "SaleItemsGallery" });
  }
};

onMounted(() => {
  const savedSearch = sessionStorage.getItem("searchContent");
  if (savedSearch) searchSaleItem.value = savedSearch;
});
</script>

<template>
  <div class="container-header text-white text-xs">
    <div class="flex justify-center items-center gap-7 h-12">
      <div class="logo w-56 flex justify-end ">
        <img
          @click="router.push({ name: 'LandingPage' })"
          src="/src/assets/imgs/icons/itbms-logo.png"
          alt="logo"
          class="w-6 object-cover opacity-85 hover:opacity-100"
        />
      </div>
      <div class="link flex h-full gap-3 ">
        <RouterLink
          :to="{ name: 'Home' }"
          class="w-20 flex justify-center items-center opacity-85 hover:opacity-100 duration-200"
          >Home</RouterLink
        >
        <RouterLink
          :to="{ name: 'SaleItemsGallery' }"
          class="w-20 flex justify-center items-center opacity-85 hover:opacity-100  duration-200"
          >Store</RouterLink
        >
        <RouterLink
          :to="{ name: 'NotFoundPage' }"
          class="w-20 flex justify-center items-center opacity-85 hover:opacity-100  duration-200"
        >
          Accessories
        </RouterLink>
        <RouterLink
          :to="{ name: 'NotFoundPage' }"
          class="w-20 flex justify-center items-center opacity-85 hover:opacity-100  duration-200"
        >
          Support
        </RouterLink>
        <RouterLink
          :to="{ name: 'AboutUs' }"
          class="w-20 flex justify-center items-center opacity-85 hover:opacity-100  duration-200"
        >
          About Us
        </RouterLink>
      </div>
      <div class="symbol w-56 flex items-center gap-3.5 ">
        <img
          @click="isSearch = !isSearch"
          src="/src/assets/imgs/search-symbol.png"
          alt="search-symbol"
          class="w-5 object-cover opacity-85 hover:opacity-100  hover:cursor-pointer"
        />
      </div>
      <RouterLink :to="{ name: 'Register' }">
        <img
          src="/src/assets/imgs/account-symbol.png"
          alt="account"
          class="w-7 object-cover hover:cursor-pointer"
        />
      </RouterLink>
      <img
        src="/src/assets/imgs/cart-symbol.png"
        alt="cart"
        class="w-7 object-cover hover:cursor-pointer"
      />
        <img
          src="/src/assets/imgs/account-symbol.png"
          alt="account"
          class="w-5 object-cover opacity-85 hover:opacity-100  hover:cursor-pointer"
        />
        <img
          src="/src/assets/imgs/cart-symbol.png"
          alt="cart"
          class="w-5 object-cover opacity-85 hover:opacity-100  hover:cursor-pointer"
        />
      </div>
    </div>
    <div
      v-if="isSearch"
      class="box-search absolute w-full h-screen backdrop-blur-lg z-50"
    >
      <div class="bg-[rgba(22,22,23,255)] h-1/2">
        <div class="w-lg mx-auto py-4 flex justify-between items-center gap-2">
          <input
            @keyup.enter="handleSearchClick(false)"
            v-model.trim="searchSaleItem"
            type="text"
            placeholder="Search"
            class="itbms-search-text w-full outline-none text-2xl"
          />
          <button
            @click="handleSearchClick(true)"
            class="itbms--search-clear-button w-7 h-7 rounded-full bg-white text-black hover:text-white hover:bg-[#0d47a1]"
          >
            x
          </button>
        </div>
        <div class="flex justify-center">
          <div class="w-96">
            <h1 class="text-center border-r">Model</h1>
          </div>
          <div class="w-96">
            <h1 class="text-center">Color</h1>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
