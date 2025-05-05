<script setup>
import { onMounted, ref } from "vue";
import { getAllData } from "@/libs/api";
import Header from "@/components/Header.vue";
const items = ref([]);
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const getAllSaleItems = async () => {
  items.value = await getAllData(`${BASE_API_DOMAIN}/v1/sale-items`);
};
onMounted(() => {
  getAllSaleItems();
});
</script>

<template>
  <div class="gallery-container">
    <Header />
    <div class="search-container border-b border-white mx-7 py-7">
      <div
        class="search-bar flex items-center w-fit mx-auto px-3 border border-white rounded-2xl"
      >
        <input
          type="text"
          placeholder="Search...."
          class="w-xs h-8 outline-0 text-white focus:w-xl transition-all ease-in"
        />
        <img
          src="/src/assets/imgs/search-symbol.png"
          alt="search-symbol"
          class="w-6 object-cover rounded-r-2xl hover:cursor-pointer"
        />
      </div>
    </div>
    <div class="item-container h-full grid grid-cols-5 gap-5 p-7">
      <h1
        v-show="items?.length === 0"
        class="itmbs-row h-screen col-span-5 text-white text-5xl text-center"
      >
        no sale item
      </h1>
      <RouterLink
        v-for="(item, index) in items"
        v-show="items?.length !== 0"
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
          <p class="itbms-brand font-black">{{ item.brandName }}</p>
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
          <p class="itbms-price mt-5 text-xl">
            Baht
            <span class="itbms-price-unit text-red-500">{{
              item.price.toLocaleString()
            }}</span>
          </p>
        </div>
      </RouterLink>
    </div>
  </div>
</template>

<style scoped></style>
