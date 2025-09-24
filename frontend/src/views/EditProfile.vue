<script setup>
import { reactive, computed } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import { editProfile } from "@/libs/userApi";
import { decodeToken } from "@/libs/jwtToken";

const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const router = useRouter();
const userData = useUserStore();
const accessToken = localStorage.getItem("accessToken");
const decoded = decodeToken(accessToken);

const form = reactive({
  nickname: userData.nickname || "",
  fullName: userData.fullname || "",
});

async function saveProfile() {
  const payload = {
    nickname: (form.nickname || "").trim(),
    fullName: (form.fullName || "").trim(),
  };

  try {
    await editProfile(BASE_API_DOMAIN, decoded.jti, accessToken, {
      nickname: payload.nickname,
      fullName: payload.fullName,
    });

    Object.assign(userData, payload);
    form.nickname = payload.nickname;
    form.fullName = payload.fullName;
    router.push({ name: "Profile" });
  } catch (error) {
    console.error( error);
  }
}

function cancelEdit() {
  router.back();
}

const isUnchanged = computed(() => {
  return (
    form.nickname === (userData.nickname || "") &&
    form.fullName === (userData.fullname || "")
  );
});

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
</script>

<template>
  <div class="min-h-screen grid place-items-center bg-gray-0">
    <div class="w-full max-w-2xl rounded-2xl bg-white pt-10 pb-6 px-6 relative">
      <label
        class="absolute top-0 left-1/2 transform -translate-x-1/2 -translate-y-1/2"
      >
        <img
          src="/src/assets/imgs/account-symbol4.png"
          class="w-25 h-25 rounded-full border-0 border-white object-cover"
        />
      </label>
      <div class="p-5 text-black">
        <div class="form-row">
          <label><strong>Nickname:</strong></label>
          <input
            v-model="form.nickname"
            type="text"
            class="border border-gray-800 rounded-md p-2"
          />
        </div>

        <div class="form-row">
          <label><strong>Email:</strong></label>
          <input v-model="userData.email" type="email" readonly />
        </div>

        <div class="form-row">
          <label><strong>Fullname:</strong></label>
          <input
            v-model="form.fullName"
            type="text"
            class="border border-gray-800 rounded-md p-2"
          />
        </div>

        <div class="form-row">
          <label><strong>Type:</strong></label>
          <p>{{ userData.role }}</p>
        </div>

        <div v-if="userData.role === 'SELLER'">
          <div class="form-row">
            <label><strong>Mobile:</strong></label>
            <input
              :value="maskMobile(userData.mobileNumber)"
              type="text"
              readonly
            />
          </div>
          <div class="form-row">
            <label><strong>Bank Account No:</strong></label>
            <input
              :value="maskBankAccount(userData.bankAccountNumber)"
              type="text"
              readonly
            />
          </div>
          <div class="form-row">
            <label><strong>Bank Name:</strong></label>
            <input v-model="userData.bankName" type="text" readonly />
          </div>
        </div>

        <div class="mt-4 flex gap-4 justify-center">
          <button
            @click="saveProfile"
            :disabled="isUnchanged"
            :class="[
              'border-2 border-gray-500 rounded-md px-3 py-1',
              isUnchanged
                ? 'bg-gray-200 opacity-50 cursor-not-allowed'
                : 'bg-gray-300 hover:bg-gray-400',
            ]"
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
