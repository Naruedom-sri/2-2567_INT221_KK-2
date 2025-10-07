<script setup>
import { getOrderById } from "@/libs/orderApi";
import { onMounted, ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
import router from "@/routers";
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const accessToken = localStorage.getItem("accessToken");
const {
  params: { orderId },
} = useRoute();
const order = ref(null);
const totalPrice = ref(0);
const getOrder = async () => {
  try {
    order.value = await getOrderById(BASE_API_DOMAIN, orderId, accessToken);
    let totalPrice = 0;
    order.value.orderItems.forEach((item) => {
      totalPrice += item.quantity * item.price;
    });
  } catch (error) {
    console.log(error);
  }
};
onMounted(() => getOrder());
</script>

<template>
  <NavBar />
  <div class="order-container mx-35 my-10 text-black text-sm">
    <div class="flex gap-2 my-2 text-white text-2xl font-semibold">
      <button
        @click="router.push({ name: 'OrderUser' })"
        class="hover:text-blue-500 duration-200 cursor-pointer"
      >
        Your Orders
      </button>
      <p>-</p>
      <button
        v-if="order !== null"
        class="py-0.5 px-2 rounded-2xl bg-gray-200 text-black"
      >
        {{ order?.seller.nickName }},
        {{ new Date(order?.orderDate).toLocaleString() }}
      </button>
      <button v-else class="py-0.5 px-2 rounded-2xl bg-gray-200 text-black">
        No Order
      </button>
    </div>

    <div v-if="order !== null" class="itbms-row my-4 p-10 rounded bg-gray-200">
      <div class="order-title flex justify-between">
        <h1 class="w-32 font-medium text-center">Seller</h1>
        <h1 class="w-32 font-medium text-center">Order No</h1>
        <h1 class="w-32 font-medium text-center">Order Date</h1>
        <h1 class="w-32 font-medium text-center">Payment Date</h1>
        <h1 class="w-32 font-medium text-center">Total</h1>
        <h1 class="w-32 font-medium text-center">Status</h1>
      </div>
      <div class="order-detail my-3 flex justify-between">
        <p class="itbms-nickname w-32 text-center">
          {{ order.seller.nickName }}
        </p>
        <p class="itbms-order-id w-32 text-center">{{ order.id }}</p>
        <p class="itbms-order-date w-32 text-center">
          {{ new Date(order.orderDate).toLocaleDateString() }}
        </p>
        <p class="itbms-payment-date w-32 text-center">
          {{ new Date(order.paymentDate).toLocaleDateString() }}
        </p>
        <p class="itbms-total-order-price w-32 text-center">
          {{ totalPrice.toLocaleString() }}
        </p>
        <p class="itbms-order-status w-32 text-center">
          {{ order.orderStatus }}
        </p>
      </div>
      <div class="ship-note-order">
        <div class="ship-detail flex gap-1">
          <h1 class="font-medium">Shipped To:</h1>
          <p class="itbms-shipping-address">{{ order.shippingAddress }}</p>
        </div>
        <div class="note-detail my-3 flex gap-1">
          <h1 class="font-medium">Note:</h1>
          <p class="Itbms-order-note">{{ order.orderNote }}</p>
        </div>
      </div>
      <div class="item-row-container space-y-2 text-white">
        <div
          v-for="(item, index) in order.orderItems"
          :key="index"
          class="itbms-item-row flex items-center gap-4 p-4 rounded bg-[rgba(22,22,23,255)]"
        >
          <div class="item-row-img w-32">
            <img
              src="../assets/imgs/no-image.png"
              alt="img"
              class="w-full object-cover"
            />
          </div>
          <div class="item-row-detail w-full flex justify-between">
            <p class="itbms-item-description w-52 text-center">
              {{ item.description }}
            </p>
            <p class="itbms-item-quantity w-52 text-center">
              Quantity: <span class="text-white/80">{{ item.quantity }}</span>
            </p>
            <p>
              Unit Price: <span class="text-white/80">{{ item.price }}</span>
            </p>
            <p class="itbms-item-total-price w-52 text-center">
              Price:
              <span class="text-white/80">{{
                (item.quantity * item.price).toLocaleString()
              }}</span>
            </p>
          </div>
        </div>
      </div>
    </div>
    <h1 v-else class="itmbs-row pt-10 text-white text-5xl text-center border-t">
      no order
    </h1>
  </div>
  <Footer />
</template>

<style scoped></style>
