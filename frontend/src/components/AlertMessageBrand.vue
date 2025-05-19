<script setup>
import { ref, onMounted } from "vue";
import { useSaleItemStatusStore } from "@/stores/SaleItemStatus";
const statusStore = useSaleItemStatusStore();
const status = statusStore.getStatus();
const method = statusStore.getMethod();
const showMessage = ref(true);
const props = defineProps({
  brandName: String,
});

const setIntervalShowMessage = () => {
  console.log(method);
  console.log(status);
  setTimeout(() => {
    showMessage.value = false;
    statusStore.clearStatusAndMethod();
  }, 4000);
};
onMounted(() => setIntervalShowMessage());
</script>

<template>
  <div
    v-show="showMessage"
    class="message-container flex gap-8 fixed right-10 bottom-10 w-96 pl-2.5 pr-7 py-4 rounded-2xl bg-white z-50"
    :class="
      status !== 201 && status !== 200 && status !== 204
        ? 'text-red-700'
        : 'text-green-700 '
    "
  >
    <div
      class="w-2.5 rounded-2xl"
      :class="
        status !== 201 && status !== 200 && status !== 204
          ? 'bg-red-700'
          : 'bg-green-700 '
      "
    ></div>
    <div class="w-full flex flex-col">
      <div class="title flex justify-between">
        <h1 class="text-2xl font-bold">
          {{
            status === 404 || status === 500 || status === 400
              ? "Error!"
              : "Success!"
          }}
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
          The brand has been added.
        </h1>
        <h1 v-else-if="status === 200" class="itbms-message">
          The brand has been updated.
        </h1>
        <h1 v-else-if="status === 204" class="itbms-message">
          The brand has been deleted.
        </h1>
        <h1 v-else-if="status === 404" class="itbms-message">
          An error has occurred, the brand does not exist.
        </h1>
        <h1
          v-else-if="status === 500 || (status === 400 && method === 'add')"
          class="itbms-message"
        >
          The brand could not be added.
        </h1>
        <h1
          v-else-if="status === 400 && method === 'update'"
          class="itbms-message"
        >
          The brand could not be updated.
        </h1>
        <h1
          v-else-if="status === 400 && method === 'delete'"
          class="itbms-message"
        >
          Delete <span class="underline">{{ brandName }}</span> is not allowed.
          There are sale items with
          <span class="underline">{{ brandName }}</span> brand.
        </h1>
        <h1 v-else class="itbms-message">Something wrong.</h1>
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
