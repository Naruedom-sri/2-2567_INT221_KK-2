<script setup></script>

<template>
  <div
    class="container-header flex justify-between items-center w-full h-20 px-6 bg-[rgba(22,22,23,255)]"
  >
    <div class="logo">
      <RouterLink :to="{name:'LandingPage'}">
      <img
        src="/src/assets/imgs/itbms-logo.png"
        alt="logo"
        class="w-48 h-20 object-cover"
      />
    </RouterLink>
    </div>
    <div class="cart">
      <img
        src="/src/assets/imgs/cart-symbol.png"
        alt="logo"
        class="w-14 object-cover hover:cursor-pointer"
      />
    </div>
  </div>
</template>

<style scoped></style>
