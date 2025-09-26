<script setup>
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
</script>

<template>
  <div>
    <NavBar />
    <div class="w-fullscreen grid grid-cols-2 text-white">
      <div class="text-left">
        <p class="text-3xl">Shopping Cart</p>
      </div>
      <div class="flex flex-col border border-white text-center p-1 rounded-md">
        <p class="text-3xl">Cart Summary</p>
        <div>
            <p class="text-xl text-left">Total Items:</p>
        </div>
        <div>
            <p class="text-xl text-left">Total Price:</p>
        </div>
        <div>
            <button class="border-none bg-blue-700 rounded-md p-1 w-100 hover:bg-blue-800">Place Order</button>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<style scoped></style>
