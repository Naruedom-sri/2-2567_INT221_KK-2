<script setup>
import { onMounted, ref } from "vue";
import { getAllData } from "@/libs/api";
import NavBar from "@/components/์NavBar.vue";
import SuccessMessage from "@/components/SuccessMessage.vue";
import { useSaleItemStatusStore } from "@/stores/SaleItemStatus";
const statusStore = useSaleItemStatusStore();
const items = ref([]);
const time = ref();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const updateTime = () => {
  const date = new Date();
  time.value = date.toLocaleString();
  setInterval(updateTime, 1000);
};
const getAllSaleItems = async () => {
  try {
    items.value = await getAllData(`${BASE_API_DOMAIN}/v1/sale-items`);
  } catch (error) {
    console.log(error);
    items.value = [];
  }
};
onMounted(() => {
  updateTime();
  getAllSaleItems();
});
</script>

<template>
  <NavBar />
  <div class="gallery-container">
    <video
      src="/src/assets/videos/mobile-preview.mp4"
      autoplay
      loop
      muted
      class="w-full h-[650px] object-cover"
    ></video>
    <div
      class="option-for-sale-item flex justify-between items-center h-20 bg-[rgba(22,22,23,255)] text-white"
    >
      <button
        class="filter w-36 h-full hover:inset-shadow-xs hover:inset-shadow-[rgba(22,22,23,255)] hover:bg-blue-500 hover:cursor-pointer duration-200"
      >
        Filter
      </button>
      <p class="text-lg">{{ time }}</p>
      <RouterLink
        :to="{ name: 'AddSaleItems' }"
        class="itbms-sale-item-add w-36 h-full flex justify-center items-center hover:inset-shadow-xs hover:inset-shadow-[rgba(22,22,23,255)] hover:bg-blue-500 hover:cursor-pointer duration-200"
      >
        Add Sale Item
      </RouterLink>
    </div>
    <SuccessMessage v-show="statusStore.getStatus() !== null" :status="statusStore.getStatus()" />
    <div class="item-container grid grid-cols-5 gap-5 p-7">
      <h1
        v-show="items.length === 0"
        class="itmbs-row h-screen col-span-5 text-white text-5xl text-center"
      >
        no sale item
      </h1>
      <RouterLink
        v-for="(item, index) in items"
        v-show="items.length !== 0"
        :to="{ name: 'SaleItemsDetail', params: { itemId: item.id } }"
        :key="index"
        class="itbms-row w-full h-[375px] rounded-4xl shadow-white bg-[rgba(22,22,23,255)] hover:scale-[101%] hover:shadow-sm duration-300"
      >
        <img
          src="/src/assets/imgs/iphone-item.png"
          alt="sale-item"
          class="w-60 mx-auto rounded-4xl"
        />
        <div class="item-detail mx-6 space-y-1 text-white">
          <p class="itbms-brand font-extrabold">{{ item.brandName }}</p>
          <p class="itbms-model">{{ item.model }}</p>
          <p class="itbms-ramGb">
            {{ item.ramGb === null || item.ramGb === "" ? "-" : item.ramGb }}
            /<span class="itbms-storageGb">{{
              item.storageGb === null || item.storageGb === ""
                ? "-"
                : item.storageGb
            }}</span
            ><span
              v-show="item.storageGb !== null && item.storageGb !== ''"
              class="itbms-storageGb-unit"
            >
              GB</span
            >
          </p>
          <p class="itbms-price mt-3 text-xl text-red-400">
            ฿
            <span class="itbms-price-unit">{{
              item.price.toLocaleString()
            }}</span>
          </p>
        </div>
      </RouterLink>
    </div>
  </div>
</template>

<style scoped></style>
