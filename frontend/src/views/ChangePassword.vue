<script setup>
import { reactive, computed, ref } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import { editPassword } from "@/libs/userApi";
import { decodeToken } from "@/libs/jwtToken";
import { useStatusStore } from "@/stores/statusStore";

const statusStore = useStatusStore();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const router = useRouter();
const userData = useUserStore();
const accessToken = localStorage.getItem("accessToken");
const decoded = decodeToken(accessToken);
const isShowError = ref(false);

const form = reactive({
  oldPassword: "",
  newPassword: "",
});

const isShowOldPassword = ref(false);
const isShowNewPassword = ref(false);

async function saveNewPassword() {
  const payload = {
    oldPassword: (form.oldPassword || "").trim(),
    newPassword: (form.newPassword || "").trim(),
  };

  try {
    await editPassword(BASE_API_DOMAIN, decoded.jti, accessToken, payload);

    isShowError.value = false;
    router.push({ name: "Profile" });

  } catch (error) {
    console.error("Failed to change password", error);

    if (statusStore.getStatus() === 401) {
      isShowError.value = true;
    } else {
      isShowError.value = false;
    }
  }
}

function cancelEdit() {
  router.back();
}

const isUnchanged = computed(() => {
  return !form.oldPassword || !form.newPassword;
});
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
        <div class="form-row gap-1 flex flex-row">
          <label><strong>Nickname:</strong></label>
          <input v-model="userData.nickname" readonly type="text" />
        </div>

        <div class="form-row gap-1 flex flex-row">
          <label><strong>Email:</strong></label>
          <input v-model="userData.email" type="email" readonly class="w-100" />
        </div>

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

        <div class="form-row flex flex-col mb-4">
          <label><strong>Old Password:</strong></label>
          <div class="flex items-center border border-gray-800 rounded-md p-2">
            <input
              v-model="form.oldPassword"
              :type="isShowOldPassword ? 'text' : 'password'"
              class="flex-1 outline-none"
              placeholder="Enter old password"
              @focus="
                (isShowError = false),
                  statusStore.clearEntityAndMethodAndStatusAndMessage()
              "
            />
            <button
              type="button"
              @click="isShowOldPassword = !isShowOldPassword"
              class="px-2"
            >
              <img
                v-if="isShowOldPassword"
                src="/src/assets/imgs/eye-off.png"
                class="w-6 opacity-50"
              />
              <img
                v-else
                src="/src/assets/imgs/eye-open.png"
                class="w-6 opacity-50"
              />
            </button>
          </div>
        </div>

        <div class="form-row flex flex-col">
          <label><strong>New Password:</strong></label>
          <div class="flex items-center border border-gray-800 rounded-md p-2">
            <input
              v-model="form.newPassword"
              :type="isShowNewPassword ? 'text' : 'password'"
              class="flex-1 outline-none"
              placeholder="Enter new password"
              @focus="
                (isShowError = false),
                  statusStore.clearEntityAndMethodAndStatusAndMessage()
              "
            />
            <button
              type="button"
              @click="isShowNewPassword = !isShowNewPassword"
              class="px-2"
            >
              <img
                v-if="isShowNewPassword"
                src="/src/assets/imgs/eye-off.png"
                class="w-6 opacity-50"
              />
              <img
                v-else
                src="/src/assets/imgs/eye-open.png"
                class="w-6 opacity-50"
              />
            </button>
          </div>
        </div>

        <div class="mt-4 flex gap-4 justify-center">
          <button
            @click.stop="saveNewPassword"
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

<style scoped></style>
