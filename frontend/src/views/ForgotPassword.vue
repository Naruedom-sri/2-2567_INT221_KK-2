<script setup>
import { reactive, computed, ref } from "vue";
import { useRouter } from "vue-router";
import { sendEmailForgotPassword } from "@/libs/userApi";
import { useStatusStore } from "@/stores/statusStore";
import GeneralAlertMessage from "@/components/GeneralAlertMessage.vue";

const statusStore = useStatusStore();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const router = useRouter();
const isShowError = ref(false);
const showConfirmModal = ref(false);

const form = reactive({
  email: "",
});

async function sendEmail() {
  const payload = {
    email: (form.email || "").trim(),
  };

  try {
    await sendEmailForgotPassword(BASE_API_DOMAIN, payload.email);
    isShowError.value = false;
    showConfirmModal.value = true;
  } catch (error) {
    console.error("Failed to send reset password email", error);
    isShowError.value = true;
  }
}

function cancelSend() {
  router.back();
}

function confirmed() {
  router.push({ name: "Login" });
  showConfirmModal.value = false;
}

const isUnchanged = computed(() => {
  return !form.email;
});
</script>

<template>
  <div class="min-h-screen grid place-items-center bg-gray-0">
    <div class="w-full max-w-2xl rounded-2xl bg-white pt-10 pb-6 px-6 relative">
      <div class="text-center">
        <p class="text-3xl font-bold">Forgot Password?</p>
        <span class="opacity-50">send your email for reset password</span>
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
              statusStore.getMessage()
            }}
          </p>
          <p v-else>
            {{ statusStore.getMessage() }}
          </p>
        </div>

        <div class="form-row gap-1 flex flex-col">
          <label><strong>Email:</strong></label>
          <div
            class="flex items-center border border-gray-800 rounded-md p-2 w-145"
          >
            <input
              v-model="form.email"
              type="email"
              class="flex-1 outline-none"
              placeholder="Enter Your Email"
            />
          </div>
          <div>
          <small class="opacity-50">Please check your email for a link to reset your password.</small>
        </div>
        </div>

        <div class="mt-4 flex gap-4 justify-center">
          <button
            @click.stop="sendEmail"
            :disabled="isUnchanged"
            :class="[
              'border-2 border-gray-500 rounded-md px-3 py-1',
              isUnchanged
                ? 'bg-gray-200 opacity-50 cursor-not-allowed'
                : 'bg-gray-300 hover:bg-gray-400',
            ]"
          >
            Send
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
    <GeneralAlertMessage
    v-if="showConfirmModal"
    title="Reset Password"
    message="Please check your email for reset the password."
    @ok="confirmed"
  />
  </div>
</template>

<style scoped></style>
