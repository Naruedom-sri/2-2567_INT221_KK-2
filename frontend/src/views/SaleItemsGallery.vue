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
  <div class="container-gellery">
    <Header />
    <div class="search-container border-b border-white mx-7 py-5">
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
          class="w-6 object-cover rounded-r-2xl hover:cursor-pointer "
        />
      </div>
    </div>
    <div class="Itbms-row grid grid-cols-5 gap-5 p-7">
      <div
        v-for="(item, index) in items"
        :key="index"
        class="item w-full h-[375px] rounded-4xl  shadow-white bg-[rgba(22,22,23,255)] hover:scale-[101%] hover:shadow-sm duration-300"
      >
        <img
          src="/src/assets/imgs/iphone-item.png"
          alt="sale-item"
          class="w-60 mx-auto rounded-4xl"
        />
        <div class="item-detail mx-6 space-y-1 text-white">
          <p class="Itbms-brand font-bold">{{ item.brand }}</p>
          <p class="Itbms-model">{{ item.model }}</p>
          <p class="Itbms-ramGb">
            {{ item.ramGb }} /<span class="Itbms-storageGb">{{
              item.storageGb
            }}</span
            ><span class="Itbms-storageGb-unit"> GB</span>
          </p>
          <p class="Itbms-price-unit mt-5 text-xl">
            Baht <span class="Itbms-price text-blue-400">{{ item.price }}</span>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
