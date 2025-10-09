<script setup>
import { useRouter } from "vue-router";
defineProps({
  orderList: { type: Array, default: [] },
  totalPriceList: { type: Array, default: [] },
  imageUrlList: { type: Array, default: [] },
});
const router = useRouter();
</script>

<template>
  <div
    v-if="orderList.length === 0"
    class="itmbs-row pt-10 text-white text-5xl text-center border-t"
  >
    no order
  </div>
  <div
    v-else
    v-for="(order, indexOrder) in orderList"
    :key="indexOrder"
    class="itbms-row my-4 p-10 rounded bg-gray-200"
    @click="router.push({ name: 'OrderDetail', params: { orderId: order.id } })"
  >
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
        {{ totalPriceList.at(indexOrder).toLocaleString() }}
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
        v-for="(item, indexItem) in order.orderItems"
        :key="indexItem"
        class="itbms-item-row flex items-center gap-4 p-4 rounded bg-[rgba(22,22,23,255)]"
      >
        <div class="item-row-img w-32">
          <img
            v-if="imageUrlList[indexOrder]?.imgOrder[indexItem]"
            :src="imageUrlList[indexOrder]?.imgOrder[indexItem]"
            class="max-w-32 max-h-32 object-cover rounded"
          />
          <img
            v-else
            src="../assets/imgs/no-image.png"
            class="max-w-32 object-cover rounded"
          />
        </div>
        <div class="item-row-detail w-full flex justify-between">
          <p class="itbms-item-description w-52 text-center">
            {{ item.description }}
          </p>
          <p class="itbms-item-quantity w-52 text-center">
            Quantity: <span class="text-white/80">{{ item.quantity }}</span>
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
</template>

<style scoped></style>
