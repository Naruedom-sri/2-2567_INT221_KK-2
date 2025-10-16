<script setup>
import Footer from "@/components/Footer.vue";
import NavBar from "@/components/NavBar.vue";
import { getAllSellerOrder } from "@/libs/userApi";
import { onMounted, onUnmounted, ref } from "vue";
import { decodeToken } from "@/libs/jwtToken";
import { getImageOfSaleItem, getSaleItemById } from "@/libs/saleItemApi";
import OrderList from "@/components/OrderList.vue";
import { markOrderAsViewed } from "@/libs/orderApi";

const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const params = new URLSearchParams();
const accessToken = localStorage.getItem("accessToken");
const decoded = decodeToken(accessToken);
const orders = ref([]);
const orderIsNotViewed = ref([]);
const totalPriceList = ref([]);
const orderStatus = ref("completed");
const indexPage = ref(0);
const tempIndexPage = ref(0);
const pageSize = ref(10);
const isSort = ref({ sortFiled: "id", sortDirection: "none" });
const pageList = ref([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]);
const isLastPage = ref();
const isFirstPage = ref();
const totalPage = ref(0);
const imageUrlList = ref([]);
const isViewedTap = ref(true);

const getAllUserOrder = async () => {
  try {
    totalPriceList.value = [];
    imageUrlList.value = [];
    orderIsNotViewed.value = [];

    params.delete("page");
    params.delete("size");
    params.delete("sortField");
    params.delete("sortDirection");
    params.delete("orderStatus");

    params.append(
      "page",
      indexPage.value === 9 || indexPage.value === 0
        ? tempIndexPage.value
        : indexPage.value
    );
    params.append("size", pageSize.value);
    params.append("sortField", isSort.value.sortFiled);
    params.append("sortDirection", isSort.value.sortDirection);
    params.append("orderStatus", orderStatus.value);
    const data = await getAllSellerOrder(
      `${BASE_API_DOMAIN}`,
      decoded.jti,
      accessToken,
      params
    );
    sessionStorage.setItem("pageSize-order-seller", String(pageSize.value));
    sessionStorage.setItem("indexPage-order-seller", String(indexPage.value));
    sessionStorage.setItem(
      "tempIndexPage-order-seller",
      String(tempIndexPage.value)
    );
    sessionStorage.setItem("sortField-order-seller", isSort.value.sortFiled);
    sessionStorage.setItem(
      "sortDirection-order-seller",
      isSort.value.sortDirection
    );
    sessionStorage.setItem(
      "pageList-order-seller",
      JSON.stringify(pageList.value)
    );
    sessionStorage.setItem("orderStatus-order-seller", orderStatus.value);
    sessionStorage.setItem("isViewedTap", isViewedTap.value);

    orders.value = data.content;
    totalPage.value = data.totalPages;
    isLastPage.value = data.last;
    isFirstPage.value = data.first;

    for (const order of orders.value) {
      let totalPrice = 0;
      order.orderItems.forEach((item) => {
        totalPrice += item.quantity * item.price;
      });
      totalPriceList.value.push(totalPrice);
      if (!order.isViewed && order.orderStatus === "COMPLETED") {
        orderIsNotViewed.value.push(order);
      }

      await getImageOfAllItem(order.orderItems);
    }
  } catch (e) {
    console.log(e);
  }
};

const getImageOfAllItem = async (orderItems) => {
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
  imageUrlList.value.push({ imgOrder });
};

const updateMarkAsViewed = async (orderId) => {
  try {
    const data = await markOrderAsViewed(BASE_API_DOMAIN, orderId, accessToken);
    console.log(data);
  } catch (error) {
    console.log(error);
  }
};

const clearSort = () => {
  isSort.value.sortFiled = "id";
  isSort.value.sortDirection = "none";
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllUserOrder();
};

const sortAsc = () => {
  isSort.value.sortFiled = "id";
  isSort.value.sortDirection = "asc";
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllUserOrder();
};

const sortDesc = () => {
  isSort.value.sortFiled = "id";
  isSort.value.sortDirection = "desc";
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllUserOrder();
};

const nextNavPage = () => {
  if (
    !isLastPage.value &&
    pageList.value[indexPage.value] !== totalPage.value
  ) {
    pageList.value.push(pageList.value[indexPage.value] + 1);
    pageList.value.shift();
  }
  getAllUserOrder();
};

const previousNavPage = () => {
  if (!isFirstPage.value && pageList.value[indexPage.value] !== 1) {
    pageList.value.unshift(pageList.value[indexPage.value] - 1);
    pageList.value.pop();
  }
  getAllUserOrder();
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
  getAllUserOrder();
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
  getAllUserOrder();
};

const clickPageNumber = (numPage) => {
  indexPage.value = pageList.value.findIndex((page) => page === numPage);
  tempIndexPage.value = numPage - 1;
  getAllUserOrder();
};

const firstPage = () => {
  indexPage.value = 0;
  tempIndexPage.value = 0;
  pageList.value = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
  getAllUserOrder();
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
  getAllUserOrder();
};
onMounted(() => {
  const savedSize = sessionStorage.getItem("pageSize-order-seller");
  const savedSortField = sessionStorage.getItem("sortField-order-seller");
  const savedSortDirection = sessionStorage.getItem(
    "sortDirection-order-seller"
  );
  const savedIndexPage = sessionStorage.getItem("indexPage-order-seller");
  const savedTempIndexPage = sessionStorage.getItem(
    "tempIndexPage-order-seller"
  );
  const savedPageList = sessionStorage.getItem("pageList-order-seller");
  const savedOrderStatus = sessionStorage.getItem("orderStatus-order-seller");
  const savedIsViewedTap = sessionStorage.getItem("isViewedTap");

  if (savedPageList) pageList.value = JSON.parse(savedPageList);
  if (savedSize) pageSize.value = parseInt(savedSize);
  if (savedSortField) isSort.value.sortFiled = savedSortField;
  if (savedSortDirection) isSort.value.sortDirection = savedSortDirection;
  if (savedIndexPage) indexPage.value = parseInt(savedIndexPage);
  if (savedTempIndexPage) tempIndexPage.value = parseInt(savedTempIndexPage);
  if (savedOrderStatus) orderStatus.value = savedOrderStatus;
  if (savedIsViewedTap) isViewedTap.value = savedIsViewedTap === "true";
  getAllUserOrder();
});
onUnmounted(() => {
  imageUrlList.value.forEach((url) => URL.revokeObjectURL(url));
});
</script>

<template>
  <NavBar />
  <div class="order-container mx-35 my-10 text-black text-sm">
    <h1 class="text-2xl text-white font-semibold">Sale Orders</h1>
    <div class="filter-container flex justify-between my-4 text-white">
      <div class="sort-page flex items-center gap-1 p-2 bg-gray-200 rounded">
        <div class="page space-x-3 text-black">
          <label>show</label>
          <select
            @change="(indexPage = 0), (tempIndexPage = 0), getAllUserOrder()"
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
        @click="
          (orderStatus = 'completed'), (isViewedTap = true), getAllUserOrder()
        "
        class="itbms-new-orders-button py-1 px-2"
        :class="[
          orderStatus === 'completed' && isViewedTap
            ? 'text-black bg-gray-200 rounded'
            : 'hover:text-blue-500 cursor-pointer duration-200',
        ]"
      >
        New Orders
      </button>
      <button
        @click="
          (orderStatus = 'cancelled'), (isViewedTap = false), getAllUserOrder()
        "
        class="itbms-canceled-orders-button py-1 px-2"
        :class="[
          orderStatus === 'cancelled'
            ? 'text-black bg-gray-200 rounded'
            : 'hover:text-blue-500 cursor-pointer duration-200',
        ]"
      >
        Canceled
      </button>
      <button
        @click="
          (orderStatus = 'completed'), (isViewedTap = false), getAllUserOrder()
        "
        class="itbms-all-orders-button py-1 px-2"
        :class="[
          orderStatus === 'completed' && !isViewedTap
            ? 'text-black bg-gray-200 rounded'
            : 'hover:text-blue-500 cursor-pointer duration-200',
        ]"
      >
        All Orders
      </button>
    </div>
    <OrderList
      :order-list="isViewedTap ? orderIsNotViewed : orders"
      :total-price-list="totalPriceList"
      :image-url-list="imageUrlList"
      :is-seller="true"
      @mark-as-viewed="updateMarkAsViewed"
    />
  </div>
  <Footer />
</template>

<style scoped></style>
