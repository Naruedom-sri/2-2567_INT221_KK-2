<script setup>
import { ref } from "vue";
const email = ref("");
const password = ref("");
const login = () => {
  console.log("login");
};
</script>

<template>
  <div
    class="login-container flex justify-center items-center h-screen text-white text-sm"
  >
    <div class="flex backdrop-blur-sm border border-white w-3xl h-96">
      <div class="bg-black flex-1">
        <h1 class="text-lg">Welcome to IT Bangmod Shop</h1>
      </div>
      <form @submit.prevent="login" class="flex-1">
        <h1 class="text-4xl font-bold mb-8 text-center mt-8">Login</h1>
        <div class="login-input flex flex-col gap-2">
          <h1 class="text-base">Username</h1>
          <input
            type="text"
            v-model="email"
            placeholder="Email"
            class="w-72 pl-4 py-1 bg-[rgba(22,22,23,255)] rounded outline-none"
          />
          <h1 class="text-base">Password</h1>
          <input
            type="password"
            v-model="password"
            placeholder="Password"
            class="w-72 pl-4 py-1 bg-[rgba(22,22,23,255)] rounded outline-none"
          />
        </div>
        <button type="submit" class="bg-white text-black">Login</button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  background-image: url("/src/assets/imgs/bg/login-page.gif");
}
</style>
