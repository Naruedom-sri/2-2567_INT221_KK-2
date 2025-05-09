<script setup>
import NavBar from "@/components/์NavBar.vue";
import { ref, onMounted } from "vue";
import { getAllData, createData, updateSomeData } from "@/libs/api";
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const brands = ref([]);
const brandItem = ref();
const model = ref();
const color = ref();
const description = ref();
const price = ref();
const ramGb = ref();
const storageGb = ref();
const quantity = ref();
const screenSizeInch = ref();

const getAllBrand = async () => {
  brands.value = await getAllData(`${BASE_API_DOMAIN}/v1/brands`);
  try {
  } catch (error) {
    console.log(error);
    brands.value = [];
  }
};
const checkIsEditing = () => {};
const addNewSaleItem = async () => {
  const newItem = {
    model,
    brand: brandItem,
    description,
    price,
    ramGb,
    screenSizeInch,
    quantity,
    storageGb,
    color,
  };
  const item = await createData(`${BASE_API_DOMAIN}/v1/sale-items`, newItem);
  console.log(item);
};
onMounted(() => getAllBrand());
</script>

<template>
  <NavBar />
  <div class="add-edit-container text-white">
    <div class="flex py-7 mx-20 border-b border-white">
      <RouterLink
        :to="{ name: 'SaleItemsGallery' }"
        class="itbms-home-button hover:text-blue-500 hover:cursor-pointer duration-100"
      >
        Home
      </RouterLink>
      <h1 class="mx-1">></h1>
      <h1 class="text-blue-500">New Sale Item</h1>
    </div>
    <form @submit.prevent="addNewSaleItem" class="py-[35px] text-xl">
      <div class="flex mx-20">
        <div class="qualitative flex-1 flex flex-col space-y-4">
          <label>Brand<span>*</span></label>
          <select
            v-model.trim="brandItem"
            required
            class="itbms-brand px-5 py-1 rounded-2xl bg-[rgba(22,22,23,255)]"
          >
            <option
              v-for="(brand, index) in brands"
              :key="index"
              :value="brand"
              class="bg-[rgba(22,22,23,255)]"
            >
              {{ brand.name }}
            </option>
          </select>
          <label>Model<span>*</span> </label>
          <input
            v-model.trim="model"
            required
            type="text"
            class="itbms-model"
          />
          <label>Color</label>
          <input
            v-model.trim="color"
            required
            type="text"
            class="itbms-color"
          />
          <label>Description<span>*</span></label>
          <textarea
            v-model.trim="description"
            required
            class="itbms-description h-32 bg-[rgba(22,22,23,255)]"
          ></textarea>
        </div>
        <div>
          <img src="/src/assets/imgs/iphone-item.png" alt="iphone" />
        </div>
        <div class="quantitative flex-1 flex flex-col space-y-4">
          <label>Price ( ฿ )<span>*</span></label>
          <input
            v-model.trim="price"
            required
            type="number"
            class="itbms-price"
          />

          <label>Ram ( GB )</label>
          <input
            v-model.trim="ramGb"
            type="number"
            class="itbms-ramGb"
            min="0"
          />
          <label>Screen Size ( Inches )</label>
          <input
            v-model.trim="screenSizeInch"
            type="number"
            class="itbms-screenSizeInch"
            min="0"
          />
          <label>Stroage ( GB )</label>
          <input
            v-model.trim="storageGb"
            type="number"
            class="itbms-storageGb"
            min="0"
          />

          <label>Quantity<span>*</span></label>
          <input
            v-model.trim="quantity"
            required
            type="number"
            class="itbms-quantity"
            min="0"
          />
        </div>
      </div>
      <div class="btn-form w-fit mx-auto space-x-4 text-2xl">
        <RouterLink
          :to="{ name: 'SaleItemsGallery' }"
          class="itbms-cancel-button"
          ><button
            class="w-48 py-2 rounded-4xl border border-red-500 text-red-500 hover:cursor-pointer hover:bg-red-500 hover:text-white duration-150"
          >
            Cancel
          </button>
        </RouterLink>
        <button
          type="submit"
          class="itbms-save-button w-48 py-2 rounded-4xl border border-blue-500 text-blue-500 hover:cursor-pointer hover:bg-blue-500 hover:text-white duration-150"
        >
          Add New Item
        </button>
      </div>
    </form>
  </div>
</template>

<style scoped>
input {
  background-color: rgba(22, 22, 23, 255);
  border-radius: 40px;
  padding: 2px 20px;
}
label {
  font-weight: bold;
}
span {
  color: red;
  margin: 0 4px;
}
</style>
