<script setup></script>

<template>
  <div class="landing-container h-screen bg-black">
    <div class="grid grid-cols-2 h-full text-white">
      <div class="landing-content self-center w-xl mx-auto">
        <h1 class="text-5xl/normal glow-text">
          Transform Your Life with iPhone
        </h1>
        <p class="text-2xl mt-6">Fast. Powerful. Pro-level Camera.</p>
        <p class="text-2xl my-6">
          0% Installments for Up to 10 Months + Tons of Freebies!
        </p>
        <div
          class="itbms-shopnow w-fit mx-auto mt-10 mb-4 px-4 py-2.5 border border-blue-500 rounded-4xl text-2xl text-blue-500 hover:bg-blue-500 hover:text-white duration-300"
        >
          <RouterLink :to="{ name: 'SaleItemsGallery' }">Shop Now</RouterLink>
        </div>
        <div class="itbms-seller w-fit mx-auto">
          <RouterLink
            :to="{ name: 'SaleItemsList' }"
            class="underline hover:text-blue-500 duration-200"
            >Seller</RouterLink
          >
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

<style scoped>
.landing-container {
  animation-name: slide-fade;
  animation-timing-function: ease-in;
  animation-duration: 0.7s;
}

.glow-text {
  text-shadow: 0 0 5px #fff, 0 0 3px #fff;
}
@keyframes slide-fade {
  from {
    transform: translateY(500px);
    opacity: 0;
  }
  to {
    transform: translateY(0px);
    opacity: 1;
  }
}
</style>
