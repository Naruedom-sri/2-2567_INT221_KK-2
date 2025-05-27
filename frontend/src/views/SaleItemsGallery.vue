<script setup>
import { onMounted, ref } from "vue";
import { getAllData, getAllDataWithParam } from "@/libs/api";
import NavBar from "@/components/NavBar.vue";
import AlertMessageSaleItem from "@/components/AlertMessageSaleItem.vue";
import { useSaleItemStatusStore } from "@/stores/SaleItemStatus";
import Footer from "@/components/Footer.vue";
const statusStore = useSaleItemStatusStore();
const items = ref([]);
const brands = ref([]);
const time = ref();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const countImg = ref(1);

const brandFilterList = ref([]);
const pageSize = ref(10);
const isSort = ref({ sortFiled: "createOn", sortDirection: "none" });
const indexPage = ref(0);
const isShowAllBrand = ref(false);
const totalPage = ref(0);
const pageList = ref([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]);
const isLastPage = ref();
const isFirstPage = ref();
const tempIndexPage = ref(0);
const params = new URLSearchParams();

const getAllSaleItemBySortAndFilter = async () => {
  try {
    params.delete("page");
    params.delete("size");
    params.delete("sortField");
    params.delete("sortDirection");
    params.delete("filterBrands");

    brandFilterList.value.forEach((brand) =>
      params.append("filterBrands", brand)
    );
    console.log("index page", indexPage.value);
    console.log("temp page", tempIndexPage.value);

    params.append(
      "page",
      indexPage.value === 9 || indexPage.value === 0
        ? tempIndexPage.value
        : indexPage.value
    );
    params.append("size", pageSize.value);
    params.append("sortField", isSort.value.sortFiled);
    params.append("sortDirection", isSort.value.sortDirection);

    sessionStorage.setItem(
      "filterBrands",
      JSON.stringify(brandFilterList.value)
    );
    sessionStorage.setItem("pageSize", String(pageSize.value));
    sessionStorage.setItem("indexPage", String(indexPage.value));
    sessionStorage.setItem("tempIndexPage", String(tempIndexPage.value));
    sessionStorage.setItem("sortField", isSort.value.sortFiled);
    sessionStorage.setItem("sortDirection", isSort.value.sortDirection);
    sessionStorage.setItem("pageList", JSON.stringify(pageList.value));

    const data = await getAllDataWithParam(
      `${BASE_API_DOMAIN}/v2/sale-items`,
      params
    );
    items.value = data.content;
    totalPage.value = data.totalPages;
    isLastPage.value = data.last;
    isFirstPage.value = data.first;
  } catch (error) {
    console.log(error);
    items.value = [];
  }
};

const nextNavPage = () => {
  if (
    !isLastPage.value &&
    pageList.value[indexPage.value] !== totalPage.value
  ) {
    pageList.value.push(pageList.value[indexPage.value] + 1);
    pageList.value.shift();
  }
};

const previousNavPage = () => {
  if (!isFirstPage.value && pageList.value[indexPage.value] !== 1) {
    pageList.value.unshift(pageList.value[indexPage.value] - 1);
    pageList.value.pop();
  }
};

const nextPage = () => {
  indexPage.value += 1;
  tempIndexPage.value += 1;
  if (indexPage.value >= 9) {
    indexPage.value = 9;
    nextNavPage();
    tempIndexPage.value = pageList.value[indexPage.value] - 1;
  }

  getAllSaleItemBySortAndFilter();
};

const previousPage = () => {
  indexPage.value -= 1;
  tempIndexPage.value -= 1;
  if (indexPage.value <= 0) {
    indexPage.value = 0;
    previousNavPage();
    tempIndexPage.value = pageList.value[indexPage.value] - 1;
  }
  getAllSaleItemBySortAndFilter();
};

const clickPageNumber = (numPage) => {
  indexPage.value = pageList.value.findIndex((page) => page === numPage);
  tempIndexPage.value = numPage - 1;
  getAllSaleItemBySortAndFilter();
};

const firstPage = () => {
  indexPage.value = 0;
  tempIndexPage.value = 0;
  pageList.value = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
  getAllSaleItemBySortAndFilter();
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
  getAllSaleItemBySortAndFilter();
};

const addToFilterList = (brandName) => {
  if (!brandFilterList.value.includes(brandName)) {
    brandFilterList.value.push(brandName);
    indexPage.value = 0;
    tempIndexPage.value = 0;
    getAllSaleItemBySortAndFilter();
  }
};

const removeFromFilterList = (brandName) => {
  brandFilterList.value = brandFilterList.value.filter(
    (name) => name !== brandName
  );
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllSaleItemBySortAndFilter();
};

const clearFilter = () => {
  brandFilterList.value = [];
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllSaleItemBySortAndFilter();
};

const clearSort = () => {
  isSort.value.sortFiled = "createOn";
  isSort.value.sortDirection = "none";
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllSaleItemBySortAndFilter();
};

const sortAsc = () => {
  isSort.value.sortFiled = "brand.name";
  isSort.value.sortDirection = "asc";
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllSaleItemBySortAndFilter();
};

const sortDesc = () => {
  isSort.value.sortFiled = "brand.name";
  isSort.value.sortDirection = "desc";
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllSaleItemBySortAndFilter();
};

const updateTime = () => {
  const date = new Date();
  time.value = date.toLocaleTimeString();
  setInterval(updateTime, 1000);
};

const getAllSaleItems = async () => {
  try {
    items.value = await getAllData(`${BASE_API_DOMAIN}/v1/sale-items`);
  } catch (error) {
    console.log(error);
    items.value = [];
  }
};

const getAllBrand = async () => {
  brands.value = await getAllData(`${BASE_API_DOMAIN}/v1/brands`);
  brands.value.sort((a, b) => a.name.localeCompare(b.name));
  try {
  } catch (error) {
    console.log(error);
    brands.value = [];
  }
};

onMounted(() => {
  const savedBrands = sessionStorage.getItem("filterBrands");
  const savedSize = sessionStorage.getItem("pageSize");
  const savedSortField = sessionStorage.getItem("sortField");
  const savedSortDirection = sessionStorage.getItem("sortDirection");
  const savedIndexPage = sessionStorage.getItem("indexPage");
  const savedTempIndexPage = sessionStorage.getItem("tempIndexPage");
  const savedPageList = sessionStorage.getItem("pageList");
  if (savedBrands) {
    try {
      brandFilterList.value = JSON.parse(savedBrands);
      pageList.value = JSON.parse(savedPageList);
    } catch (e) {
      console.error("Failed to parse filter Brands", e);
    }
  }

  if (savedSize) pageSize.value = parseInt(savedSize);
  if (savedSortField) isSort.value.sortFiled = savedSortField;
  if (savedSortDirection) isSort.value.sortDirection = savedSortDirection;
  if (savedIndexPage) indexPage.value = parseInt(savedIndexPage);
  if (savedTempIndexPage) tempIndexPage.value = parseInt(savedTempIndexPage);

  updateTime();
  getAllSaleItemBySortAndFilter();
  getAllBrand();
});
</script>

<template>
  <NavBar />
  <div class="gallery-container text-white">
    <div class="promote text-lg">
      <div
        class="w-full absolute duration-500"
        :class="countImg === 2 || countImg === 4 ? 'text-black' : 'text-white'"
      >
        <div class="my-11 flex justify-center gap-5">
          <h1
            @click="countImg = 1"
            class="w-44 py-2 text-center hover:cursor-pointer"
            :class="countImg === 1 ? 'underline' : 'hover:opacity-80'"
          >
            Galaxy S25 Ultra
          </h1>
          <h1
            @click="countImg = 2"
            class="w-44 py-2 text-center hover:cursor-pointer"
            :class="countImg === 2 ? 'underline' : 'hover:opacity-80'"
          >
            Pixel 9 Pro
          </h1>
          <h1
            @click="countImg = 3"
            class="w-44 py-2 text-center hover:cursor-pointer"
            :class="countImg === 3 ? 'underline' : 'hover:opacity-80'"
          >
            iPhone 12 Pro Max
          </h1>
          <h1
            @click="countImg = 4"
            class="w-44 py-2 text-center hover:cursor-pointer"
            :class="countImg === 4 ? 'underline' : 'hover:opacity-80'"
          >
            Galaxy S25 | S25+
          </h1>
        </div>
        <div class="relative flex flex-col items-center gap-5 top-130">
          <h1 v-if="countImg === 1" class="font-black text-4xl">
            Galaxy S25 Ultra
          </h1>
          <h1 v-else-if="countImg === 2" class="font-black text-4xl">
            Pixel 9 Pro
          </h1>
          <h1 v-else-if="countImg === 3" class="font-black text-4xl">
            iPhone 12 Pro Max
          </h1>
          <h1 v-else class="font-black text-4xl">Galaxy S25 | S25+</h1>
          <div class="space-x-4">
            <button class="text-base underline">More Detail</button>
            <button
              class="px-3 py-2 text-base rounded-4xl duration-500"
              :class="
                countImg === 2 || countImg === 4
                  ? 'bg-black text-white'
                  : 'bg-white text-black'
              "
            >
              Buy Now
            </button>
          </div>
        </div>
      </div>
      <img
        :src="`/kk2/imgs/product-${countImg}.png`"
        alt="product"
        class="img-promote w-full h-[800px] object-cover"
        :class="countImg === 2 ? 'object-top' : ''"
      />
    </div>
    <div
      class="option-for-sale-item flex justify-between items-center h-20 bg-[rgba(22,22,23,255)] text-white"
    >
      <button
        class="filter w-36 h-full hover:inset-shadow-xs hover:inset-shadow-[rgba(22,22,23,255)] hover:bg-blue-500 hover:cursor-pointer duration-200"
      >
        Filter
      </button>
      <p class="text-lg">{{ time }}</p>
      <RouterLink
        :to="{ name: 'AddSaleItems' }"
        class="itbms-sale-item-add w-36 h-full flex justify-center items-center hover:inset-shadow-xs hover:inset-shadow-[rgba(22,22,23,255)] hover:bg-blue-500 hover:cursor-pointer duration-200"
      >
        Add Sale Item
      </RouterLink>
    </div>
    <div class="mx-7 py-7 border-b flex justify-between">
      <div class="gap-2 flex">
        <div
          @click="isShowAllBrand = !isShowAllBrand"
          class="itbms-brand-filter flex flex-wrap items-center gap-2 w-96 py-2 px-4 border rounded"
        >
          <p v-if="brandFilterList.length === 0" class="text-white/80">
            Filter by brand(s)
          </p>
          <div
            v-else
            v-for="(brand, index) in brandFilterList"
            :key="index"
            class="itbms-filter-item flex justify-between bg-blue-500 rounded-2xl text-base"
          >
            <p class="mx-4">{{ brand }}</p>
            <button
              @click="removeFromFilterList(brand)"
              class="itbms-filter-item-clear w-5 bg-gray-300 rounded-r-2xl text-black hover:cursor-pointer"
            >
              x
            </button>
          </div>
        </div>
        <div class="max-h-10 flex gap-2">
          <img
            @click="isShowAllBrand = !isShowAllBrand"
            src="/src/assets/imgs/filter.png"
            alt="filter"
            class="itbms-brand-filter-button w-10 object-cover border rounded hover:cursor-pointer hover:bg-gradient-to-r from-purple-500 to-blue-300"
            :class="
              isShowAllBrand
                ? 'bg-gradient-to-r from-purple-500 to-blue-300'
                : ''
            "
          />
          <button
            @click="clearFilter"
            class="itbms-brand-filter-clear px-7 border rounded hover:bg-white hover:text-black hover:cursor-pointer duration-200"
          >
            Clear
          </button>
        </div>
      </div>
      <div class="sort-page max-h-10 flex gap-2">
        <div class="page self-center space-x-3 mx-2">
          <label>show</label>
          <select
            @change="
              (indexPage = 0),
                (tempIndexPage = 0),
                getAllSaleItemBySortAndFilter()
            "
            v-model="pageSize"
            class="itbms-page-size border rounded bg-black"
          >
            <option value="5">5</option>
            <option value="10">10</option>
            <option value="20">20</option>
          </select>
        </div>
        <img
          @click="sortAsc"
          src="/src/assets/imgs/asc-sort.png"
          alt="asc"
          class="itbms-brand-asc w-10 object-cover border rounded hover:cursor-pointer hover:bg-gradient-to-r from-purple-500 to-blue-300 duration-200"
          :class="
            isSort.sortDirection === 'asc'
              ? 'bg-gradient-to-r from-purple-500 to-blue-300'
              : ''
          "
        />
        <img
          @click="clearSort"
          src="/src/assets/imgs/none-sort.png"
          alt="none"
          class="itbms-brand-none w-10 object-cover border rounded hover:cursor-pointer hover:bg-gradient-to-r from-purple-500 to-blue-300 duration-200"
          :class="
            isSort.sortDirection === 'none'
              ? 'bg-gradient-to-r from-purple-500 to-blue-300'
              : ''
          "
        />
        <img
          @click="sortDesc"
          src="/src/assets/imgs/desc-sort.png"
          alt="desc"
          class="itbms-brand-desc w-10 object-cover border rounded hover:cursor-pointer hover:bg-gradient-to-r from-purple-500 to-blue-300 duration-200"
          :class="
            isSort.sortDirection === 'desc'
              ? 'bg-gradient-to-r from-purple-500 to-blue-300'
              : ''
          "
        />
      </div>
    </div>
    <div
      v-if="isShowAllBrand"
      class="dropdown-brand flex flex-col bg-white text-black text-sm z-50 absolute left-100"
    >
      <div
        @click="addToFilterList(brand.name)"
        v-for="(brand, index) in brands"
        :key="index"
        class="itbms-filter-item px-2 hover:bg-blue-500 hover:text-white hover:cursor-pointer duration-200"
      >
        <p>{{ brand.name }}</p>
      </div>
    </div>

    <div class="item-container clear-both grid grid-cols-5 gap-5 p-7">
      <h1
        v-show="items.length === 0"
        class="itmbs-row h-screen col-span-5 text-white text-5xl text-center"
      >
        no sale item
      </h1>
      <RouterLink
        @click="statusStore.clearStatusAndMethod()"
        v-for="(item, index) in items"
        v-show="items.length !== 0"
        :to="{
          name: 'SaleItemsDetail',
          params: { itemId: item.id },
        }"
        :key="index"
        class="itbms-row w-full pt-4 rounded-4xl shadow-white bg-[rgba(22,22,23,255)] hover:scale-[101%] hover:shadow-sm duration-300"
      >
        <div
          class="item-detail flex flex-col items-center space-y-1 text-white text-lg"
        >
          <p class="itbms-brand font-semibold text-2xl">{{ item.brandName }}</p>
          <p class="itbms-model font-bold">{{ item.model }}</p>
          <p class="itbms-ramGb text-sm">
            {{ item.ramGb === null || item.ramGb === "" ? "-" : item.ramGb }}
            /
            <span class="itbms-storageGb">{{
              item.storageGb === null || item.storageGb === ""
                ? "-"
                : item.storageGb
            }}</span
            ><span
              v-show="item.storageGb !== null && item.storageGb !== ''"
              class="itbms-storageGb-unit"
            >
              GB</span
            >
          </p>
          <p class="itbms-price text-sm text-white/80">
            From ฿<span class="itbms-price-unit mx-0.5">{{
              item.price.toLocaleString()
            }}</span>
          </p>
          <button
            class="py-2 px-4 mt-4 rounded-2xl bg-blue-500 text-sm hover:cursor-pointer hover:bg-blue-500/90"
          >
            Add to Cart
          </button>
        </div>
        <img
          src="/src/assets/imgs/iphone-item.png"
          alt="sale-item"
          class="w-60 mx-auto rounded-4xl"
        />
      </RouterLink>
      <div
        v-if="items.length !== 0"
        class="nav-page mt-2 flex items-center justify-center col-span-5 text-white"
      >
        <button
          @click="firstPage"
          :disabled="isFirstPage"
          class="itbms-page-first px-3 py-1 border rounded-l duration-200"
          :class="
            isFirstPage
              ? 'opacity-60'
              : 'hover:cursor-pointer hover:bg-gradient-to-r from-purple-500 to-blue-300'
          "
        >
          First
        </button>
        <button
          @click="previousPage"
          :disabled="isFirstPage"
          class="itbms-page-prev px-3 py-1 border duration-200"
          :class="
            isFirstPage
              ? 'opacity-60'
              : 'hover:cursor-pointer hover:bg-gradient-to-r from-purple-500 to-blue-300'
          "
        >
          <
        </button>
        <div
          @click="clickPageNumber(page)"
          v-for="(page, index) in totalPage > 10 ? pageList : totalPage"
          :key="index"
          class="px-3 py-1 border hover:cursor-pointer hover:bg-gradient-to-r from-purple-500 to-blue-300 duration-200"
          :class="`itbms-page-${pageList.findIndex(
            (pageNum) => pageNum === page
          )}`"
          v-bind:class="
            indexPage === pageList.findIndex((pageNum) => pageNum === page)
              ? 'bg-gradient-to-r from-purple-500 to-blue-300'
              : ''
          "
        >
          <p>{{ page }}</p>
        </div>
        <button
          @click="nextPage"
          :disabled="isLastPage"
          class="itbms-page-next px-3 py-1 border duration-200"
          :class="
            isLastPage
              ? 'opacity-60'
              : 'hover:cursor-pointer hover:bg-gradient-to-r from-purple-500 to-blue-300 '
          "
        >
          >
        </button>
        <button
          @click="lastPage"
          :disabled="isLastPage"
          class="itbms-page-last px-3 py-1 border rounded-r duration-200"
          :class="
            isLastPage
              ? 'opacity-60'
              : 'hover:cursor-pointer hover:bg-gradient-to-r from-purple-500 to-blue-300'
          "
        >
          Last
        </button>
      </div>
    </div>
  </div>
  <AlertMessageSaleItem v-if="statusStore.getStatus() !== null" />
  <Footer />
</template>

<style scoped>
.animation {
  animation-name: op;
  animation-duration: 1s;
}
.dropdown-brand {
  animation-name: op2;
  animation-duration: 0.4s;
}
.img-promote {
  z-index: -100;
}
@keyframes op2 {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes op {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>
