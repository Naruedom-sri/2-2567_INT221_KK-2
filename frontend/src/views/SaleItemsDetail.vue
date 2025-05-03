<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { getDataById } from "@/libs/api";
import Header from "@/components/Header.vue";
const {
  params: { itemId },
} = useRoute();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const item = ref({});
const getItem = async () => {
  item.value = await getDataById(`${BASE_API_DOMAIN}/v1/sale-items`, itemId);
};
onMounted(() => getItem());
</script>

<template>
  <div class="detail-container h-screen bg-black">
    <Header />
    <div class="itbms-row grid grid-cols-2 text-xl mt-6 text-white">
      <div class="detail-content-left">
        <img
          src="/src/assets/imgs/iphone-item-detail.png"
          alt="iphone-item"
          class="mx-auto"
        />
      </div>
      <div class="detail-content-right self-center mr-5 space-y-4">
        <h1 class="itbms-model text-4xl text-shadow-xs text-shadow-white">
          {{ item.model }}
        </h1>
        <p class="itbms-description text-lg">{{ item.description }}</p>
        <h1>Capacity</h1>
        <div class="capacity flex w-fit rounded-lg px-3 bg-blue-500 text-lg">
          <p class="itbms-ramGb">
            {{ item.ramGb === null ? "-" : item.ramGb
            }}<span v-show="item.ramGb !== null" class="itbms-ramGb-unit">
              GB</span
            >
          </p>
          <p class="mx-2">+</p>
          <p class="itbms-storageGb">
            {{ item.storageGb === null ? "-" : item.storageGb
            }}<span
              v-show="item.storageGb !== null"
              class="itbms-storageGb-unit"
            >
              GB</span
            >
          </p>
        </div>
        <h1>
          Color:
          <span class="itbms-color w-fit text-lg">{{
            item.color === null ? "-" : item.color
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
        <p class="itbms-price text-3xl text-red-500">
          <span class="itbms-price-unit text-white">Baht</span> {{ item.price }}
        </p>
        <div class="btn-add-buy space-x-32 mt-7">
          <button
            class="w-56 py-3 rounded-2xl bg-blue-500 text-3xl hover:cursor-pointer hover:bg-blue-500/80"
          >
            Add to cart
          </button>
          <button
            class="w-56 py-3 rounded-2xl border border-blue-500 text-3xl text-blue-500 hover:cursor-pointer hover:bg-blue-500 hover:text-white duration-300"
          >
            Buy
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
              {{ item.screenSizeInch === null ? "-" : item.screenSizeInch }}
              <span
                v-show="item.screenSizeInch !== null"
                class="itbms-screenSizeInch-unit"
                >Inch</span
              >
            </p>
            <div class="memory flex px-24">
              <p class="itbms-ramGb">
                Ram
                {{ item.ramGb === null ? "-" : item.ramGb
                }}<span v-show="item.ramGb !== null" class="itbms-ramGb-unit">
                  GB</span
                >
              </p>
              <p class="mx-1">/</p>
              <p class="itbms-storageGb">
                Rom
                {{ item.storageGb === null ? "-" : item.storageGb
                }}<span
                  v-show="item.storageGb !== null"
                  class="itbms-storageGb-unit"
                >
                  GB</span
                >
              </p>
            </div>

            <p class="itbms-color px-24 py-3 bg-black">
              {{ item.color === null ? "-" : item.color }}
            </p>
            <p class="itbms-quantity px-24">
              {{ item.quantity }}
              <span class="itbms-quantity-unit">units</span>
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
