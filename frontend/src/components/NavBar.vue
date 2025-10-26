<script setup>
import { onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { logoutUser } from "@/libs/userApi";
import { decodeToken } from "@/libs/jwtToken";
import { useStatusStore } from "@/stores/statusStore";
import { useCartStore } from "@/stores/cartStore";

const accessToken = localStorage.getItem("accessToken");
const decoded = decodeToken(accessToken);
const router = useRouter();
const route = useRoute();
const statusStore = useStatusStore();
const cartStore = useCartStore();
const cart = useCartStore();
const isSearch = ref(false);
const searchSaleItem = ref("");
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
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
    router.push({ name: "SaleItemsGallery" }).then(() => router.go(0));
  }
};

function goToProfile() {
  statusStore.clearEntityAndMethodAndStatusAndMessage();
  router.push({ name: "Profile" });
}

const logout = async () => {
  try {
    await logoutUser(`${BASE_API_DOMAIN}`, accessToken, router);
    localStorage.removeItem("accessToken")
    sessionStorage.clear();
    cartStore.clearCart();
    if (router.currentRoute.value.name !== "SaleItemsGallery") {
      router.push({ name: "SaleItemsGallery" });
    } else {
      router.replace({ name: "SaleItemsGallery" });
      router.go(0);
    }
  } catch (error) {
    console.log(error);
  }
};

onMounted(() => {
  const savedSearch = sessionStorage.getItem("searchContent");
  if (savedSearch) searchSaleItem.value = savedSearch;
});

// new: badge animation control
const badgeAnimate = ref(false);
let prevTotal = cart.totalItems || 0;
watch(
  () => cart.totalItems,
  (newVal) => {
    // animate only when increasing (add to cart)
    if (typeof newVal === "number" && newVal > prevTotal) {
      badgeAnimate.value = false; // reset to retrigger
      // nextTick not necessary here; short timeout to ensure class application
      setTimeout(() => {
        badgeAnimate.value = true;
        setTimeout(() => (badgeAnimate.value = false), 600); // duration matches CSS
      }, 20);
    }
    prevTotal = newVal;
  }
);
</script>

<template>
  <div class="container-header text-white text-xs">
    <div class="flex justify-center items-center gap-7 h-12">
      <div class="your-or ders flex justify-center w-28 h-full">
        <button
          @click="
            router.push({ name: 'OrderBuyer' }),
              statusStore.clearEntityAndMethodAndStatusAndMessage()
          "
          class="opacity-85 hover:opacity-100 cursor-pointer"
        >
          My Order
        </button>
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
        <button
          @click="
            statusStore.clearEntityAndMethodAndStatusAndMessage(),
              router.push({ name: 'SaleItemsGallery' })
          "
          class="w-20 flex justify-center items-center cursor-pointer opacity-85 hover:opacity-100 duration-200"
        >
          Store
        </button>
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

        <div class="flex items-center relative">
          <img
            @click="
              !cart.items.length ? null : router.push({ name: 'Cart' }),
                statusStore.clearEntityAndMethodAndStatusAndMessage()
            "
            src="/src/assets/imgs/cart-symbol.png"
            alt="cart"
            class="w-5 object-cover opacity-85 hover:opacity-100 hover:cursor-pointer"
            :class="{
              'opacity-40 cursor-not-allowed pointer-events-none':
                !cart.items.length,
              'hover:cursor-pointer': cart.items.length,
            }"
          />
          <span
            v-show="cart.totalItems !== 0"
            :class="[
              'itbms-badge flex items-center justify-center text-xs text-white',
              { 'itbms-badge-animate': badgeAnimate },
            ]"
            aria-live="polite"
          >
            {{ cart.totalItems }}
          </span>
        </div>

        <div class="flex justify-center items-center gap-2">
          <img
            src="/src/assets/imgs/account-symbol.png"
            alt="account"
            class="w-5 object-cover opacity-85 hover:opacity-100 hover:cursor-pointer"
            @click="goToProfile"
          />
          <p
            class="opacity-85 hover:opacity-100 hover:cursor-pointer"
            @click="goToProfile"
          >
            {{ decoded?.nickname }}
          </p>
        </div>
      </div>
      <div
        v-if="accessToken === null"
        class="register-login w-28 flex justify-center items-center gap-3"
      >
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
      <div v-else class="logout w-28 flex justify-center items-center gap-3">
        <button
          @click="logout"
          class="opacity-85 hover:opacity-100 duration-200 hover:cursor-pointer"
        >
          Logout
        </button>
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

<style scoped>
.itbms-badge {
  position: absolute;
  top: -6px;
  right: -10px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 9999px;
  background: linear-gradient(135deg, #ef4444, #dc2626); /* red gradient */
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.35);
  transform-origin: center;
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.itbms-badge-animate {
  animation: badge-pop 0.6s ease forwards;
}

@keyframes badge-pop {
  0% {
    transform: scale(0.6);
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
  }
  50% {
    transform: scale(1.25);
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
  }
  100% {
    transform: scale(1);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.25);
  }
}
</style>
