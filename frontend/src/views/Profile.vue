<!-- <script setup>
import { useUserStore } from "@/stores/ีuserStore";

const userData = useUserStore();
</script>

<template>
  <div class="min-h-screen grid place-items-center bg-gray-0">
    <div class="w-full max-w-2xl rounded-2xl bg-white p-6">
      <div class="text-black">
        <div class="p-3 text-center">
          <p>Nickname: {{ userData.nickname }}</p>
          <p>Email: {{ userData.email }}</p>
          <p>Fullname: {{ userData.fullname }}</p>
          <p>Type: {{ userData.role }}</p>
          <div class="mt-4">
             <router-link :to="{ name: 'EditProfile' }"
              ><button class="border-2 border-gray-500 rounded-md px-3 py-1 bg-gray-300 hover:bg-gray-400">Edit Profile</button></router-link
            >
          </div>
           
        </div>

        <div v-if="userData.role.toLowerCase() === 'seller'">
          <p>Mobile: {{ userData.sellerMobileNumber }}</p>
          <p>Bank Account No: {{ userData.sellerBankAccountNumber }}</p>
          <p>Bank Name: {{ userData.sellerBankName }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style> -->

<script setup>
import { onMounted } from "vue";
import { useUserStore } from "@/stores/ีuserStore";
import { getUserById } from "@/libs/userApi";
import { useStatusStore } from "@/stores/statusStore"; // ถ้าใช้ statusStore
import { useRouter } from "vue-router";

const userStore = useUserStore();
const statusStore = useStatusStore();
const router = useRouter();

// ใส่ base URL ของ backend
const BASE_URL = "http://localhost:8080/itb-mshop";

// สมมุติว่าคุณมี token เก็บไว้
const token = localStorage.getItem("token"); 

const fetchUserData = async () => {
  try {
    const userId = userStore.id || 1; // หรือเอาจาก token/route
    const userData = await getUserById(BASE_URL, userId, token);

    // set store
    userStore.setUser(userData);
  } catch (error) {
    console.error("Failed to fetch user:", error);
  }
};

onMounted(() => {
  fetchUserData();
});

const goEditProfile = () => {
  router.push({ name: "EditProfile" });
};
</script>

<template>
  <div class="min-h-screen grid place-items-center bg-gray-50">
    <div class="w-full max-w-2xl rounded-2xl bg-white p-6 shadow-md">
      <div class="text-black text-center">
        <p><strong>Nickname:</strong> {{ userStore.nickname }}</p>
        <p><strong>Email:</strong> {{ userStore.email }}</p>
        <p><strong>Fullname:</strong> {{ userStore.fullname }}</p>
        <p><strong>Type:</strong> {{ userStore.role }}</p>

        <div class="mt-4">
          <button
            class="border-2 border-gray-500 rounded-md px-3 py-1 bg-gray-300 hover:bg-gray-400"
            @click="goEditProfile"
          >
            Edit Profile
          </button>
        </div>

        <div v-if="userStore.role.toLowerCase() === 'seller'" class="mt-4 text-left">
          <p><strong>Mobile:</strong> {{ userStore.sellerMobileNumber }}</p>
          <p><strong>Bank Account No:</strong> {{ userStore.sellerBankAccountNumber }}</p>
          <p><strong>Bank Name:</strong> {{ userStore.sellerBankName }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* เพิ่มเงาและ background เล็กน้อยให้สวยขึ้น */
</style>

