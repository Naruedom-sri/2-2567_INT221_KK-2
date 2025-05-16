<script setup>
import { onMounted, ref } from "vue";
import { getAllData } from "@/libs/api";
import AlertMessage from "@/components/AlertMessage.vue";
import { useSaleItemStatusStore } from "@/stores/SaleItemStatus";
import Footer from "@/components/Footer.vue";
import NavBar from "@/components/์NavBar.vue";
const statusStore = useSaleItemStatusStore();
const items = ref([]);
const time = ref();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
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
  <div class="list-container text-white">
    <div class="promote h-96 flex flex-col justify-center items-center gap-8">
      <h1 class="text-6xl">it's your lifestyle</h1>
      <p class="text-white">Portable, fast to use, new model</p>
      <RouterLink
        :to="{ name: 'AddSaleItems' }"
        class="itbms-sale-item-add w-36 py-2 rounded-2xl bg-blue-500 text-center hover:bg-blue-500/90"
        >Add New</RouterLink
      >
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
        class="itbms-manage-brand w-36 h-full flex justify-center items-center hover:inset-shadow-xs hover:inset-shadow-[rgba(22,22,23,255)] hover:bg-blue-500 hover:cursor-pointer duration-200"
      >
        Manage Brand
      </RouterLink>
    </div>
    <div class="table w-full px-10 my-10">
      <h1 class="text-4xl mb-10">My Product</h1>
      <table class="w-full">
        <tr class="bg-[rgba(22,22,23,255)]">
          <th>Id</th>
          <th>Brand</th>
          <th>Model</th>
          <th>Ram</th>
          <th>Storage</th>
          <th>Color</th>
          <th>Price</th>
          <th>Action</th>
        </tr>
        <tr v-for="(item, index) in items" :key="index" class="itbms-row">
          <td>{{ item.id }}</td>
          <td>{{ item.brandName }}</td>
          <td>{{ item.model }}</td>
          <td>
            {{ item.ramGb === null || item.ramGb === "" ? "-" : item.ramGb }}
          </td>
          <td>
            {{
              item.storageGb === null || item.storageGb === ""
                ? "-"
                : item.storageGb
            }}
          </td>
          <td>
            {{ item.color === null || item.color === "" ? "-" : item.color }}
          </td>
          <td>฿ {{ item.price.toLocaleString() }}</td>
        </tr>
      </table>
    </div>
  </div>
  <Footer />
</template>

<style scoped>
th,
td {
  border: 1px solid;
  text-align: center;
  padding: 10px 0;
}

.promote {
  background-image: url("/src/assets/imgs/bg-gif.gif");
  background-size: contain;
  background-repeat: no-repeat;
}
</style>
