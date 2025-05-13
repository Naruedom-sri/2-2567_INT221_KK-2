<script setup>
import { useRouter } from "vue-router";
import { useSaleItemStatusStore } from "@/stores/SaleItemStatus";
const statusStore = useSaleItemStatusStore();
const route = useRouter();
const goBackToPreviousPage = () => {
  statusStore.clearStatus();
  route.back();
};
</script>

<template>
  <div
    class="err-message-container h-screen flex flex-col justify-center items-center gap-10 text-4xl text-white"
  >
    <img
      src="/src/assets/imgs/err-item-notfound.png"
      alt="item-notfound"
      class="w-72 object-cover"
    />
    <p class="itbms-message">The requested sale item does not exist.</p>
    <button
      @click="goBackToPreviousPage"
      class="itbms-button py-4 px-10 border border-blue-500 rounded-3xl text-blue-500 text-2xl hover:bg-blue-500 hover:text-white hover:cursor-pointer duration-150"
    >
      Ok
    </button>
  </div>
</template>

<style scoped></style>
