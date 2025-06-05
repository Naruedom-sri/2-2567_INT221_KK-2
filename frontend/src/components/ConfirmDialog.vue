<script setup>
defineProps({
  haveSaleItem: { type: Boolean, default: false },
  visible: Boolean,
  title: { type: String, default: "Are you sure?  " },
  message: { type: String, default: "Do you want to delete this sale item?" },
});

const emit = defineEmits(["confirm", "cancel"]);

const confirm = () => emit("confirm");
const cancel = () => emit("cancel");
</script>

<template>
  <div
    v-if="visible"
    class="fixed inset-0 bg-black/50 flex items-center justify-center z-50"
  >
    <div class="animation-slide-up bg-white p-6 space-y-4 rounded-lg shadow w-md text-center">
      <img
        src="/src/assets/imgs/alert-symbol.png"
        alt="alert-symbol"
        class="w-12 mx-auto"
      />
      <h2 class="font-bold mb-4 text-xl">{{ title }}</h2>
      <p class="itbms-message mb-6 text-gray-600">{{ message }}</p>
      <div class="flex flex-col gap-4">
        <button
          v-if="!haveSaleItem"
          class="itbms-confirm-button bg-red-500 text-white px-4 py-2 rounded hover:cursor-pointer hover:bg-red-500/80"
          @click="confirm"
        >
          Confirm
        </button>
        <button
          class="itbms-cancel-button bg-gray-300 px-4 py-2 rounded hover:cursor-pointer hover:bg-gray-300/80"
          @click="cancel"
        >
          Cancel
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.animation-slide-up {
  animation: slide-up 0.3s ease-in-out;
}
@keyframes slide-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
