<script setup>
import Footer from "@/components/Footer.vue";
import NavBar from "@/components/NavBar.vue";
import { getAllOrder, getAllSellerOrder } from "@/libs/userApi";
import { onMounted, onUnmounted, ref } from "vue";
import { decodeToken } from "@/libs/jwtToken";
import { getImageOfSaleItem, getSaleItemById } from "@/libs/saleItemApi";
import { markOrderAsViewed } from "@/libs/orderApi";
import OrderList from "@/components/OrderList.vue";
import Notification from "@/components/Notification.vue";
import { useStatusStore } from "@/stores/statusStore";
import { useRouter } from "vue-router";
import { getAllBrand } from "@/libs/brandApi";
import FilterItem from "@/components/FilterItem.vue";

const props = defineProps({
  isSeller: Boolean,
});
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const params = new URLSearchParams();
const accessToken = localStorage.getItem("accessToken");
const decoded = decodeToken(accessToken);
const statusStore = useStatusStore();
const router = useRouter();
const orders = ref([]);
const totalPriceList = ref([]);
const orderStatus = ref(props.isSeller ? "completed" : "all");
const indexPage = ref(0);
const tempIndexPage = ref(0);
const pageSize = ref(10);
const isSort = ref({ sortFiled: "id", sortDirection: "none" });
const pageList = ref([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]);
const isLastPage = ref();
const isFirstPage = ref();
const totalPage = ref(0);
const isViewedTap = ref(props.isSeller ? true : false);
const imageUrlList = ref([]);
const brandList = ref([]);
const brands = ref([]);
const brandFilterList = ref([]);
const isShowAllBrand = ref(false);
const isShowAllUser = ref(false);
const userFilterList = ref([]);
const users = ref([]);

const searchContent = ref("");
const startOrderDate = ref("");
const endOrderDate = ref("");

const getAllOrderUser = async () => {
  try {
    console.log(startOrderDate.value);
    let data = null;
    totalPriceList.value = [];
    imageUrlList.value = [];
    brandList.value = [];

    params.delete("page");
    params.delete("size");
    params.delete("sortField");
    params.delete("sortDirection");
    params.delete("orderStatus");
    params.delete("isViewed");
    params.delete("filterBrands");
    params.delete("filterUsers");
    params.delete("searchContent");
    params.delete("startOrderDate");
    params.delete("endOrderDate");

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
    params.append("searchContent", searchContent.value);
    params.append("startOrderDate", startOrderDate.value);
    params.append("endOrderDate", endOrderDate.value);

    brandFilterList.value.forEach((brand) =>
      params.append("filterBrands", brand)
    );
    userFilterList.value.forEach((user) => params.append("filterUsers", user));

    if (!props.isSeller) {
      data = await getAllOrder(
        `${BASE_API_DOMAIN}`,
        decoded.jti,
        accessToken,
        params,
        router
      );

      sessionStorage.setItem("pageSize-order-buyer", String(pageSize.value));
      sessionStorage.setItem("indexPage-order-buyer", String(indexPage.value));
      sessionStorage.setItem(
        "tempIndexPage-order-buyer",
        String(tempIndexPage.value)
      );
      sessionStorage.setItem("sortField-order-buyer", isSort.value.sortFiled);
      sessionStorage.setItem(
        "sortDirection-order-buyer",
        isSort.value.sortDirection
      );
      sessionStorage.setItem(
        "pageList-order-buyer",
        JSON.stringify(pageList.value)
      );
      sessionStorage.setItem("orderStatus-order-buyer", orderStatus.value);
      sessionStorage.setItem(
        "filterBrands-order-buyer",
        JSON.stringify(brandFilterList.value)
      );

      sessionStorage.setItem(
        "filterUsers-order-buyer",
        JSON.stringify(userFilterList.value)
      );

      sessionStorage.setItem("searchContent-order-buyer", searchContent.value);
      sessionStorage.setItem("start-orderDate-buyer", startOrderDate.value);
      sessionStorage.setItem("end-orderDate-buyer", endOrderDate.value);

      orders.value = data.content;
    } else {
      params.append("isViewed", isViewedTap.value ? false : "");
      data = await getAllSellerOrder(
        `${BASE_API_DOMAIN}`,
        decoded.jti,
        accessToken,
        params,
        router
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

      sessionStorage.setItem(
        "filterBrands-order-seller",
        JSON.stringify(brandFilterList.value)
      );

      sessionStorage.setItem(
        "filterUsers-order-seller",
        JSON.stringify(userFilterList.value)
      );

      sessionStorage.setItem("searchContent-order-seller", searchContent.value);
      sessionStorage.setItem("start-orderDate-seller", startOrderDate.value);
      sessionStorage.setItem("end-orderDate-seller", endOrderDate.value);
      orders.value = data.content;
    }

    totalPage.value = data.totalPages;
    isLastPage.value = data.last;
    isFirstPage.value = data.first;

    for (const order of orders.value) {
      let totalPrice = 0;
      order.orderItems.forEach((item) => {
        totalPrice += item.quantity * item.price;
      });
      totalPriceList.value.push(totalPrice);
      if (!users.value.includes(order.seller.fullName)) {
        if (!props.isSeller) {
          users.value.push(order.seller.fullName);
        } else {
          users.value.push(order.buyer.userName);
        }
      }
      await getImageOfAllItem(order.orderItems);
      await getBrandEachItem(order.orderItems);
    }
  } catch (e) {
    orders.value = [];
    console.log(e);
  }
};

const updateMarkAsViewed = async (orderId) => {
  try {
    const data = await markOrderAsViewed(
      BASE_API_DOMAIN,
      orderId,
      accessToken,
      router
    );
    console.log(data);
  } catch (error) {
    console.log(error);
  }
};

const getBrandEachItem = async (orderItems) => {
  const brandItemOrder = [];
  for (const item of orderItems) {
    try {
      const saleItem = await getSaleItemById(
        `${BASE_API_DOMAIN}`,
        item.saleItemId
      );
      brandItemOrder.push(saleItem.brandName);
    } catch (error) {
      console.log(error);
    }
  }
  brandList.value.push({ brandItemOrder });
};

const getImageOfAllItem = async (orderItems) => {
  const imgItemOrder = [];
  for (const item of orderItems) {
    try {
      const data = await getSaleItemById(`${BASE_API_DOMAIN}`, item.saleItemId);
      if (data.saleItemImages.length !== 0) {
        const imgUrl = await getImageOfSaleItem(
          `${BASE_API_DOMAIN}`,
          item.saleItemId,
          1
        );
        imgItemOrder.push(imgUrl);
      } else {
        imgItemOrder.push(null);
      }
    } catch (error) {
      imgItemOrder.push(null);
    }
  }
  imageUrlList.value.push({ imgItemOrder });
};

const getAllBrands = async () => {
  brands.value = await getAllBrand(`${BASE_API_DOMAIN}`);
  brands.value.sort((a, b) => a.name.localeCompare(b.name));
  try {
  } catch (error) {
    console.log(error);
    brands.value = [];
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

const clearFilter = () => {
  brandFilterList.value = [];
  userFilterList.value = [];
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllOrderUser();
};

const removeFromFilterList = (item, className) => {
  if (className === "brand") {
    brandFilterList.value = brandFilterList.value.filter(
      (name) => name !== item
    );
  } else if (className === "user") {
    userFilterList.value = userFilterList.value.filter((user) => user !== item);
  }
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllOrderUser();
};

const addToFilterList = (item, className) => {
  if (!brandFilterList.value.includes(item.name) && className === "brand") {
    brandFilterList.value.push(item.name);
    brandFilterList.value.sort();
  } else if (!userFilterList.value.includes(item) && className === "user") {
    userFilterList.value.push(item);
  }
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllOrderUser();
};

const toggleIsShow = (className) => {
  if (className === "brand") isShowAllBrand.value = !isShowAllBrand.value;
  else if (className === "user") isShowAllUser.value = !isShowAllUser.value;
};

onMounted(() => {
  if (!props.isSeller) {
    const savedSize = sessionStorage.getItem("pageSize-order-buyer");
    const savedSortField = sessionStorage.getItem("sortField-order-buyer");
    const savedSortDirection = sessionStorage.getItem(
      "sortDirection-order-buyer"
    );
    const savedIndexPage = sessionStorage.getItem("indexPage-order-buyer");
    const savedTempIndexPage = sessionStorage.getItem(
      "tempIndexPage-order-buyer"
    );
    const savedPageList = sessionStorage.getItem("pageList-order-buyer");
    const savedOrderStatus = sessionStorage.getItem("orderStatus-order-buyer");
    const savedSearchContent = sessionStorage.getItem(
      "searchContent-order-buyer"
    );
    const savedStartOrderDate = sessionStorage.getItem("start-orderDate-buyer");
    const savedEndOrderDate = sessionStorage.getItem("end-orderDate-buyer");
    const savedBrands = sessionStorage.getItem("filterBrands-order-buyer");
    const savedUsers = sessionStorage.getItem("filterUsers-order-buyer");

    if (savedBrands) {
      try {
        brandFilterList.value = JSON.parse(savedBrands);
        pageList.value = JSON.parse(savedPageList);
      } catch (e) {
        console.error("Failed to parse filter brands", e);
      }
    }

    if (savedUsers) {
      try {
        userFilterList.value = JSON.parse(savedUsers);
      } catch (e) {
        console.error("Failed to parse filter storages", e);
      }
    }
    if (savedSearchContent) searchContent.value = savedSearchContent;
    if (startOrderDate) startOrderDate.value = savedStartOrderDate;
    if (savedEndOrderDate) endOrderDate.value = savedEndOrderDate;

    if (savedPageList) pageList.value = JSON.parse(savedPageList);
    if (savedSize) pageSize.value = parseInt(savedSize);
    if (savedSortField) isSort.value.sortFiled = savedSortField;
    if (savedSortDirection) isSort.value.sortDirection = savedSortDirection;
    if (savedIndexPage) indexPage.value = parseInt(savedIndexPage);
    if (savedTempIndexPage) tempIndexPage.value = parseInt(savedTempIndexPage);
    if (savedOrderStatus) orderStatus.value = savedOrderStatus;
  } else {
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
    const savedSearchContent = sessionStorage.getItem(
      "searchContent-order-seller"
    );
    const savedStartOrderDate = sessionStorage.getItem(
      "start-orderDate-seller"
    );
    const savedEndOrderDate = sessionStorage.getItem("end-orderDate-seller");

    const savedBrands = sessionStorage.getItem("filterBrands-order-seller");
    const savedUsers = sessionStorage.getItem("filterUsers-order-seller");

    if (savedBrands) {
      try {
        brandFilterList.value = JSON.parse(savedBrands);
        pageList.value = JSON.parse(savedPageList);
      } catch (e) {
        console.error("Failed to parse filter brands", e);
      }
    }

    if (savedUsers) {
      try {
        userFilterList.value = JSON.parse(savedUsers);
      } catch (e) {
        console.error("Failed to parse filter storages", e);
      }
    }
    if (savedSearchContent) searchContent.value = savedSearchContent;
    if (startOrderDate) startOrderDate.value = savedStartOrderDate;
    if (savedEndOrderDate) endOrderDate.value = savedEndOrderDate;
    if (savedPageList) pageList.value = JSON.parse(savedPageList);
    if (savedSize) pageSize.value = parseInt(savedSize);
    if (savedSortField) isSort.value.sortFiled = savedSortField;
    if (savedSortDirection) isSort.value.sortDirection = savedSortDirection;
    if (savedIndexPage) indexPage.value = parseInt(savedIndexPage);
    if (savedTempIndexPage) tempIndexPage.value = parseInt(savedTempIndexPage);
    if (savedOrderStatus) orderStatus.value = savedOrderStatus;
    if (savedIsViewedTap) isViewedTap.value = savedIsViewedTap === "true";
  }
  getAllOrderUser();
  getAllBrands();
});

onUnmounted(() => {
  imageUrlList.value.forEach((url) => URL.revokeObjectURL(url));
});
</script>

<template>
  <NavBar />
  <Notification v-if="statusStore.getStatus() !== null" />
  <div class="order-container mx-35 my-10 text-black text-sm">
    <h1 class="text-2xl text-white font-semibold">
      {{ isSeller ? "Sale Orders" : "Your Orders" }}
    </h1>
    <div
      class="filter-container flex items-center justify-between my-4 text-white"
    >
      <div
        class="brand-seller-filter-container gap-2 p-2 flex bg-gray-300 rounded"
      >
        <FilterItem
          label="Filter by brand (s)"
          class="brand"
          :is-show="isShowAllBrand"
          :option-list="brands"
          :filter-list="brandFilterList"
          @addToFilterList="addToFilterList"
          @removeFromFilterList="removeFromFilterList"
          @toggleIsShow="toggleIsShow"
        />
        <FilterItem
          :label="isSeller ? 'Filter by buyer (s)' : 'Filter by seller (s)'"
          class="user"
          :is-show="isShowAllUser"
          :option-list="users"
          :filter-list="userFilterList"
          @addToFilterList="addToFilterList"
          @removeFromFilterList="removeFromFilterList"
          @toggleIsShow="toggleIsShow"
        />
        <div class="max-h-9 flex gap-2">
          <button
            @click="clearFilter"
            class="itbms-brand-filter-clear px-7 h-10 text-black rounded bg-white hover:bg-[#0d47a1] hover:text-white cursor-pointer duration-200"
          >
            Clear
          </button>
        </div>
      </div>

      <div class="sort-page flex items-center gap-1 p-2 bg-gray-300 rounded">
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
    </div>
    <div
      class="date-nav-page-container flex justify-between items-center text-black"
    >
      <div class="date flex gap-5 text-black">
        <div>
          <label class="text-white">Start Date</label>
          <input
            @change="getAllOrderUser"
            v-model="startOrderDate"
            type="date"
            class="px-2 py-2 ml-2 rounded outline-0 bg-gray-300"
          />
        </div>
        <div>
          <label class="text-white">End Date</label>
          <input
            @change="getAllOrderUser"
            v-model="endOrderDate"
            type="date"
            class="px-2 py-2 ml-2 rounded outline-0 bg-gray-300"
          />
        </div>
      </div>
      <div
        v-show="orders.length !== 0 && totalPage > 1"
        class="nav-page gap-1 flex items-center justify-center text-white"
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
    <div class="search my-4 flex text-black">
      <input
        @keyup.enter="getAllOrderUser"
        v-model.trim="searchContent"
        placeholder="address, note, model"
        type="text"
        class="w-117 px-2 py-2 rounded outline-0 bg-gray-300"
      />
    </div>
    <div class="all-completed-cancel space-x-2 my-4 text-white">
      <button
        v-if="isSeller"
        @click="
          (orderStatus = 'completed'), (isViewedTap = true), getAllOrderUser()
        "
        class="itbms-completed-orders-button py-1 px-2"
        :class="[
          orderStatus === 'completed' && isViewedTap
            ? 'text-black bg-gray-300 rounded'
            : 'hover:text-blue-500 cursor-pointer duration-200',
        ]"
      >
        New Orders
      </button>
      <button
        v-else
        @click="
          (orderStatus = 'completed'), (isViewedTap = false), getAllOrderUser()
        "
        class="itbms-completed-orders-button py-1 px-2"
        :class="[
          orderStatus === 'completed'
            ? 'text-black bg-gray-300 rounded'
            : 'hover:text-blue-500 cursor-pointer duration-200',
        ]"
      >
        Completed
      </button>
      <button
        @click="
          (orderStatus = 'cancelled'), (isViewedTap = false), getAllOrderUser()
        "
        class="itbms-canceled-orders-button py-1 px-2"
        :class="[
          orderStatus === 'cancelled'
            ? 'text-black bg-gray-300 rounded'
            : 'hover:text-blue-500 cursor-pointer duration-200',
        ]"
      >
        Cancelled
      </button>

      <button
        v-if="isSeller"
        @click="
          (orderStatus = 'completed'), (isViewedTap = false), getAllOrderUser()
        "
        class="py-1 px-2"
        :class="[
          orderStatus === 'completed' && !isViewedTap
            ? 'text-black bg-gray-300 rounded'
            : 'hover:text-blue-500 cursor-pointer duration-200',
        ]"
      >
        All Orders
      </button>
      <button
        v-else
        @click="(orderStatus = 'all'), getAllOrderUser()"
        class="py-1 px-2"
        :class="[
          orderStatus === 'all'
            ? 'text-black bg-gray-300 rounded'
            : 'hover:text-blue-500 cursor-pointer duration-200',
        ]"
      >
        All Orders
      </button>
    </div>
    <OrderList
      :order-list="orders"
      :total-price-list="totalPriceList"
      :image-url-list="imageUrlList"
      :brand-list="brandList"
      :is-seller="isSeller"
      @mark-as-viewed="updateMarkAsViewed"
    />
  </div>
  <Footer />
</template>

<style scoped></style>
