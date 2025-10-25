<script setup>
import { onMounted, ref, watch } from "vue";
import { loginUser } from "@/libs/userApi";
import { useRouter } from "vue-router";
import { useStatusStore } from "@/stores/statusStore";
import { decodeToken } from "@/libs/jwtToken";
const statusStore = useStatusStore();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const email = ref("");
const password = ref("");
const isDisable = ref(true);
const isShowError = ref(false);
const isShowPassword = ref(false);
const router = useRouter();

const login = async () => {
  try {
    const data = await loginUser(BASE_API_DOMAIN, {
      email: email.value.trim(),
      password: password.value,
    });
    localStorage.setItem("accessToken", data.access_token);
    const decoded = decodeToken(data.access_token);
    if (decoded.role === "SELLER") {
      router.push({ name: "SaleItemsList" });
    } else {
      router.push({ name: "SaleItemsGallery" });
    }
  } catch (error) {
    console.log(error);
    isShowError.value = true;
  }
};

onMounted(async () => {
  const accessToken = localStorage.getItem("accessToken");
  if (accessToken) {
    const decoded = decodeToken(accessToken);
    if (decoded.role === "SELLER") {
      router.push({ name: "SaleItemsList" });
    } else if (decoded.role === "BUYER") {
      router.push({ name: "SaleItemsGallery" });
    }
  }
});

watch(
  [email, password],
  () => {
    if (email.value.trim() !== "" && password.value !== "")
      isDisable.value = false;
    else isDisable.value = true;
  },
  { immediate: true }
);
</script>

<template>
  <div class="login-container h-screen text-sm">
    <div class="flex w-full h-full bg-white">
      <div
        class="right-content flex flex-col items-center justify-center flex-1 gap-4 text-center text-white"
      >
        <h1 class="text-4xl font-semibold">Welcome to IT Bangmod Shop!</h1>
        <p>You can login into access with your existing account.</p>
        <button>Store</button>
      </div>
      <form
        @submit.prevent="login"
        class="flex-1 flex flex-col items-center justify-center gap-5"
      >
        <div
          v-if="statusStore.getStatus() !== null"
          class="itbms-message py-1 px-5 text-xs"
          :class="
            isShowError
              ? 'bg-red-100 border border-red-500 text-red-500'
              : 'bg-green-100 border border-green-500 text-green-500'
          "
        >
          <p v-if="isShowError">
            {{
              statusStore.getStatus() === 400
                ? "Email or password is incorrect."
                : statusStore.getMessage()
            }}
          </p>
          <p v-else>
            {{ statusStore.getMessage() }}
          </p>
        </div>
        <h1 class="text-4xl font-bold text-center">Login</h1>
        <div class="login-input mx-auto w-72 flex flex-col gap-4">
          <div>
            <input
              @focus="
                (isShowError = false),
                  statusStore.clearEntityAndMethodAndStatusAndMessage()
              "
              type="text"
              v-model="email"
              maxlength="50"
              placeholder="Email"
              class="itbms-email w-72 px-4 py-3 border border-gray-300 rounded-2xl outline-none"
            />
          </div>

          <div class="flex items-center border border-gray-300 rounded-2xl">
            <input
              @focus="
                (isShowError = false),
                  statusStore.clearEntityAndMethodAndStatusAndMessage()
              "
              :type="isShowPassword ? 'text' : 'password'"
              v-model="password"
              maxlength="14"
              placeholder="Password"
              class="itbms-password w-72 pl-4 py-3 outline-none"
            />
            <button
              type="button"
              @click="isShowPassword = !isShowPassword"
              class="px-4 text-xs"
            >
              <span v-if="isShowPassword">
                <img
                  src="/src/assets/imgs/eye-off.png"
                  class="w-7 opacity-50"
                />
              </span>
              <span v-else>
                <img
                  src="/src/assets/imgs/eye-open.png"
                  class="w-7 opacity-50"
                />
              </span>
            </button>
          </div>
        </div>
        <div class="flex justify-between w-72 text-xs">
          <div class="flex space-x-2">
            <input type="checkbox" />
            <label>Remember me</label>
          </div>
          <div>
            <button
              type="button"
              class="text-blue-500 hover:underline cursor-pointer"
              @click="router.push({ name: 'ForgotPassword' })"
            >
              Forgot password?
            </button>
          </div>
        </div>
        <button
          type="submit"
          class="itbms-signin-button w-72 py-2 text-base bg-blue-500 text-white disabled:bg-[rgba(22,22,23,255)] rounded-2xl disabled: disabled:opacity-50 disabled:cursor-not-allowed"
          :disabled="isDisable"
        >
          Login
        </button>
        <button type="button" @click="router.push({ name: 'Register' })">
          Don't have account?
          <span class="text-blue-500 hover:underline cursor-pointer"
            >Create an Account</span
          >
        </button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.divider {
  position: absolute;
  top: 0;
  left: 50%;
  width: 2px; /* ความหนาของเส้น */
  height: 100%;
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.1),
    rgba(0, 0, 0, 0.5),
    rgba(0, 0, 0, 0.1)
  );
  transform: translateX(-50%);
}
.right-content {
  background-image: url("/src/assets/imgs/bg/login-page.gif");
}
</style>
