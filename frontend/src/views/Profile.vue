<script setup>
import { onMounted } from "vue";
import { useUserStore } from "@/stores/userStore";
import { getUserById } from "@/libs/userApi";
import { useRouter } from "vue-router";
import { decodeToken } from "@/libs/jwtToken";
import Notification from "@/components/Notification.vue";
import { useStatusStore } from "@/stores/statusStore";

const router = useRouter();
const userStore = useUserStore();
const statusStore = useStatusStore();
const accessToken = localStorage.getItem("accessToken");
const decoded = decodeToken(accessToken);
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;

const closePage = () => {
  statusStore.clearEntityAndMethodAndStatusAndMessage();
  router.push({ name: "SaleItemsGallery" });
};

const fetchUserData = async () => {
  try {
    const userData = await getUserById(
      BASE_API_DOMAIN,
      decoded.jti,
      accessToken
    );
    userStore.setUser(userData);
  } catch (error) {
    console.error(error);
  }
};

const maskShowMiddleThreeBeforeLast = (value) => {
  if (value === null || value === undefined) return "";
  const s = String(value);
  const len = s.length;
  if (len < 4) {
    return "x".repeat(len);
  }
  const prefixLen = Math.max(0, len - 4); // number of leading x's
  const prefix = "x".repeat(prefixLen);
  const middle = s.slice(len - 4, len - 1); // three chars before last
  const suffix = "x";
  return prefix + middle + suffix;
};

const maskMobile = (mobile) => maskShowMiddleThreeBeforeLast(mobile);
const maskBankAccount = (acct) => maskShowMiddleThreeBeforeLast(acct);

onMounted(() => {
  fetchUserData();
});
</script>

<template>
  <Notification v-if="statusStore.getStatus() !== null" />
  <div class="min-h-screen grid place-items-center bg-gray-0">
    <div class="w-full max-w-xl rounded-2xl bg-white p-5 relative">
      <button
        @click="closePage"
        aria-label="Close profile"
        class="absolute top-3 w-10 h-10 flex items-center justify-center rounded-full bg-gray-200 hover:bg-gray-300 text-gray-700"
      >
        <img src="/src/assets/imgs/close-symbol.png" class="w-10" />
      </button>

      <label
        class="absolute top-0 left-1/2 transform -translate-x-1/2 -translate-y-1/2"
      >
        <img
          src="/src/assets/imgs/account-symbol4.png"
          class="w-20 h-20 rounded-full border-0 border-white object-cover"
        />
      </label>
      <div class="text-black flex flex-col justify-center items-center">
        <div class="pt-7 text-center gap-1 flex flex-col">
          <p><strong>Nickname:</strong> {{ userStore.nickname }}</p>
          <p><strong>Email:</strong> {{ userStore.email }}</p>
          <p><strong>Fullname:</strong> {{ userStore.fullname }}</p>
          <p><strong>Type:</strong> {{ userStore.role }}</p>
        </div>

        <div
          v-if="userStore.role === 'SELLER'"
          class="text-center gap-1 flex flex-col mt-1"
        >
          <p>
            <strong>Mobile:</strong> {{ maskMobile(userStore.mobileNumber) }}
          </p>
          <p>
            <strong>Bank Account No:</strong>
            {{ maskBankAccount(userStore.bankAccountNumber) }}
          </p>
          <p><strong>Bank Name:</strong> {{ userStore.bankName }}</p>
        </div>

        <div class="mt-5">
          <router-link :to="{ name: 'EditProfile' }"
            ><button
              class="border-2 border-gray-400 rounded-md px-3 py-1 bg-gray-400 text-white hover:bg-gray-950 hover:border-gray-950"
            >
              Edit Profile
            </button></router-link
          >
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
