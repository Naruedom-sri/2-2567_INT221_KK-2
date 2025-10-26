<script setup>
import { onMounted, ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import { getSaleItemById, getImageOfSaleItem } from "@/libs/saleItemApi";
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
import { getSellerOrderById } from "@/libs/userApi";
import { getOrderById } from "@/libs/orderApi";
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const accessToken = localStorage.getItem("accessToken");
const router = useRouter();
const imageUrlList = ref([]);
const {
  params: { orderId },
} = useRoute();
const props = defineProps({
  isSeller: { type: Boolean, default: false },
});
const order = ref(null);
const totalPrice = ref(0);
const brandList = ref([]);

const getOrder = async () => {
  try {
    if (props.isSeller) {
      order.value = await getSellerOrderById(
        BASE_API_DOMAIN,
        orderId,
        accessToken
      );
    } else {
      order.value = await getOrderById(
        BASE_API_DOMAIN,
        orderId,
        accessToken,
        router
      );
    }
    order.value.orderItems.forEach((item) => {
      totalPrice.value += item.quantity * item.price;
    });
    getImageOfAllItem();
    getBrands();
  } catch (error) {
    console.log(error);
  }
};

const getBrands = async () => {
  for (const item of order.value.orderItems) {
    try {
      const saleItem = await getSaleItemById(
        `${BASE_API_DOMAIN}`,
        item.saleItemId
      );
      brandList.value.push(saleItem.brandName);
    } catch (error) {
      console.log(error);
    }
  }
};

const getImageOfAllItem = async () => {
  for (const item of order.value.orderItems) {
    try {
      const data = await getSaleItemById(`${BASE_API_DOMAIN}`, item.saleItemId);
      if (data.saleItemImages.length !== 0) {
        const imgUrl = await getImageOfSaleItem(
          `${BASE_API_DOMAIN}`,
          item.saleItemId,
          1
        );
        imageUrlList.value.push(imgUrl);
      } else {
        imageUrlList.value.push(null);
      }
    } catch (error) {
      imageUrlList.value.push(null);
    }
  }
};

const handelClickButton = () => {
  if (props.isSeller) router.push({ name: "OrderSeller" });
  else router.push({ name: "OrderBuyer" });
};

onMounted(() => {
  getOrder();
});
</script>

<template>
  <NavBar />
  <div class="order-container mx-35 my-10 text-black text-sm">
    <div class="flex gap-2 my-2 text-white text-2xl font-semibold">
      <button
        @click="handelClickButton"
        class="hover:text-blue-500 duration-200 cursor-pointer"
        :class="
          isSeller ? 'itbms-sale-orders-button' : 'itbms-your-orders-button'
        "
      >
        {{ isSeller ? "Sale Orders" : "Your Orders" }}
      </button>
      <p>-</p>
      <button
        v-if="order !== null"
        class="py-0.5 px-2 rounded-2xl bg-gray-200 text-black"
      >
        {{ isSeller ? order.buyer?.userName : order.seller?.nickName }},
        {{ new Date(order?.orderDate).toLocaleString() }}
      </button>
      <button v-else class="py-0.5 px-2 rounded-2xl bg-gray-200 text-black">
        No Order
      </button>
    </div>

    <div v-if="order !== null" class="itbms-row my-4 p-10 rounded bg-gray-200">
      <div class="order-title flex justify-between">
        <h1 class="w-32 font-medium text-center">
          {{ isSeller ? "Buyer" : "Seller" }}
        </h1>
        <h1 class="w-32 font-medium text-center">Order No</h1>
        <h1 class="w-32 font-medium text-center">Order Date</h1>
        <h1 class="w-32 font-medium text-center">Payment Date</h1>
        <h1 class="w-32 font-medium text-center">Total</h1>
        <h1 class="w-32 font-medium text-center">Status</h1>
      </div>
      <div class="order-detail my-3 flex justify-between">
        <p class="itbms-nickname w-32 text-center">
          {{ isSeller ? order.buyer?.userName : order.seller?.nickName }}
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
        <p
          class="itbms-order-status w-32 text-center font-semibold"
          :class="
            order.orderStatus === 'COMPLETED'
              ? 'text-green-500'
              : 'text-red-500'
          "
        >
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
              v-if="imageUrlList[index]"
              :src="imageUrlList[index]"
              class="max-w-32 max-h-32 object-cover rounded"
            />
            <img
              v-else
              src="../assets/imgs/no-image.png"
              class="max-w-32 object-cover rounded"
            />
          </div>
          <div class="item-row-detail w-full grid grid-cols-5">
            <p class="itbms-item-description text-center">
              {{ brandList[index] }}
            </p>
            <p class="itbms-item-description text-center">
              {{ item.description }}
            </p>
            <p class="itbms-item-quantity text-center">
              Quantity: <span class="text-white/80">{{ item.quantity }}</span>
            </p>
            <p>
              Unit Price:
              <span class="text-white/80">{{
                item.price.toLocaleString()
              }}</span>
            </p>
            <p class="itbms-item-total-price text-center">
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
