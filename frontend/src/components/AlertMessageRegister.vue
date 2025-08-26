<script setup>
import { ref, onMounted } from "vue";
import { useSaleItemStatusStore } from "@/stores/SaleItemStatus";
const statusStore = useSaleItemStatusStore();
const status = statusStore.getStatus();
const showMessage = ref(true);
const setIntervalShowMessage = () => {
  setTimeout(() => {
    showMessage.value = false;
    statusStore.clearStatusAndMethod();
  }, 5000);
};
onMounted(() => {
  setIntervalShowMessage();
});
</script>

<template>
  <div
    v-show="showMessage"
    class="message-container flex gap-8 fixed right-10 bottom-10 w-96 pl-2.5 pr-7 py-4 rounded-2xl bg-white z-50"
    :class="status !== 201 ? 'text-red-700' : 'text-green-700'"
  >
    <div
      class="w-2.5 rounded-2xl"
      :class="status !== 201 ? 'bg-red-700' : 'bg-green-700'"
    ></div>
    <div class="w-full flex flex-col">
      <div class="title flex justify-between">
        <h1 class="text-2xl font-bold">
          {{ status === 201 ? "Success!" : "Error!" }}
        </h1>
        <button
          @click="(showMessage = false), statusStore.clearStatusAndMethod()"
          class="w-6 h-6 text-center font-black text-black/80 bg-gray-300 rounded-full hover:cursor-pointer"
        >
          X
        </button>
      </div>
      <div class="message w-64">
        <h1 v-if="status === 201" class="itbms-message text-lg">
          The user account has been successfully registered.
        </h1>
        <h1 v-else class="itbms-message">Registration failed.</h1>
      </div>
    </div>
  </div>
</template>

<style scoped>
.message-container {
  animation: slide 0.8s ease;
}
@keyframes slide {
  from {
    transform: scale(0.9);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}
</style>
