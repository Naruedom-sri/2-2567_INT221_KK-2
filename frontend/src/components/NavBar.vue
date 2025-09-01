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
    emit("searchSaleItem", searchSaleItem.value);
    isSearch.value = !isSearch.value;
  } else if (route.name === "SaleItemsGallery") {
    emit("searchSaleItem", searchSaleItem.value, "SaleItemsGallery");
    isSearch.value = !isSearch.value;
  } else {
    sessionStorage.setItem("searchContent", searchSaleItem.value);
    sessionStorage.setItem("indexPage", String(0));
    sessionStorage.setItem("tempIndexPage", String(0));
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
      <div class="empty-element w-28 h-full">
        <!-- Empty element for spacing -->
      </div>
      <div class="logo w-56 flex justify-end">
        <img
          @click="router.push({ name: 'LandingPage' })"
          src="/src/assets/imgs/icons/itbms-logo.png"
          alt="logo"
          class="w-6 object-cover opacity-85 hover:opacity-100"
        />
      </div>
      <div class="link flex h-full gap-3">
        <RouterLink
          :to="{ name: 'Home' }"
          class="w-20 flex justify-center items-center opacity-85 hover:opacity-100 duration-200"
          >Home</RouterLink
        >
        <RouterLink
          :to="{ name: 'SaleItemsGallery' }"
          class="w-20 flex justify-center items-center opacity-85 hover:opacity-100 duration-200"
          >Store</RouterLink
        >
        <RouterLink
          :to="{ name: 'NotFoundPage' }"
          class="w-20 flex justify-center items-center opacity-85 hover:opacity-100 duration-200"
        >
          Accessories
        </RouterLink>
        <RouterLink
          :to="{ name: 'NotFoundPage' }"
          class="w-20 flex justify-center items-center opacity-85 hover:opacity-100 duration-200"
        >
          Support
        </RouterLink>
        <RouterLink
          :to="{ name: 'AboutUs' }"
          class="w-20 flex justify-center items-center opacity-85 hover:opacity-100 duration-200"
        >
          About Us
        </RouterLink>
      </div>
      <div class="symbol w-56 flex items-center gap-7">
        <img
          @click="isSearch = !isSearch"
          src="/src/assets/imgs/search-symbol.png"
          alt="search-symbol"
          class="w-5 object-cover opacity-85 hover:opacity-100 hover:cursor-pointer"
        />
        <img
          src="/src/assets/imgs/account-symbol.png"
          alt="account"
          class="w-5 object-cover opacity-85 hover:opacity-100 hover:cursor-pointer"
        />
        <img
          src="/src/assets/imgs/cart-symbol.png"
          alt="cart"
          class="w-5 object-cover opacity-85 hover:opacity-100 hover:cursor-pointer"
        />
      </div>
      <div class="register-login w-28 flex justify-center items-center gap-3">
        <RouterLink
          :to="{ name: 'Login' }"
          class="opacity-85 hover:opacity-100 duration-200"
          >Login</RouterLink
        >
        <p>/</p>
        <RouterLink
          :to="{ name: 'Register' }"
          class="itbms-register-button opacity-85 hover:opacity-100 duration-200"
        >
          Signup
        </RouterLink>
      </div>
    </div>
  </div>
  <div
    v-if="isSearch"
    class="box-search absolute w-full h-screen backdrop-blur-lg text-xs z-50 text-white"
  >
    <div
      class="bg-black transition-all duration-300"
      :class="[isSearch ? 'h-1/2' : 'h-0']"
    >
      <div class="w-md mx-auto flex flex-col items-center">
        <div class="w-full py-10 flex justify-between items-center gap-2">
          <input
            @keyup.enter="handleSearchClick(false)"
            v-model.trim="searchSaleItem"
            type="text"
            placeholder="Search itbms.com"
            class="itbms-search-text w-full outline-none text-2xl"
          />
          <button
            @click="handleSearchClick(true)"
            class="itbms--search-clear-button w-7 h-7 rounded-full bg-white text-black hover:text-white hover:bg-[#0d47a1]"
          >
            x
          </button>
        </div>
        <div class="self-start text-white/70 space-y-4">
          <h1>Popular search</h1>
          <div class="space-y-4 mx-2">
            <p class="hover:text-white duration-200 cursor-pointer">
              > iPhone 16 Pro Max
            </p>
            <p class="hover:text-white duration-200 cursor-pointer">
              > iPhone 15 Pro
            </p>
            <p class="hover:text-white duration-200 cursor-pointer">
              > iPhone 13
            </p>
            <p class="hover:text-white duration-200 cursor-pointer">
              > iPhone 14 Plus
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
