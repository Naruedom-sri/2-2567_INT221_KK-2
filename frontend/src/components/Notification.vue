<script setup>
import { ref, onMounted } from "vue";
import { useStatusStore } from "@/stores/statusStore";
const statusStore = useStatusStore();
const status = statusStore.getStatus();
const message = statusStore.getMessage();
const showMessage = ref(true);
const setIntervalShowMessage = () => {
  setTimeout(() => {
    showMessage.value = false;
    statusStore.clearEntityAndMethodAndStatusAndMessage();
  }, 5000);
};
onMounted(() => {
  setIntervalShowMessage();
});
</script>

<template>
  <div
    v-show="showMessage"
    class="message-container flex gap-8 fixed right-10 bottom-10 w-96 pl-2.5 pr-7 py-4 rounded-2xl text-sm bg-white z-50"
    :class="status >= 400 ? 'text-red-700' : 'text-green-700'"
  >
    <div
      class="w-2.5 rounded-2xl"
      :class="status >= 400 ? 'bg-red-700' : 'bg-green-700'"
    ></div>
    <div class="w-full flex flex-col">
      <div class="title flex justify-between">
        <h1 class="text-2xl font-bold">
          {{ status >= 400 ? "Error!" : "Success!" }}
        </h1>
        <button
          @click="
            (showMessage = false),
              statusStore.clearEntityAndMethodAndStatusAndMessage()
          "
          class="w-6 h-6 text-center text-black bg-gray-300 rounded-full hover:cursor-pointer"
        >
          x
        </button>
      </div>
      <div class="message w-64">
        <h1 class="itbms-message">
          {{ message }}
        </h1>
      </div>
    </div>
  </div>
</template>

<style scoped>
.message-container {
  animation-name: slide;
  animation-duration: 1s;
}
@keyframes slide {
  from {
    transform: scale(0);
  }
  to {
    transform: scale(100%);
  }
}
</style>
