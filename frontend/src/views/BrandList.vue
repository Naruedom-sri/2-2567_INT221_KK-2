<script setup>
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
import { ref, onMounted } from "vue";
import { getAllBrand, deleteBrandById, getBrandById } from "@/libs/brandApi";
import { useStatusStore } from "@/stores/statusStore";
import AlertMessage from "@/components/AlertMessage.vue";
import Notification from "@/components/Notification.vue";
const statusStore = useStatusStore();
const brands = ref([]);
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const showDialog = ref(false);
const brandName = ref();
const brandId = ref();
const haveSaleItem = ref(false);

const checkHaveSaleItem = async () => {
  try {
    const brand = await getBrandById(`${BASE_API_DOMAIN}`, brandId.value);
    if (brand) {
      haveSaleItem.value = brand.noOfSaleItems !== 0 ? true : false;
    }
  } catch (error) {
    console.log(error);
  }
};

const getAllBrands = async () => {
  try {
    brands.value = await getAllBrand(`${BASE_API_DOMAIN}`);
  } catch (error) {
    console.log(error);
    brands.value = [];
  }
};

const deleteBrand = async (brandId) => {
  statusStore.clearEntityAndMethodAndStatusAndMessage();
  showDialog.value = false;
  try {
    const status = await deleteBrandById(`${BASE_API_DOMAIN}`, brandId);
    if (status === 204) {
      brands.value = brands.value.filter((brand) => brand.id !== brandId);
    }
  } catch (error) {
    console.log(error);
  }
};

onMounted(() => {
  getAllBrands();
});
</script>

<template>
  <NavBar />
  <Notification
    v-if="statusStore.getStatus() !== null"
    :brandName="brandName"
  />
  <div class="brand-container text-white">
    <AlertMessage
      v-if="showDialog"
      :title="haveSaleItem ? 'Warning!' : 'Are you sure?'"
      :message="
        haveSaleItem
          ? `Delete ${brandName} is not allowed. There are sale items with ${brandName} brand.`
          : `Do you want to delete ${brandName} brand?`
      "
      :haveSaleItem="haveSaleItem"
      @confirm="deleteBrand(brandId)"
      @cancel="showDialog = false"
    />
    <div class="promote h-96 flex flex-col justify-center items-center gap-8">
      <h1 class="text-6xl">Elegance in Every Touch</h1>
      <p class="text-white">Designed with heart, felt in every touch.</p>
      <RouterLink
        :to="{ name: 'AddBrand' }"
        class="itbms-add-button w-40 py-2 rounded-2xl bg-blue-500 text-center hover:bg-blue-500/90"
        >Create Brand</RouterLink
      >
    </div>
    <div class="flex py-7 px-10 border-y border-white">
      <RouterLink
        @click="statusStore.clearStatusAndMethod()"
        :to="{ name: 'SaleItemsList' }"
        class="itbms-item-list hover:text-blue-500 hover:cursor-pointer duration-100"
      >
        Sale Item List
      </RouterLink>
      <h1 class="mx-3">/</h1>
      <h1 class="px-3 rounded-2xl bg-gradient-to-r from-pink-400 to-purple-500">
        Brand List
      </h1>
    </div>

    <div class="flex justify-around my-10">
      <div class="w-sm flex bg-[rgba(22,22,23,255)] rounded-2xl">
        <img
          src="/src/assets/imgs/symbol-brand.png"
          alt="brand"
          class="w-36 object-cover"
        />
        <div class="self-center space-y-2">
          <h1 class="text-2xl">Available Brand</h1>
          <p class="w-fit mx-auto p-1 rounded-full text-xl bg-blue-500">
            {{ brands.length }}
          </p>
        </div>
      </div>
    </div>
    <div class="mx-10 my-5">
      <h1 class="text-4xl">Brand</h1>
      <table v-if="brands.length !== 0" class="w-full my-10">
        <tr class="bg-[rgba(22,22,23,255)]">
          <th>Id</th>
          <th>Name</th>
          <th>Action</th>
        </tr>
        <tr v-for="(brand, index) in brands" :key="index" class="itbms-row">
          <td class="itbms-id">{{ brand.id }}</td>
          <td class="itbms-name">{{ brand.name }}</td>
          <td class="flex justify-center gap-5">
            <RouterLink
              :to="{ name: 'EditBrand', params: { brandId: brand.id } }"
              class="itbms-edit-button w-10 border rounded border-blue-500 text-blue-500 hover:bg-blue-500 hover:text-white hover:cursor-pointer duration-200"
            >
              E
            </RouterLink>
            <button
              @click="
                (showDialog = true),
                  (brandName = brand.name),
                  (brandId = brand.id),
                  checkHaveSaleItem()
              "
              class="itbms-delete-button w-10 border rounded border-red-500 text-red-500 hover:bg-red-500 hover:text-white hover:cursor-pointer duration-200"
            >
              D
            </button>
          </td>
        </tr>
      </table>
      <h1
        v-else
        class="itmbs-row py-10 text-white text-5xl text-center border-t"
      >
        no brand
      </h1>
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
