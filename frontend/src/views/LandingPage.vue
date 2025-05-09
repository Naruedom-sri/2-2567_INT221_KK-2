<script setup></script>

<template>
  <div class="landing-container h-screen bg-black overflow-hidden">
    <div class="grid grid-cols-2 h-full text-white">
      <div class="landing-content self-center w-xl mx-auto space-y-6">
        <h1 class="text-5xl/normal text-shadow-md/50 text-shadow-white">
          Transform Your Life with iPhone
        </h1>
        <p class="text-2xl">Fast. Powerful. Pro-level Camera.</p>
        <p class="text-2xl">
          0% Installments for Up to 10 Months + Tons of Freebies!
        </p>
        <div
          class="itbms-shopnow w-fit mx-auto my-9 px-4 py-2.5 border border-blue-500 rounded-4xl text-2xl text-blue-500 hover:bg-blue-500 hover:text-white duration-300"
        >
          <RouterLink :to="{ name: 'SaleItemsGallery' }">Shop Now</RouterLink>
        </div>
      </div>
      <img
        src="/src/assets/imgs/iphone-15.png"
        alt="iphone"
        class="self-end w-full h-[92%] object-cover hover:scale-[102%] duration-500"
      />
    </div>
  </div>
</template>

<style scoped></style>
