<script setup>
defineProps({
  title: { type: String },
  message: { type: String },
});

const emit = defineEmits(["ok"]);
const ok = () => emit("ok");
</script>

<template>
  <div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
    <div
      class="animation-slide-up bg-white p-6 space-y-4 rounded-lg shadow w-md text-center"
    >
      <img
        src="/src/assets/imgs/check.png"
        alt="alert-symbol"
        class="w-12 mx-auto"
      />
      <h2 class="font-bold text-black mb-4 text-xl">{{ title }}</h2>
      <p class="itbms-message mb-6 text-gray-700 font-medium">{{ message }}</p>
      <div
        class="flex flex-col gap-4"
      >
        <button
          class="itbms-ok-button bg-green-500 text-white px-4 py-2 rounded hover:cursor-pointer hover:bg-green-400"
          @click="ok"
        >
          OK
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
