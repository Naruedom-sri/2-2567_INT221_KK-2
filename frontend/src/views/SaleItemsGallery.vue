<script setup>
import { onMounted, ref } from "vue";
import { getAllData } from "@/libs/api";
import NavBar from "@/components/์NavBar.vue";
import AlertMessageSaleItem from "@/components/AlertMessageSaleItem.vue";
import { useSaleItemStatusStore } from "@/stores/SaleItemStatus";
import Footer from "@/components/Footer.vue";
const statusStore = useSaleItemStatusStore();
const items = ref([]);
const time = ref();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const countImg = ref(1);
const brandFilterList = ref(["Apple", "Samsung"]);
const sizePage = ref(10);
const isFilter = ref(false);
const isSort = ref(false);

const clearFilter = () => {
  brandFilterList.value = [];
  isFilter.value = false;
};

const clearSort = () => {
  isSort.value = false;
};

const updateTime = () => {
  const date = new Date();
  time.value = date.toLocaleTimeString();
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
  <div class="gallery-container text-white">
    <div class="promote text-lg">
      <div
        class="w-full absolute duration-500"
        :class="countImg === 2 || countImg === 4 ? 'text-black' : 'text-white'"
      >
        <div class="my-11 flex justify-center gap-5">
          <h1
            @click="countImg = 1"
            class="w-44 py-2 text-center hover:cursor-pointer"
            :class="countImg === 1 ? 'underline' : 'hover:opacity-80'"
          >
            Galaxy S25 Ultra
          </h1>
          <h1
            @click="countImg = 2"
            class="w-44 py-2 text-center hover:cursor-pointer"
            :class="countImg === 2 ? 'underline' : 'hover:opacity-80'"
          >
            Pixel 9 Pro
          </h1>
          <h1
            @click="countImg = 3"
            class="w-44 py-2 text-center hover:cursor-pointer"
            :class="countImg === 3 ? 'underline' : 'hover:opacity-80'"
          >
            iPhone 12 Pro Max
          </h1>
          <h1
            @click="countImg = 4"
            class="w-44 py-2 text-center hover:cursor-pointer"
            :class="countImg === 4 ? 'underline' : 'hover:opacity-80'"
          >
            Galaxy S25 | S25+
          </h1>
        </div>
        <div class="relative flex flex-col items-center gap-5 top-130">
          <h1 v-if="countImg === 1" class="font-black text-4xl">
            Galaxy S25 Ultra
          </h1>
          <h1 v-else-if="countImg === 2" class="font-black text-4xl">
            Pixel 9 Pro
          </h1>
          <h1 v-else-if="countImg === 3" class="font-black text-4xl">
            iPhone 12 Pro Max
          </h1>
          <h1 v-else class="font-black text-4xl">Galaxy S25 | S25+</h1>
          <div class="space-x-4">
            <button class="text-base underline">More Detail</button>
            <button
              class="px-3 py-2 text-base rounded-4xl duration-500"
              :class="
                countImg === 2 || countImg === 4
                  ? 'bg-black text-white'
                  : 'bg-white text-black'
              "
            >
              Buy Now
            </button>
          </div>
        </div>
      </div>
      <img
        :src="`/kk2/imgs/product-${countImg}.png`"
        alt="product"
        class="img-promote w-full h-[800px] object-cover"
        :class="countImg === 2 ? 'object-top' : ''"
      />
    </div>
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
    <div class="flex justify-between mx-7 mt-7 pb-7 border-b">
      <div class="filter flex gap-2">
        <div
          class="items-brand-filter flex flex-wrap items-center gap-2 w-96 py-2 px-4 border rounded"
        >
          <p v-if="brandFilterList.length === 0" class="text-white/80">
            Filter by brand(s)
          </p>
          <div
            v-else
            v-for="(item, index) in brandFilterList"
            :key="index"
            class="itbms-filter-item flex justify-between bg-blue-500 rounded-2xl text-base"
          >
            <p class="mx-4">{{ item }}</p>
            <button class="w-5 bg-gray-300 rounded-r-2xl text-black">x</button>
          </div>
        </div>
        <img
          src="/src/assets/imgs/filter.png"
          alt="filter"
          class="itbms-brand-filter-button w-10 object-cover border rounded hover:cursor-pointer"
        />
        <button
        @click="clearFilter"
          class="px-7 border rounded hover:bg-white hover:text-black hover:cursor-pointer duration-200"
        >
          Clear
        </button>
      </div>
      <div class="sort-page flex gap-2">
        <div class="page self-center space-x-3 mx-2">
          <label>show</label>
          <select v-model="sizePage" class="border rounded bg-black">
            <option value="5">5</option>
            <option value="10">10</option>
            <option value="20">20</option>
          </select>
        </div>
        <img
          src="/src/assets/imgs/asc-sort.png"
          alt="asc"
          class="w-10 object-cover border rounded"
        />
        <img
          src="/src/assets/imgs/none-sort.png"
          alt="none"
          class="w-10 object-cover border rounded"
        />
        <img
          src="/src/assets/imgs/desc-sort.png"
          alt="desc"
          class="w-10 object-cover border rounded"
        />
      </div>
    </div>

    <div class="item-container grid grid-cols-5 gap-5 p-7">
      <h1
        v-show="items.length === 0"
        class="itmbs-row h-screen col-span-5 text-white text-5xl text-center"
      >
        no sale item
      </h1>
      <RouterLink
        @click="statusStore.clearStatusAndMethod()"
        v-for="(item, index) in items"
        v-show="items.length !== 0"
        :to="{ name: 'SaleItemsDetail', params: { itemId: item.id } }"
        :key="index"
        class="itbms-row w-full pt-4 rounded-4xl shadow-white bg-[rgba(22,22,23,255)] hover:scale-[101%] hover:shadow-sm duration-300"
      >
        <div
          class="item-detail flex flex-col items-center space-y-1 text-white text-lg"
        >
          <p class="itbms-brand font-semibold text-2xl">{{ item.brandName }}</p>
          <p class="itbms-model font-bold">{{ item.model }}</p>
          <p class="itbms-ramGb text-sm">
            {{ item.ramGb === null || item.ramGb === "" ? "-" : item.ramGb }}
            /
            <span class="itbms-storageGb">{{
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
          <p class="itbms-price text-sm text-white/80">
            From ฿<span class="itbms-price-unit mx-0.5">{{
              item.price.toLocaleString()
            }}</span>
          </p>
          <button
            class="py-2 px-4 mt-4 rounded-2xl bg-blue-500 text-sm hover:cursor-pointer hover:bg-blue-500/90"
          >
            Add to Cart
          </button>
        </div>
        <img
          src="/src/assets/imgs/iphone-item.png"
          alt="sale-item"
          class="w-60 mx-auto rounded-4xl"
        />
      </RouterLink>
    </div>
  </div>
  <AlertMessageSaleItem v-if="statusStore.getStatus() !== null" />
  <Footer />
</template>

<style scoped>
.animation {
  animation-name: op;
  animation-duration: 1s;
}
.img-promote {
  z-index: -100;
}
@keyframes op {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>
