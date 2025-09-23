<script setup>
import { reactive, computed } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import { useTokenStore } from "@/stores/tokenStore";
import { editProfile } from "@/libs/userApi";
// import { computed } from "vue";
import { jwtDecode } from "jwt-decode";

const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const router = useRouter();
const userData = useUserStore();
const tokenStore = useTokenStore();

const form = reactive({
  nickname: userData.nickname || "",
  fullName: userData.fullname || "" 
});

async function saveProfile() {
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
    await editProfile(
      BASE_API_DOMAIN,
      tokenStore.getDecode().jti, 
      tokenStore.getAccessToken(),{
        nickname: form.nickname,
        fullName: form.fullName
      }
    );

    Object.assign(userData, form);

    router.push({ name: "Profile" });
  } catch (error) {
    console.error("Failed to save profile:", error);
  }
}

function cancelEdit() {
  router.back();
}
</script>

<template>
  <div class="min-h-screen grid place-items-center bg-gray-0">
    <div class="w-full max-w-2xl rounded-2xl bg-white pt-10 pb-6 px-6 relative">
      <label class="absolute top-0 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
        <img src="/src/assets/imgs/account-symbol4.png" class="w-25 h-25 rounded-full border-0 border-white object-cover"/>
      </label>
      <div class="p-5 text-black">
        <div class="form-row">
          <label><strong>Nickname:</strong></label>
          <input v-model="form.nickname" type="text" />
        </div>

        <div class="form-row">
          <label><strong>Email:</strong></label>
          <input v-model="userData.email" type="email" readonly/>
        </div>

        <div class="form-row">
          <label><strong>Fullname:</strong></label>
          <input v-model="form.fullName" type="text" />
        </div>

        <div class="form-row">
          <label><strong>Type:</strong></label>
          <p>{{ userData.role }}</p>
        </div>

        <div v-if="userData.role === 'SELLER'">
          <div class="form-row">
            <label><strong>Mobile:</strong></label>
            <input v-model="userData.mobileNumber" type="text" readonly />
          </div>
          <div class="form-row">
            <label><strong>Bank Account No:</strong></label>
            <input v-model="userData.bankAccountNumber" type="text" readonly />
          </div>
          <div class="form-row">
            <label><strong>Bank Name:</strong></label>
            <input v-model="userData.bankName" type="text" readonly />
          </div>
        </div>

        <div class="mt-4 flex gap-4 justify-center">
          <button
            @click="saveProfile"
            class="border-2 border-gray-500 rounded-md px-3 py-1 bg-gray-300 hover:bg-gra y-400"
          >
            Save
          </button>
          <button
            @click="cancelEdit"
            type="button"
            class="border-2 border-gray-500 rounded-md px-3 py-1 bg-gray-300 hover:bg-gray-400"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.form-row {
  margin-bottom: 8px;
  display: flex;
  flex-direction: column;
}
/* .actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
} */
</style>
