<script setup>
import { ref, onMounted } from "vue";
import { useSaleItemStatusStore } from "@/stores/SaleItemStatus";
const statusStore = useSaleItemStatusStore();
const status = statusStore.getStatus();
const showMessage = ref(true);
const setIntervalShowMessage = () => {
  console.log(status);
  setTimeout(() => {
    showMessage.value = false;
    statusStore.clearStatusAndMethod();
  }, 8000);
};
onMounted(() => {
  setIntervalShowMessage();
});
</script>

<template>
  <div
    v-show="showMessage"
    class="message-container fixed bottom-10 w-96 rounded-l space-y-1 py-2 px-10 z-50"
    :class="
      status !== 201 && status !== 200 && status !== 204
        ? 'text-red-800 bg-red-200'
        : 'text-green-800 bg-green-200'
    "
  >
    <h1 class="text-2xl font-bold">
      {{ status === 404 ? "Fail !!!" : "Success !!!" }}
    </h1>
    <h1 v-if="status === 201" class="itbms-message text-lg">
      The sale item has been successfully added.
    </h1>
    <h1 v-else-if="status === 200" class="itbms-message">
      The sale item has been updated.
    </h1>
    <h1 v-else-if="status === 204" class="itbms-message">
      The sale item has been deleted.
    </h1>
    <h1 v-else-if="status === 404" class="itbms-message">
      The requested sale item does not exist.
    </h1>
    <h1 v-else>Something wrong.</h1>
  </div>
</template>

<style scoped>
.message-container {
  animation-name: slide;
  animation-duration: 4s;
  animation-direction: alternate;
  animation-iteration-count: 2;
  right: -400px;
}
@keyframes slide {
  from {
    right: -400px;
  }
  to {
    right: 0;
  }
}
</style>
