<script setup>
import { onMounted } from "vue";
import { useTokenStore } from "./stores/tokenStore";
import { refreshAccessToken } from "./libs/userApi";
import { useRouter } from "vue-router";
import { jwtDecode } from "jwt-decode";
const tokenStore = useTokenStore();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const router = useRouter();
const requestNewAccessToken = async () => {
  try {
    const data = await refreshAccessToken(`${BASE_API_DOMAIN}`);
    const decoded = jwtDecode(data.access_token);
    tokenStore.setAccessToken(data.access_token);
    tokenStore.setDecode(decoded);
  } catch (e) {
    console.log(e);
    router.push({ name: "Login" });
  }
};
onMounted(() => requestNewAccessToken());
</script>

<template>
  <div class="container-system h-full bg-black overflow-hidden">
    <RouterView :key="$route.fullPath" />
  </div>
</template>

<style scoped>
.container-system {
  font-family: "Montserrat", sans-serif;
}
</style>
