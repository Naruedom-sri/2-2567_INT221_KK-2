<script setup>
import { reactive, computed, ref } from "vue";
import { useRouter } from "vue-router";
import { resetPassword } from "@/libs/userApi";
import { useStatusStore } from "@/stores/statusStore";
import { decodeToken } from "@/libs/jwtToken";

const statusStore = useStatusStore();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const router = useRouter();
const isShowError = ref(false);
const accessToken = localStorage.getItem("accessToken");
const decoded = decodeToken(accessToken);
const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\w\s]).{8,}$/;

const form = reactive({
  password: "",
});

async function submitToResetPassword() {
  const payload = {
    password: (form.password || "").trim(),
  };

  if (!passwordPattern.test(payload.password)) {
    isShowError.value = true;
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "password",
      "edit",
      400,
      "Password must be at least 8 characters and include uppercase, lowercase, number, and special character."
    );
    return; 
  }

  try {
    // const tokenUrl = router.query.token
    await resetPassword(BASE_API_DOMAIN, payload.password, decoded.jti, accessToken);

    isShowError.value = false;
    router.push({ name: "Login" });
  } catch (error) {
    console.error("Failed to send reset password email", error);
  }
}

function cancelSend() {
  router.back();
}

const isUnchanged = computed(() => {
  return !form.email;
});
</script>

<template>
  <div class="min-h-screen grid place-items-center bg-gray-0">
    <div class="w-full max-w-2xl rounded-2xl bg-white pt-10 pb-6 px-6 relative">
      <div class="text-center">
        <p class="text-3xl font-bold">Reset Password</p>
        <span>enter your new password to reset password</span>
      </div>
      <div class="p-5 text-black">
        <div
          v-if="statusStore.getStatus() !== null"
          class="itbms-message py-1 px-5 text-xs rounded"
          :class="
            isShowError
              ? 'bg-red-100 border border-red-500 text-red-500'
              : 'bg-green-100 border border-green-500 text-green-500'
          "
        >
          <p v-if="isShowError">
            {{
              statusStore.getStatus() === 401
                ? "Old password is incorrect."
                : statusStore.getMessage()
            }}
          </p>
          <p v-else>
            {{ statusStore.getMessage() }}
          </p>
        </div>

        <div class="flex flex-col items-left gap-1 ">
          <label><strong>New Password:</strong></label>
          <div class="flex flex-col">
          <div
            class="flex border border-gray-800 rounded-md p-2 w-110"
          >
            <input
              v-model="form.password"
              type="password"
              class="flex-1 outline-none"
              placeholder="Enter Your New Password"
            /> 
          </div>
            <small class="text-xs text-gray-500 mt-1 w-110">
            Minimum 8 chars, including upper, lower, number, and special
            character
          </small>
          </div>
        </div>

        <div class="mt-4 flex gap-4 justify-center">
          <button
            @click.stop="submitToResetPassword"
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
            @click="cancelSend"
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

<style scoped></style>
