<script setup></script>

<template>
  <div class="landing-container h-screen bg-black">
    <div class="bg-container grid grid-cols-2 h-full text-white">
      <div class="landing-content slide-fade self-center w-xl mx-auto">
        <h1 class="text-5xl/normal">Transform Your Life with iPhone</h1>
        <p class="text-2xl my-6">
          0% Installments for Up to 10 Months + Tons of Freebies!
        </p>

        <div
          class="itbms-shopnow w-fit py-2 px-4 text-xl bg-blue-500 rounded-3xl hover:bg-blue-500/80 duration-300"
        >
          <RouterLink :to="{ name: 'SaleItemsGallery' }">Shop Now</RouterLink>
        </div>
      </div>
      <img
        src="/src/assets/imgs/iphone-15.png"
        alt="iphone"
        class="fade self-end w-full h-[92%] object-cover hover:scale-[102%] duration-500"
      />
    </div>
  </div>
</template>

<style scoped>
.slide-fade {
  animation-name: slide-fade;
  animation-timing-function: ease-in-out;
  animation-duration: 1s;
}

.fade {
  animation-name: fade;
  animation-timing-function: ease-in;
  animation-duration: 1s;
}

@keyframes fade {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slide-fade {
  from {
    transform: translateY(200px);
    opacity: 0;
  }
  to {
    transform: translateY(0px);
    opacity: 1;
  }
}
</style>
