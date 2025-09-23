<script setup>
import { onMounted, computed } from "vue";
import { useUserStore } from "@/stores/userStore";
import { getUserById } from "@/libs/userApi";
import { useTokenStore } from "@/stores/tokenStore";
const userStore = useUserStore();
const tokenStore = useTokenStore();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;

const isSeller = computed(() => userStore.role.toLowerCase() === "seller");
// const isBuyer = computed(() => userStore.role.toLowerCase() === "buyer");

const fetchUserData = async () => {
  if (tokenStore.getAccessToken() === null) {
    try {
      const data = await refreshAccessToken(`${BASE_API_DOMAIN}`);
      const decoded = jwtDecode(data.access_token);
      tokenStore.setAccessToken(data.access_token);
      tokenStore.setDecode(decoded);
    } catch (e) {
      console.log(e);
      router.push({ name: "Login" });
    }
  }

  try {
    const userData = await getUserById(
      BASE_API_DOMAIN,
      tokenStore.getDecode().jti,
      tokenStore.getAccessToken()
    );
    userStore.setUser(userData);
  } catch (error) {
    console.error("Failed to fetch user:", error);
  }
};

onMounted(() => {
  fetchUserData();
});
</script>

<template>
  <div class="min-h-screen grid place-items-center bg-gray-0">
    <div class="w-full max-w-xl rounded-2xl bg-white p-5 relative">
      <label
        class="absolute top-0 left-1/2 transform -translate-x-1/2 -translate-y-1/2"
      >
        <img
          src="/src/assets/imgs/account-symbol4.png"
          class="w-20 h-20 rounded-full border-0 border-white object-cover"
        />
      </label>
      <div class="text-black">
        <div class="p-5 text-center">
          <p><strong>Nickname:</strong> {{ userStore.nickname }}</p>
          <p><strong>Email:</strong> {{ userStore.email }}</p>
          <p><strong>Fullname:</strong> {{ userStore.fullname }}</p>
          <p><strong>Type:</strong> {{ userStore.role }}</p>
          <div class="mt-4">
            <router-link :to="{ name: 'EditProfile' }"
              ><button
                class="border-2 border-gray-400 rounded-md px-3 py-1 bg-gray-400 text-white hover:bg-gray-950 hover:border-gray-950"
              >
                Edit Profile
              </button></router-link
            >
          </div>
        </div>

        <div v-if="isSeller">
          <p>Mobile: {{ userStore.sellerMobileNumber }}</p>
          <p>Bank Account No: {{ userStore.sellerBankAccountNumber }}</p>
          <p>Bank Name: {{ userStore.sellerBankName }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
