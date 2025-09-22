<script setup>
import { ref, watch } from "vue";
import { loginUser } from "@/libs/userApi";
import { useRouter } from "vue-router";
import { useStatusStore } from "@/stores/statusStore";
import { useUserStore } from "@/stores/ีuserStore";
const statusStore = useStatusStore();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const email = ref("");
const password = ref("");
const isDisable = ref(true);
const isShowError = ref(false);
const isShowPassword = ref(false);
const router = useRouter();
const userData = useUserStore();
const login = async () => {
  try {
    const data = await loginUser(BASE_API_DOMAIN, {
      email: email.value.trim(),
      password: password.value,
    });
    // บันทึกสถานะผู้ใช้แบบเรียบง่ายเพื่อให้ NavBar ตรวจสอบและแสดงไอคอนโปรไฟล์
    sessionStorage.setItem("isAuthenticated", "true");
    sessionStorage.setItem("userEmail", email.value.trim());
    // พยายามอ่าน nickname/role จาก response ถ้ามี, ถ้าไม่มีใช้ส่วนก่อน @ ของอีเมลเป็น fallback
    const nicknameFromApi =
      data?.data?.nickname || data?.nickname || email.value.split("@")[0];
    const roleFromApi = data?.data?.role || data?.role || "buyer";
    sessionStorage.setItem("userNickname", nicknameFromApi);
    sessionStorage.setItem("userRole", roleFromApi);
    // อัปเดต user store เบื้องต้น (ถ้าต้องการให้ข้อมูลพร้อมใช้ในหน้าอื่น)
    userData.nickname = nicknameFromApi;
    userData.email = email.value.trim();
    userData.role = roleFromApi;
    router.push({ name: "SaleItemsGallery" });
  } catch (error) {
    console.log(error);
    isShowError.value = true;
  }
};
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
      </div>
      <form
        @submit.prevent="login"
        class="flex-1 flex flex-col items-center justify-center gap-5"
      >
        <div
          v-show="isShowError"
          class="itbms-message flex justify-between w-72 py-1 px-3 bg-red-100 border border-red-500 text-red-500 text-xs text-center"
        >
          <p>
            {{
              statusStore.getStatus() === 400
                ? "Email or password is incorrect."
                : statusStore.getMessage()
            }}
          </p>
          <button
            type="button"
            @click="isShowError = false"
            class="text-black hover:text-red-500"
          >
            x
          </button>
        </div>
        <h1 class="text-4xl font-bold text-center">Login</h1>
        <div class="login-input mx-auto w-72 flex flex-col gap-4">
          <div>
            <input
              type="text"
              v-model="email"
              maxlength="50"
              placeholder="Email"
              class="itbms-email w-72 px-4 py-3 border border-gray-300 rounded-2xl outline-none"
            />
          </div>

          <div class="flex items-center border border-gray-300 rounded-2xl">
            <input
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
              {{ isShowPassword ? "Hide" : "Show" }}
            </button>
          </div>
        </div>
        <div class="flex justify-between w-72 text-xs">
          <div class="flex space-x-2">
            <input type="checkbox" />
            <label>Remember me</label>
          </div>
          <label>Forgot password?</label>
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
          <span class="text-blue-500">Create an Account</span>
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
