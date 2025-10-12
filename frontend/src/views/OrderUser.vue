<script setup>
import Footer from "@/components/Footer.vue";
import NavBar from "@/components/NavBar.vue";
import { getAllOrder } from "@/libs/userApi";
import { onMounted, onUnmounted, ref } from "vue";
import { decodeToken } from "@/libs/jwtToken";
import { getImageOfSaleItem, getSaleItemById } from "@/libs/saleItemApi";
import OrderList from "@/components/OrderList.vue";
import Notification from "@/components/Notification.vue";
import { useStatusStore } from "@/stores/statusStore";
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const params = new URLSearchParams();
const accessToken = localStorage.getItem("accessToken");
const decoded = decodeToken(accessToken);
const statusStore = useStatusStore();
const orders = ref([]);
const orderCompletedList = ref([]);
const totalPriceCompletedList = ref([]);
const orderCanceledList = ref([]);
const totalPriceCanceledList = ref([]);
const indexPage = ref(0);
const tempIndexPage = ref(0);
const pageSize = ref(10);
const isSort = ref({ sortFiled: "id", sortDirection: "none" });
const pageList = ref([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]);
const isLastPage = ref();
const isFirstPage = ref();
const haveStatus = ref("completed");
const totalPage = ref(0);
const imageUrlCompletedList = ref([]);
const imageUrlCanceledList = ref([]);

const getAllOrderUser = async () => {
  try {
    orderCompletedList.value = [];
    orderCanceledList.value = [];
    totalPriceCompletedList.value = [];
    totalPriceCanceledList.value = [];
    imageUrlCanceledList.value = [];
    imageUrlCompletedList.value = [];

    params.delete("page");
    params.delete("size");
    params.delete("sortField");
    params.delete("sortDirection");

    params.append(
      "page",
      indexPage.value === 9 || indexPage.value === 0
        ? tempIndexPage.value
        : indexPage.value
    );
    params.append("size", pageSize.value);
    params.append("sortField", isSort.value.sortFiled);
    params.append("sortDirection", isSort.value.sortDirection);
    const data = await getAllOrder(
      `${BASE_API_DOMAIN}`,
      decoded.jti,
      accessToken,
      params
    );
    sessionStorage.setItem("pageSize-order", String(pageSize.value));
    sessionStorage.setItem("indexPage-order", String(indexPage.value));
    sessionStorage.setItem("tempIndexPage-order", String(tempIndexPage.value));
    sessionStorage.setItem("sortField-order", isSort.value.sortFiled);
    sessionStorage.setItem("sortDirection-order", isSort.value.sortDirection);
    sessionStorage.setItem("pageList-order", JSON.stringify(pageList.value));

    orders.value = data.content;
    totalPage.value = data.totalPages;
    isLastPage.value = data.last;
    isFirstPage.value = data.first;

    for (const order of orders.value) {
      let totalPrice = 0;
      order.orderItems.forEach((item) => {
        totalPrice += item.quantity * item.price;
      });
      if (order.orderStatus === "COMPLETED") {
        orderCompletedList.value.push(order);
        totalPriceCompletedList.value.push(totalPrice);
      }
      if (order.orderStatus === "CANCELED") {
        orderCanceledList.value.push(order);
        totalPriceCanceledList.value.push(totalPrice);
      }
      await getImageOfAllItem(order.orderStatus, order.orderItems);
    }
  } catch (e) {
    console.log(e);
  }
};

const getImageOfAllItem = async (status, orderItems) => {
  const imgOrder = [];
  for (const item of orderItems) {
    try {
      const data = await getSaleItemById(`${BASE_API_DOMAIN}`, item.saleItemId);
      if (data.saleItemImages.length !== 0) {
        const imgUrl = await getImageOfSaleItem(
          `${BASE_API_DOMAIN}`,
          item.saleItemId,
          1
        );
        imgOrder.push(imgUrl);
      } else {
        imgOrder.push(null);
      }
    } catch (error) {
      imgOrder.push(null);
    }
  }
  if (status === "COMPLETED") {
    imageUrlCompletedList.value.push({ imgOrder });
  } else {
    imageUrlCanceledList.value.push(imgUrl);
  }
};

const clearSort = () => {
  isSort.value.sortFiled = "id";
  isSort.value.sortDirection = "none";
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllOrderUser();
};

const sortAsc = () => {
  isSort.value.sortFiled = "id";
  isSort.value.sortDirection = "asc";
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllOrderUser();
};

const sortDesc = () => {
  isSort.value.sortFiled = "id";
  isSort.value.sortDirection = "desc";
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllOrderUser();
};

const nextNavPage = () => {
  if (
    !isLastPage.value &&
    pageList.value[indexPage.value] !== totalPage.value
  ) {
    pageList.value.push(pageList.value[indexPage.value] + 1);
    pageList.value.shift();
  }
  getAllOrderUser();
};

const previousNavPage = () => {
  if (!isFirstPage.value && pageList.value[indexPage.value] !== 1) {
    pageList.value.unshift(pageList.value[indexPage.value] - 1);
    pageList.value.pop();
  }
  getAllOrderUser();
};

const nextPage = () => {
  indexPage.value += 1;
  tempIndexPage.value += 1;
  if (indexPage.value >= 9) {
    if (indexPage.value !== 9) {
      indexPage.value = 9;
      nextNavPage();
      tempIndexPage.value = pageList.value[indexPage.value] - 1;
    } else {
      indexPage.value = 9;
      tempIndexPage.value = pageList.value[indexPage.value] - 1;
    }
  }
  getAllOrderUser();
};

const previousPage = () => {
  indexPage.value -= 1;
  tempIndexPage.value -= 1;
  if (indexPage.value <= 0) {
    if (indexPage.value !== 0) {
      indexPage.value = 0;
      previousNavPage();
      tempIndexPage.value = pageList.value[indexPage.value] - 1;
    } else {
      indexPage.value = 0;
      tempIndexPage.value = pageList.value[indexPage.value] - 1;
    }
  }
  getAllOrderUser();
};

const clickPageNumber = (numPage) => {
  indexPage.value = pageList.value.findIndex((page) => page === numPage);
  tempIndexPage.value = numPage - 1;
  getAllOrderUser();
};

const firstPage = () => {
  indexPage.value = 0;
  tempIndexPage.value = 0;
  pageList.value = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
  getAllOrderUser();
};

const lastPage = () => {
  tempIndexPage.value = totalPage.value - 1;
  indexPage.value = totalPage.value - 1;
  if (totalPage.value > 10) {
    pageList.value = [];
    let tempTotalPage = totalPage.value;
    for (let index = 0; index < 10; index++) {
      pageList.value.unshift(tempTotalPage--);
    }
    indexPage.value = pageList.value.findIndex(
      (page) => page === totalPage.value
    );
  }
  getAllOrderUser();
};
onMounted(() => {
  const savedSize = sessionStorage.getItem("pageSize-order");
  const savedSortField = sessionStorage.getItem("sortField-order");
  const savedSortDirection = sessionStorage.getItem("sortDirection-order");
  const savedIndexPage = sessionStorage.getItem("indexPage-order");
  const savedTempIndexPage = sessionStorage.getItem("tempIndexPage-order");
  const savedPageList = sessionStorage.getItem("pageList-order");

  if (savedPageList) pageList.value = JSON.parse(savedPageList);
  if (savedSize) pageSize.value = parseInt(savedSize);
  if (savedSortField) isSort.value.sortFiled = savedSortField;
  if (savedSortDirection) isSort.value.sortDirection = savedSortDirection;
  if (savedIndexPage) indexPage.value = parseInt(savedIndexPage);
  if (savedTempIndexPage) tempIndexPage.value = parseInt(savedTempIndexPage);
  getAllOrderUser();
});
onUnmounted(() => {
  imageUrlCompletedList.value.forEach((url) => URL.revokeObjectURL(url));
  imageUrlCanceledList.value.forEach((url) => URL.revokeObjectURL(url));
});
</script>

<template>
  <NavBar />
  <Notification v-if="statusStore.getStatus() !== null" />
  <div class="order-container mx-35 my-10 text-black text-sm">
    <h1 class="text-2xl text-white font-semibold">Your Orders</h1>
    <div class="filter-container flex justify-between my-4 text-white">
      <div class="sort-page flex items-center gap-1 p-2 bg-gray-200 rounded">
        <div class="page space-x-3 text-black">
          <label>show</label>
          <select
            @change="(indexPage = 0), (tempIndexPage = 0), getAllOrderUser()"
            v-model="pageSize"
            class="itbms-page-size border rounded bg-[rgba(22,22,23,255)] text-gray-300"
          >
            <option value="5">5</option>
            <option value="10">10</option>
            <option value="20">20</option>
          </select>
        </div>
        <div>
          <button
            @click="sortAsc"
            class="p-1.5 rounded cursor-pointer hover:bg-[#0d47a1]"
            :class="
              isSort.sortDirection === 'asc'
                ? 'bg-[#0d47a1]'
                : 'bg-[rgba(22,22,23,255)] '
            "
          >
            ↑ A-Z
          </button>
        </div>
        <div>
          <button
            @click="clearSort"
            class="p-1.5 rounded cursor-pointer hover:bg-[#0d47a1]"
            :class="
              isSort.sortDirection === 'none'
                ? 'bg-[#0d47a1]'
                : 'bg-[rgba(22,22,23,255)]'
            "
          >
            Default
          </button>
        </div>
        <div>
          <button
            @click="sortDesc"
            class="p-1.5 rounded cursor-pointer hover:bg-[#0d47a1]"
            :class="
              isSort.sortDirection === 'desc'
                ? 'bg-[#0d47a1]'
                : 'bg-[rgba(22,22,23,255)]'
            "
          >
            ↓ Z-A
          </button>
        </div>
      </div>
      <div
        v-show="orders.length !== 0 && totalPage > 1"
        class="nav-page my-5 gap-1 flex items-center justify-center col-span-5 text-white"
      >
        <button
          @click="firstPage"
          :disabled="isFirstPage"
          class="itbms-page-first px-3 py-1 rounded duration-200"
          :class="
            isFirstPage
              ? 'opacity-60'
              : 'hover:cursor-pointer hover:bg-white hover:text-black'
          "
        >
          First
        </button>
        <button
          @click="previousPage"
          :disabled="isFirstPage"
          class="itbms-page-prev px-3 py-1 rounded duration-200"
          :class="
            isFirstPage
              ? 'opacity-60'
              : 'hover:cursor-pointer hover:bg-white hover:text-black'
          "
        >
          <
        </button>
        <div
          @click="clickPageNumber(page)"
          v-for="(page, index) in totalPage > 10 ? pageList : totalPage"
          :key="index"
          class="px-3 py-1 rounded hover:cursor-pointer hover:bg-white hover:text-black duration-200"
          :class="`itbms-page-${pageList.findIndex(
            (pageNum) => pageNum === page
          )}`"
          v-bind:class="
            indexPage === pageList.findIndex((pageNum) => pageNum === page)
              ? 'bg-white text-black rounded'
              : ''
          "
        >
          <p>{{ page }}</p>
        </div>
        <button
          @click="nextPage"
          :disabled="isLastPage"
          class="itbms-page-next px-3 py-1 rounded duration-200"
          :class="
            isLastPage
              ? 'opacity-60'
              : 'hover:cursor-pointer hover:bg-white hover:text-black '
          "
        >
          >
        </button>
        <button
          @click="lastPage"
          :disabled="isLastPage"
          class="itbms-page-last px-3 py-1 rounded duration-200"
          :class="
            isLastPage
              ? 'opacity-60'
              : 'hover:cursor-pointer hover:bg-white hover:text-black'
          "
        >
          Last
        </button>
      </div>
    </div>
    <div class="completed-cancel space-x-2 my-4 text-white">
      <button
        @click="haveStatus = 'completed'"
        class="py-1 px-2"
        :class="[
          haveStatus === 'completed'
            ? 'text-black bg-gray-200 rounded'
            : 'hover:text-blue-500 cursor-pointer duration-200',
        ]"
      >
        Completed
      </button>
      <button
        @click="haveStatus = 'canceled'"
        class="py-1 px-2"
        :class="[
          haveStatus === 'canceled'
            ? 'text-black bg-gray-200 rounded'
            : 'hover:text-blue-500 cursor-pointer duration-200',
        ]"
      >
        Canceled
      </button>
    </div>
    <OrderList
      v-if="haveStatus === 'completed'"
      :order-list="orderCompletedList"
      :total-price-list="totalPriceCompletedList"
      :image-url-list="imageUrlCompletedList"
    />
    <OrderList
      v-else-if="haveStatus === 'canceled'"
      :order-list="orderCanceledList"
      :total-price-list="totalPriceCanceledList"
      :image-url-list="imageUrlCanceledList"
    />
  </div>
  <Footer />
</template>

<style scoped></style>
