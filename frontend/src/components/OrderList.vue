<script setup>
import { useRouter } from "vue-router";

const props = defineProps({
  orderList: { type: Array, default: [] },
  totalPriceList: { type: Array, default: [] },
  imageUrlList: { type: Array, default: [] },
  brandList: { type: Array, default: [] },
  isSeller: { type: Boolean, default: false },
});
const router = useRouter();
const emit = defineEmits(["markAsViewed"]);

const handleClickView = async (orderId) => {
  if (props.isSeller) {
    emit("markAsViewed", orderId);
    sessionStorage.setItem("indexPage-order-seller", 0);
    sessionStorage.setItem("tempIndexPage-order-seller", 0);
    router.push({
      name: "OrderSellerDetail",
      params: { orderId: orderId },
    });
  } else {
    router.push({
      name: "OrderBuyerDetail",
      params: { orderId: orderId },
    });
  }
};
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
  >
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
        {{ isSeller ? order.buyer.userName : order.seller.fullName }}
      </p>
      <p class="itbms-order-id w-32 text-center">{{ order.id }}</p>
      <p class="itbms-order-date w-32 text-center">
        {{ new Date(order.orderDate).toLocaleDateString() }}
      </p>
      <p class="itbms-payment-date w-32 text-center">
        {{ new Date(order.paymentDate).toLocaleDateString() }}
      </p>
      <p class="itbms-total-order-price w-32 text-center">
        {{ totalPriceList.at(indexOrder)?.toLocaleString() }}
      </p>
      <p
        class="itbms-order-status w-32 text-center font-semibold"
        :class="
          order.orderStatus === 'COMPLETED' ? 'text-green-500' : 'text-red-500'
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
    <div class="mb-4">
      <button
        @click="handleClickView(order.id)"
        class="itbms-view-button py-1.5 px-3 bg-blue-500 rounded text-white cursor-pointer hover:bg-blue-600"
      >
        View
      </button>
    </div>
    <div class="item-row-container space-y-2 text-white">
      <div
        v-for="(item, indexItem) in order.orderItems"
        :key="indexItem"
        class="itbms-item-row flex items-center gap-4 p-4 rounded bg-[rgba(22,22,23,255)]"
      >
        <div class="item-row-img w-32">
          <img
            v-if="imageUrlList[indexOrder]?.imgItemOrder[indexItem]"
            :src="imageUrlList[indexOrder]?.imgItemOrder[indexItem]"
            class="max-w-32 max-h-32 object-cover rounded"
          />
          <img
            v-else
            src="../assets/imgs/no-image.png"
            class="max-w-32 object-cover rounded"
          />
        </div>
        <div class="item-row-detail w-full grid grid-cols-4">
          <p class="itbms-item-description text-center">
            {{ brandList[indexOrder]?.brandItemOrder[indexItem] }}
          </p>
          <p class="itbms-item-description text-center">
            {{ item.description }}
          </p>
          <p class="itbms-item-quantity text-center">
            Quantity: <span class="text-white/80">{{ item.quantity }}</span>
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
</template>

<style scoped></style>
