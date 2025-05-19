<script setup>
import { onMounted, ref } from "vue";
import { getAllData, deleteData } from "@/libs/api";
import AlertMessageSaleItem from "@/components/AlertMessageSaleItem.vue";
import ConfirmDialog from "@/components/ConfirmDialog.vue";
import { useSaleItemStatusStore } from "@/stores/SaleItemStatus";
import Footer from "@/components/Footer.vue";
import NavBar from "@/components/์NavBar.vue";
const statusStore = useSaleItemStatusStore();
const items = ref([]);
const brands = ref([]);
const time = ref();
const itemId = ref();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const showDialog = ref(false);
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

const getAllBrands = async () => {
  try {
    brands.value = await getAllData(`${BASE_API_DOMAIN}/v1/brands`);
  } catch (error) {
    console.log(error);
    brands.value = [];
  }
};
const deleteSaleItem = async (itemId) => {
  statusStore.clearStatusAndMethod();
  showDialog.value = false;
  try {
    const status = await deleteData(`${BASE_API_DOMAIN}/v1/sale-items`, itemId);
    statusStore.setStatusAndMethod("delete", status);
    items.value = items.value.filter((item) => item.id !== itemId);
  } catch (error) {
    console.log(error);
  }
};
onMounted(() => {
  updateTime();
  getAllSaleItems();
  getAllBrands();
});
</script>

<template>
  <NavBar />
  <div class="list-container text-white">
    <div class="promote h-96 flex flex-col justify-center items-center gap-8">
      <h1 class="text-6xl">It's your lifestyle</h1>
      <p class="text-white">Portable, fast to use, new model</p>
      <RouterLink
        :to="{ name: 'AddSaleItems' }"
        class="itbms-sale-item-add w-36 py-2 rounded-2xl bg-blue-500 text-center hover:bg-blue-500/90"
        >Add Model</RouterLink
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
        @click="statusStore.clearStatusAndMethod()"
        :to="{ name: 'BrandList' }"
        class="itbms-manage-brand w-36 h-full flex justify-center items-center hover:inset-shadow-xs hover:inset-shadow-[rgba(22,22,23,255)] hover:bg-blue-500 hover:cursor-pointer duration-200"
      >
        Manage Brand
      </RouterLink>
    </div>
    <AlertMessageSaleItem v-if="statusStore.getStatus() !== null" />
    <div class="table w-full px-10 my-10">
      <div class="flex justify-center mb-10">
        <div class="w-sm flex bg-[rgba(22,22,23,255)] rounded-2xl">
          <img
            src="/src/assets/imgs/symbol-phone.png"
            alt="phone"
            class="w-36 object-cover"
          />
          <div class="self-center space-y-2">
            <h1 class="text-2xl">Available Model</h1>
            <p class="w-fit mx-auto p-1 rounded-full text-xl bg-blue-500">
              {{ items.length }}
            </p>
          </div>
        </div>
      </div>
      <h1 class="text-4xl mb-10">My Product</h1>
      <table v-if="items.length !== 0" class="w-full">
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
          <td class="itbms-id">{{ item.id }}</td>
          <td class="itbms-brand">{{ item.brandName }}</td>
          <td class="itbms-model">{{ item.model }}</td>
          <td class="itbms-ramGb">
            {{ item.ramGb === null || item.ramGb === "" ? "-" : item.ramGb }}
          </td>
          <td class="itbms-storageGb">
            {{
              item.storageGb === null || item.storageGb === ""
                ? "-"
                : item.storageGb
            }}
          </td>
          <td class="itbms-color">
            {{ item.color === null || item.color === "" ? "-" : item.color }}
          </td>
          <td class="itbms-price">฿ {{ item.price.toLocaleString() }}</td>
          <td class="flex justify-center gap-5">
            <RouterLink
              @click="statusStore.clearStatusAndMethod()"
              :to="{ name: 'EditSaleItems', params: { itemId: item.id } }"
              class="itbms-edit-button w-10 border rounded border-blue-500 text-blue-500 hover:bg-blue-500 hover:text-white hover:cursor-pointer duration-200"
              >E</RouterLink
            >
            <button
              @click="(showDialog = true), (itemId = item.id)"
              class="itbms-delete-button w-10 border rounded border-red-500 text-red-500 hover:bg-red-500 hover:text-white hover:cursor-pointer duration-200"
            >
              D
            </button>
          </td>
        </tr>
      </table>
      <h1
        v-else
        class="itmbs-row pt-10 text-white text-5xl text-center border-t"
      >
        no sale item
      </h1>
    </div>
  </div>
  <ConfirmDialog
    :visible="showDialog"
    title="Delete Confirmation"
    message="Do you want to delete this sale item?"
    @confirm="deleteSaleItem(itemId)"
    @cancel="showDialog = false"
  />
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
