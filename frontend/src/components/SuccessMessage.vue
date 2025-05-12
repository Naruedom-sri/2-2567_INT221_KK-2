<script setup>
import { ref, onMounted } from "vue";
const props = defineProps({
  status: Number,
});
const showMessage = ref(true);
const setIntervalShowMessage = () => {
  setTimeout(() => {
    showMessage.value = false;
  }, 4000);
};
onMounted(() => setIntervalShowMessage());
</script>

<template>
  <div
    v-show="showMessage"
    class="success-container text-green-800 border border-green-800 rounded space-y-3 py-5 px-10 mx-7 mt-7 bg-green-200"
  >
    <h1 class="text-2xl font-bold">Success!</h1>
    <h1 v-if="status === 201" class="itbms-message text-lg">
      The sale item has been successfully added.
    </h1>
    <h1 v-else-if="status === 200" class="itbms-message">
      The sale item has been updated.
    </h1>
    <h1 v-else class="itbms-message">The sale item has been deleted.</h1>
  </div>
</template>

<style scoped></style>
