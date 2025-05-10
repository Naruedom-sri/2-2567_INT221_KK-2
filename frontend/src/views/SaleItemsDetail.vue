<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { getDataById } from "@/libs/api";
import { deleteData } from "@/libs/api";
import ConfirmDialog from "@/components/ConfirmDialog.vue";
import ErrorMessage from "@/components/ErrorMessage.vue";
import NavBar from "@/components/์NavBar.vue";

const {
  params: { itemId },
} = useRoute();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const item = ref({});
const errorMessage = ref("");
const router = useRoute()
const showDialog = ref(false);

const getSaleItem = async () => {
  try {
    item.value = await getDataById(`${BASE_API_DOMAIN}/v1/sale-items`, itemId);
  } catch (error) {
    errorMessage.value = "The requested sale item does not exist.";
    item.value = null;
  }
};

const deleteSaleItem = async () => {
  showDialog.value = false
  try {
    const result = await deleteData(`${BASE_API_DOMAIN}/v1/sale-items`, itemId);
    if (result?.status === 204) {
      router.push({ name: "SaleItemsGallery" });
    }
  } catch (error) {
    if (error.response?.status === 404) {
      router.push({ name: "SaleItemsGallery" });
    } else {
      router.push({ name: "SaleItemsGallery" });
    }
  }
};
onMounted(() => getSaleItem());
</script>

<template>
  <div class="detail-container">
    <NavBar />
    <ErrorMessage v-if="item === null" />
    <div v-else class="itbms-row grid grid-cols-2 text-xl text-white">
      <div
        class="flex col-span-2 py-7 mx-20 mb-6 border-b border-white text-base"
      >
        <RouterLink
          :to="{ name: 'SaleItemsGallery' }"
          class="itbms-home-button hover:text-blue-500 hover:cursor-pointer duration-100"
        >
          Home
        </RouterLink>
        <h1 class="mx-1">></h1>
        <h1 class="text-blue-500">
          {{ item.model }} {{ item.ramGb }} / {{ item.storageGb }} GB
          {{ item.color }}
        </h1>
      </div>
      <div class="detail-content-left self-center">
        <img
          src="/src/assets/imgs/iphone-item.png"
          alt="iphone-item"
          class="mx-auto"
        />
      </div>
      <div class="detail-content-right self-center mr-20 space-y-5">
        <h1 class="itbms-model text-4xl text-shadow-white font-extrabold">
          {{ item.model }}
        </h1>
        <p class="itbms-description text-lg">{{ item.description }}</p>
        <h1>Capacity</h1>
        <div class="capacity flex w-fit rounded-lg px-3 bg-blue-500 text-lg">
          <p class="itbms-ramGb">
            {{ item.ramGb === null || item.ramGb === "" ? "-" : item.ramGb
            }}<span
              v-show="item.ramGb !== null && item.ramGb !== ''"
              class="itbms-ramGb-unit"
            >
              GB</span
            >
          </p>
          <p class="mx-2">+</p>
          <p class="itbms-storageGb">
            {{
              item.storageGb === null || item.storageGb === ""
                ? "-"
                : item.storageGb
            }}<span
              v-show="item.storageGb !== null && item.storageGb !== ''"
              class="itbms-storageGb-unit"
            >
              GB</span
            >
          </p>
        </div>

        <h1>
          Color:
          <span class="itbms-color w-fit text-lg">{{
            item.color === null || item.color === "" ? "-" : item.color
          }}</span>
        </h1>
        <p class="w-8 h-8 border bg-orange-500 rounded-full"></p>
        <div class="quantity">
          <h1 class="my-4">Quantity</h1>
          <button
            class="w-7 border rounded-md hover:bg-white hover:text-black hover:cursor-pointer duration-100"
          >
            -
          </button>
          <input
            type="number"
            min="1"
            value="1"
            :max="item.quantity"
            class="mx-3 border rounded-md text-center"
          />
          <button
            class="w-7 border rounded-md hover:bg-white hover:text-black hover:cursor-pointer duration-100"
          >
            +
          </button>
        </div>
        <p class="itbms-price text-4xl text-red-400">
          <span class="itbms-price-unit">฿ </span>
          {{ item.price?.toLocaleString() }}
        </p>
        <div class="btn-add-buy mt-7">
          <div class="flex justify-between gap-4 space-y-5">
            <button  @click="showDialog = true"
              class="itbms-delete-button flex-1 py-3 border rounded-2xl border-red-500 text-3xl text-red-500 hover:bg-red-500 hover:text-white hover:cursor-pointer duration-200"
            >
              Delete
            </button>
            <RouterLink
              :to="{
                name: 'EditSaleItems',
                params: { itemId: item.id },
              }"
              class="itbms-edit-button flex-1"
              ><button
                class="w-full py-3 border rounded-2xl border-blue-500 text-3xl text-blue-500 hover:bg-blue-500 hover:text-white hover:cursor-pointer duration-200"
              >
                Edit
              </button></RouterLink
            >
          </div>
          <button
            class="w-full py-3 rounded-2xl bg-blue-500 text-3xl hover:cursor-pointer hover:bg-blue-500/80"
          >
            Add to cart
          </button>
        </div>
      </div>
      <div class="property col-span-2 px-48 pb-3 mt-6 bg-[rgba(22,22,23,255)]">
        <h1 class="my-10 text-4xl">Properties</h1>
        <div class="flex text-xl">
          <div class="label w-52 space-y-3">
            <h1 class="py-3 px-6 bg-black">Model</h1>
            <h1 class="px-6">Brand</h1>
            <h1 class="py-3 px-6 bg-black">Screen Size</h1>
            <h1 class="px-6">Memory</h1>
            <h1 class="py-3 px-6 bg-black">Color</h1>
            <h1 class="px-6">Available</h1>
          </div>
          <div class="detail w-full space-y-3">
            <p class="itbms-model px-24 py-3 bg-black">
              {{ item.model }}
            </p>
            <p class="itbms-brand px-24">{{ item.brandName }}</p>
            <p class="itbms-screenSizeInch px-24 py-3 bg-black">
              {{
                item.screenSizeInch === null || item.screenSizeInch === ""
                  ? "-"
                  : item.screenSizeInch
              }}
              <span
                v-show="
                  item.screenSizeInch !== null && item.screenSizeInch !== ''
                "
                class="itbms-screenSizeInch-unit"
                >Inches</span
              >
            </p>
            <div class="memory flex px-24">
              <p class="itbms-ramGb">
                Ram
                {{ item.ramGb === null || item.ramGb === "" ? "-" : item.ramGb
                }}<span
                  v-show="item.ramGb !== null && item.ramGb !== ''"
                  class="itbms-ramGb-unit"
                >
                  GB</span
                >
              </p>
              <p class="mx-1">/</p>
              <p class="itbms-storageGb">
                Rom
                {{
                  item.storageGb === null || item.storageGb === ""
                    ? "-"
                    : item.storageGb
                }}<span
                  v-show="item.storageGb !== null && item.storageGb !== ''"
                  class="itbms-storageGb-unit"
                >
                  GB</span
                >
              </p>
            </div>

            <p class="itbms-color px-24 py-3 bg-black">
              {{ item.color === null || item.color === "" ? "-" : item.color }}
            </p>
            <p class="itbms-quantity px-24">
              {{ item.quantity }}
              <span class="itbms-quantity-unit">units</span>
            </p>
          </div>
        </div>
      </div>
    </div>
    <ConfirmDialog
        :visible="showDialog"
        title="Delete Confirmation"
        message="Do you want to delete this sale item?"
        @confirm="deleteSaleItem"
        @cancel="showDialog = false"
    />
  </div>
</template>

<style scoped></style>
