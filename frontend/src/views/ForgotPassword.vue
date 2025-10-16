<script setup>
import { reactive, computed, ref } from "vue";
import { useRouter } from "vue-router";
import { sendEmailforgotPassword } from "@/libs/userApi";
import { useStatusStore } from "@/stores/statusStore";

const statusStore = useStatusStore();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const router = useRouter();
const isShowError = ref(false);

const form = reactive({
  email: "",
});

async function sendEmail() {
  const payload = {
    email: (form.email || "").trim(),
  };

  try {
    await sendEmailforgotPassword(BASE_API_DOMAIN, payload.email);

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
        <p class="text-3xl font-bold">Forgot Password?</p>
        <span>send your email to reset password</span>
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

        <div class="form-row gap-1 flex flex-row items-center text-center">
          <label><strong>Email:</strong></label>
          <div
            class="flex items-center border border-gray-800 rounded-md p-2 w-150"
          >
            <input
              v-model="form.email"
              type="email"
              class="flex-1 outline-none"
              placeholder="Enter Your Email"
            />
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
  </div>
</template>

<style scoped></style>
