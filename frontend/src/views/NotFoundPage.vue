<script setup>
import { useRouter } from "vue-router";

const router = useRouter();
</script>

<template>
  <div
    class="not-found-container h-screen flex flex-col gap-5 justify-center items-center text-white bg-black"
  >
    <h1 class="text-9xl font-black">404</h1>
    <h2 class="text-5xl font-semibold">Not Found</h2>
    <p class="">This page is currently under development.</p>
    <button
      @click="router.push({ name: 'Home' })"
      class="px-6 py-2 rounded bg-blue-500 cursor-pointer hover:bg-blue-600"
    >
      Home
    </button>
  </div>
</template>

<style scoped></style>
