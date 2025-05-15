<script setup>
import NavBar from "@/components/์NavBar.vue";
import Footer from "@/components/Footer.vue";
const brands = ["Apple", "Samsung", "Vivo", "Oppo", "Huawei", "Sony"];
</script>

<template>
  <NavBar />
  <div class="home-container mx-36 my-24 text-white">
    <h1 class="w-3xl text-5xl/relaxed font-medium text-white/70">
      <span class="text-white glow-text">Our store</span> is the best place to
      buy the products you love.
    </h1>
    <div class="nav-brand flex justify-around my-24">
      <RouterLink
        :to="{ name: 'SaleItemsGallery' }"
        v-for="(brand, index) in brands"
        :key="index"
        class="w-36 flex flex-col items-center"
      >
        <img
          :src="`/imgs/homes/${brand.toLocaleLowerCase()}.png`"
          :alt="`${brand.toLocaleLowerCase()}`"
          class="object-cover"
        />
        <button class="hover:cursor-pointer">{{ brand }}</button>
      </RouterLink>
    </div>
    <h1 class="text-3xl ">
      Our latest products.
      <span class="text-white/70">Come see what's new.</span>
    </h1>
    <div class="product flex my-10 gap-6">
      <div
        class="w-md rounded-2xl text-lg px-10 pt-14 shadow-white bg-[rgba(22,22,23,255)] hover:scale-[101%] hover:cursor-pointer hover:shadow-xs duration-200"
      >
        <p class="mb-3 text-3xl font-semibold">iPhone 16 Pro</p>
        <p class="mb-1">The best of iPhone</p>
        <p class="text-white/70 text-base">From ฿ 39,900</p>
        <img
          src="/src/assets/imgs/homes/iphone-16.png"
          alt="iphone"
          class="w-64 mx-auto mt-6 object-cover"
        />
      </div>
      <div
        class="w-md rounded-2xl text-lg px-10 pt-14 shadow-white bg-[rgba(22,22,23,255)] hover:scale-[101%] hover:cursor-pointer hover:shadow-xs duration-200"
      >
        <p class="mb-3 text-3xl font-semibold">Reno13 F 5G</p>
        <p class="mb-1">Thinner than before</p>
        <p class="text-white/70 text-base">From ฿ 24,999</p>
        <img
          src="/src/assets/imgs/homes/reno.png"
          alt="iphone"
          class="w-full h-96 mx-auto object-cover"
        />
      </div>
      <div
        class="w-md flex flex-col justify-between rounded-2xl shadow-white text-lg px-10 pt-14 bg-[rgba(22,22,23,255)] hover:scale-[101%] hover:cursor-pointer hover:shadow-xs duration-200"
      >
        <div>
          <p class="mb-3 text-3xl font-semibold">Galaxy S24 Ultra</p>
          <p class="mb-1">Galaxy AI is here</p>
          <p class="text-white/70 text-base">From ฿ 32,900</p>
        </div>

        <img
          src="/src/assets/imgs/homes/galaxy-s24.png"
          alt="iphone"
          class="w-full h-[283px] object-cover"
        />
      </div>
    </div>
  </div>
  <Footer />
</template>

<style scoped>
.glow-text {
  text-shadow: 0 0 5px #fff, 0 0 10px #fff;
}
</style>
