<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { getDataById } from "@/libs/api";
import { deleteData } from "@/libs/api";
import ConfirmDialog from "@/components/ConfirmDialog.vue";
import SaleItemNotFound from "@/components/SaleItemNotFound.vue";
import NavBar from "@/components/์NavBar.vue";
import Footer from "@/components/Footer.vue";
import { useSaleItemStatusStore } from "@/stores/SaleItemStatus";
import AlertMessageSaleItem from "@/components/AlertMessageSaleItem.vue";

const {
  params: { itemId },
} = useRoute();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const statusStore = useSaleItemStatusStore();
const item = ref({});
const route = useRouter();
const showDialog = ref(false);

const getSaleItem = async () => {
  try {
    item.value = await getDataById(`${BASE_API_DOMAIN}/v1/sale-items`, itemId);
  } catch (error) {
    console.log(error);
    item.value = null;
  }
};

const deleteSaleItem = async () => {
  showDialog.value = false;
  try {
    const status = await deleteData(`${BASE_API_DOMAIN}/v1/sale-items`, itemId);
    statusStore.setStatusAndMethod("delete", status);
    route.push({ name: "SaleItemsGallery" });
  } catch (error) {
    console.log(error);
  }
};
onMounted(() => getSaleItem());
</script>

<template>
  <NavBar />
  <AlertMessageSaleItem v-show="statusStore.getStatus() !== null" />
  <SaleItemNotFound v-if="item === null" />
  <div v-else class="detail-container">
    <div class="itbms-row grid grid-cols-2 mb-10 text-white text-lg">
      <div
        class="flex col-span-2 py-7 mx-20 mb-6 border-b border-white text-base"
      >
        <RouterLink
          @click="statusStore.clearStatusAndMethod()"
          :to="{ name: 'SaleItemsGallery' }"
          class="itbms-home-button hover:text-blue-500 hover:cursor-pointer duration-100"
        >
          Home
        </RouterLink>
        <h1 class="mx-1">-</h1>
        <h1 class="px-3 rounded-2xl bg-gradient-to-r from-blue-500 to-blue-300">
          {{ item.model }}
          {{ item.color }}
        </h1>
      </div>
      <div class="model-price mx-20 space-y-4">
        <h1 class="itbms-model text-5xl font-semibold">
          <span class="itbms-brand">{{ item.brandName }}</span> {{ item.model }}
        </h1>
        <p class="itbms-price text-white/80 text-lg">
          From
          <span class="itbms-price-unit">฿</span>
          {{ item.price?.toLocaleString() }}
        </p>
        <div class="flex h-4 items-center gap-2">
          <p class="w-3 h-3 rounded-full bg-green-500"></p>
          <h1 class="my-4 text-green-500">Instock</h1>
          <p class="itbms-quantity text-white/80">
            / available {{ item.quantity }}
            <span class="itbms-quantity-unit">units</span>
          </p>
        </div>
      </div>
      <div class="truck-bag flex justify-end gap-6 h-7 mx-20">
        <div class="flex gap-2">
          <img
            src="/src/assets/imgs/truck-symbol.png"
            alt="truck"
            class="object-cover"
          />
          <p class="text-sm self-center">Free shipping</p>
        </div>
        <p>|</p>
        <div class="flex gap-2">
          <img
            src="/src/assets/imgs/bag-symbol.png"
            alt="bag"
            class="object-cover"
          />
          <p class="text-sm self-center">Pick up from Store</p>
        </div>
      </div>
      <div class="detail-content-left self-center">
        <img
          src="/src/assets/imgs/iphone-item.png"
          alt="iphone-item"
          class="mx-auto"
        />
        <div class="w-52 mx-14 flex gap-2">
          <img
            src="/src/assets/imgs/iphone-item.png"
            alt="iphone-item"
            class="object-cover border rounded-xl"
          />
          <img
            src="/src/assets/imgs/iphone-item.png"
            alt="iphone-item"
            class="object-cover border rounded-xl"
          />
          <img
            src="/src/assets/imgs/iphone-item.png"
            alt="iphone-item"
            class="object-cover border rounded-xl"
          />
        </div>
      </div>
      <div class="detail-content-right self-center mr-20 space-y-5">
        <h1 class="text-3xl">Overview</h1>
        <p class="itbms-description">{{ item.description }}</p>
        <h1 class="text-3xl mt-10">
          Model. <span class="text-white/80">Which is best for you?</span>
        </h1>
        <div
          class="detail-model flex justify-between items-center w-md p-3 border rounded-2xl"
        >
          <div>
            <p class="itbms-model">
              {{ item.model }}
            </p>
            <p class="itbms-screenSizeInch text-white/80 text-base">
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
              display
            </p>
          </div>
          <p class="text-base text-white/80">
            From ฿ {{ item.price?.toLocaleString() }}
          </p>
        </div>
        <h1 class="text-3xl mt-10">
          Finish. <span class="text-white/80">Pick your favorite.</span>
        </h1>
        <h1>
          Color -
          <span class="itbms-color w-fit text-lg">{{
            item.color === null || item.color === "" ? "-" : item.color
          }}</span>
        </h1>
        <div class="flex gap-5">
          <p class="w-8 h-8 bg-orange-400 rounded-full"></p>
          <p class="w-8 h-8 bg-green-300 rounded-full"></p>
          <p class="w-8 h-8 bg-purple-300 rounded-full"></p>
          <p class="w-8 h-8 border-4 bg-blue-300 rounded-full"></p>
        </div>

        <h1 class="text-3xl mt-10">
          Storage.
          <span class="text-white/80">How much space do you need?</span>
        </h1>
        <div
          class="detail-ram-rom flex items-center justify-between w-xl px-3 py-5 border rounded-2xl"
        >
          <div class="flex">
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
          <p class="itbms-price text-base text-white/80">
            From<span class="itbms-price-unit"> ฿</span>
            {{ item.price?.toLocaleString() }}
          </p>
        </div>
        <div class="quantity">
          <h1 class="text-3xl mt-10 mb-5">
            Quantity. <span class="text-white/80">How many do you want?</span>
          </h1>
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
        <div class="btn-add-buy mt-10">
          <div class="flex justify-between gap-4 space-y-5">
            <button
              @click="showDialog = true"
              class="itbms-delete-button flex-1 py-3 border rounded-4xl border-red-500 text-3xl text-red-500 hover:bg-red-500 hover:text-white hover:cursor-pointer duration-200"
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
                class="w-full py-3 border rounded-4xl border-blue-500 text-3xl text-blue-500 hover:bg-blue-500 hover:text-white hover:cursor-pointer duration-200"
              >
                Edit
              </button></RouterLink
            >
          </div>
          <button
            class="w-full py-3 rounded-4xl bg-blue-500 text-3xl hover:cursor-pointer hover:bg-blue-500/80"
          >
            Add to Cart
          </button>
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
  <Footer />
</template>

<style scoped></style>
