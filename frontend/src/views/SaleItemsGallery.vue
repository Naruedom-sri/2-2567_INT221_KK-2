<script setup>
import { onMounted, onUnmounted, ref } from "vue";
import {
  getAllData,
  getAllDataWithParam,
  getImageOfData,
  getDataById,
} from "@/libs/api";
import NavBar from "@/components/NavBar.vue";
import AlertMessageSaleItem from "@/components/AlertMessageSaleItem.vue";
import AlertMessageRegister from "@/components/AlertMessageRegister.vue";
import { useSaleItemStatusStore } from "@/stores/SaleItemStatus";
import Footer from "@/components/Footer.vue";
import FilterItem from "@/components/FilterItem.vue";
const statusStore = useSaleItemStatusStore();
const items = ref([]);
const brands = ref([]);
const prices = ref([
  "0 - 5,000",
  "5,001 - 10,000",
  "10,001 - 20,000",
  "20,001 - 30,000",
  "30,001 - 40,000",
  "40,001 - 50,000",
]);
const storages = ref([32, 64, 128, 256, 512, 1, "Not specified"]);
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const countImg = ref(1);

const brandFilterList = ref([]);
const priceFilterList = ref([]);
const storageFilterList = ref([]);
const pageSize = ref(10);
const isSort = ref({ sortFiled: "createOn", sortDirection: "none" });
const isShowAllBrand = ref(false);
const isShowAllPrice = ref(false);
const isShowAllStorage = ref(false);
const totalPage = ref(0);
const pageList = ref([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]);
const isLastPage = ref();
const isFirstPage = ref();
const indexPage = ref(0);
const tempIndexPage = ref(0);
const params = new URLSearchParams();
const itemAnimations = ref([]);
const minPrice = ref("");
const maxPrice = ref("");
const searchContent = ref("");

const getAllSaleItemBySortAndFilter = async (search = null) => {
  try {
    if (search !== null) searchContent.value = search;
    params.delete("page");
    params.delete("size");
    params.delete("sortField");
    params.delete("sortDirection");
    params.delete("filterBrands");
    params.delete("filterStorages");
    params.delete("filterPriceLower");
    params.delete("filterPriceUpper");
    params.delete("searchContent");

    brandFilterList.value.forEach((brand) =>
      params.append("filterBrands", brand)
    );
    storageFilterList.value.forEach((size) =>
      params.append("filterStorages", size === "Not specified" ? null : size)
    );
    params.append(
      "page",
      indexPage.value === 9 || indexPage.value === 0
        ? tempIndexPage.value
        : indexPage.value
    );
    params.append("size", pageSize.value);
    params.append("sortField", isSort.value.sortFiled);
    params.append("sortDirection", isSort.value.sortDirection);
    params.append("filterPriceLower", minPrice.value);
    params.append("filterPriceUpper", maxPrice.value);
    params.append("searchContent", searchContent.value);
    
    const data = await getAllDataWithParam(
      `${BASE_API_DOMAIN}/v2/sale-items`,
      params
    );
    items.value = data.content;
    totalPage.value = data.totalPages;
    isLastPage.value = data.last;
    isFirstPage.value = data.first;
    imageUrlList.value = [];
    getImageOfAllItem();
    setAnimationItems();

    sessionStorage.setItem(
      "filterBrands",
      JSON.stringify(brandFilterList.value)
    );
    sessionStorage.setItem(
      "filterStorages",
      JSON.stringify(storageFilterList.value)
    );
    sessionStorage.setItem(
      "filterPrices",
      JSON.stringify(priceFilterList.value)
    );

    sessionStorage.setItem("pageSize", String(pageSize.value));
    sessionStorage.setItem("indexPage", String(indexPage.value));
    sessionStorage.setItem("tempIndexPage", String(tempIndexPage.value));
    sessionStorage.setItem("sortField", isSort.value.sortFiled);
    sessionStorage.setItem("sortDirection", isSort.value.sortDirection);
    sessionStorage.setItem("pageList", JSON.stringify(pageList.value));
    sessionStorage.setItem("minPrice", String(minPrice.value));
    sessionStorage.setItem("maxPrice", String(maxPrice.value));
    sessionStorage.setItem("searchContent", searchContent.value);
  } catch (error) {
    console.log(error);
    items.value = [];
  }
};

const setAnimationItems = () => {
  itemAnimations.value = [];
  items.value.forEach((_) => {
    itemAnimations.value.push(false);
  });
  itemAnimations.value.forEach((boolean, index) => {
    setTimeout(() => {
      itemAnimations.value[index] = true;
    }, index * 200);
  });
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
    if (indexPage.value !== 9) {
      indexPage.value = 9;
      nextNavPage();
      tempIndexPage.value = pageList.value[indexPage.value] - 1;
    } else {
      indexPage.value = 9;
      tempIndexPage.value = pageList.value[indexPage.value] - 1;
    }
  }
  getAllSaleItemBySortAndFilter();
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

const addToFilterList = (item, className) => {
  if (!brandFilterList.value.includes(item.name) && className === "brand") {
    brandFilterList.value.push(item.name);
    brandFilterList.value.sort();
  } else if (className === "price") {
    priceFilterList.value.splice(0, 1, item);
    const splitArr = priceFilterList.value[0].split(" ");
    minPrice.value = parseInt(splitArr[0].replace(/,/g, ""));
    maxPrice.value = parseInt(splitArr[splitArr.length - 1].replace(/,/g, ""));
  } else if (
    !storageFilterList.value.includes(item) &&
    className === "storage-size"
  ) {
    storageFilterList.value.push(item);
  }
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllSaleItemBySortAndFilter();
};

const applyMinMaxPriceToFilterList = () => {
  let strPriceRange;
  if (minPrice.value === "" && maxPrice.value === "") {
    priceFilterList.value = [];
  } else if (minPrice.value === "") {
    minPrice.value = maxPrice.value;
    strPriceRange =
      minPrice.value.toLocaleString() + " - " + maxPrice.value.toLocaleString();
    priceFilterList.value.splice(0, 1, strPriceRange);
  } else if (maxPrice.value === "") {
    maxPrice.value = minPrice.value;
    strPriceRange =
      minPrice.value.toLocaleString() + " - " + maxPrice.value.toLocaleString();
    priceFilterList.value.splice(0, 1, strPriceRange);
  } else {
    strPriceRange =
      minPrice.value.toLocaleString() + " - " + maxPrice.value.toLocaleString();
    priceFilterList.value.splice(0, 1, strPriceRange);
  }
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllSaleItemBySortAndFilter();
};

const toggleIsShow = (className) => {
  if (className === "brand") isShowAllBrand.value = !isShowAllBrand.value;
  else if (className === "price") isShowAllPrice.value = !isShowAllPrice.value;
  else isShowAllStorage.value = !isShowAllStorage.value;
};

const removeFromFilterList = (item, className) => {
  if (className === "brand") {
    brandFilterList.value = brandFilterList.value.filter(
      (name) => name !== item
    );
  } else if (className === "price") {
    priceFilterList.value = priceFilterList.value.filter(
      (price) => price !== item
    );
    minPrice.value = "";
    maxPrice.value = "";
  } else if (className === "storage-size") {
    storageFilterList.value = storageFilterList.value.filter(
      (size) => size !== item
    );
  }
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllSaleItemBySortAndFilter();
};

const clearFilter = () => {
  brandFilterList.value = [];
  priceFilterList.value = [];
  storageFilterList.value = [];
  minPrice.value = [];
  maxPrice.value = "";
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

const getAllBrand = async () => {
  brands.value = await getAllData(`${BASE_API_DOMAIN}/v1/brands`);
  brands.value.sort((a, b) => a.name.localeCompare(b.name));
  try {
  } catch (error) {
    console.log(error);
    brands.value = [];
  }
};

const phoneLabelAnimations = ref([false, false, false, false]);
const phoneLabels = [
  "Galaxy S25 Ultra",
  "Pixel 9 Pro",
  "iPhone 12 Pro Max",
  "Galaxy S25 | S25+",
];
const setAnimation = () => {
  phoneLabelAnimations.value.forEach((boolean, index) => {
    setTimeout(() => {
      phoneLabelAnimations.value[index] = true;
    }, index * 200);
  });
};
const imageUrlList = ref([]);

const getImageOfAllItem = async () => {
  for (const item of items.value) {
    try {
      const data = await getDataById(
        `${BASE_API_DOMAIN}/v2/sale-items`,
        item.id
      );
      if (data.saleItemImages.length !== 0) {
        const imgUrl = await getImageOfData(
          `${BASE_API_DOMAIN}/v2/sale-items`,
          item.id,
          1
        );
        imageUrlList.value.push(imgUrl);
      }
    } catch (error) {
      imageUrlList.value.push(null);
    }
  }
};

onMounted(() => {
  const savedBrands = sessionStorage.getItem("filterBrands");
  const savedStorages = sessionStorage.getItem("filterStorages");
  const savedPrices = sessionStorage.getItem("filterPrices");
  const savedSize = sessionStorage.getItem("pageSize");
  const savedSortField = sessionStorage.getItem("sortField");
  const savedSortDirection = sessionStorage.getItem("sortDirection");
  const savedIndexPage = sessionStorage.getItem("indexPage");
  const savedTempIndexPage = sessionStorage.getItem("tempIndexPage");
  const savedPageList = sessionStorage.getItem("pageList");
  const savedMinPrice = sessionStorage.getItem("minPrice");
  const savedMaxPrice = sessionStorage.getItem("maxPrice");
  const savedSearchContent = sessionStorage.getItem("searchContent");

  if (savedBrands) {
    try {
      brandFilterList.value = JSON.parse(savedBrands);
      pageList.value = JSON.parse(savedPageList);
    } catch (e) {
      console.error("Failed to parse filter brands", e);
    }
  }
  if (savedStorages) {
    try {
      storageFilterList.value = JSON.parse(savedStorages);
    } catch (e) {
      console.error("Failed to parse filter storages", e);
    }
  }
  if (savedPrices) {
    try {
      priceFilterList.value = JSON.parse(savedPrices);
    } catch (e) {
      console.error("Failed to parse filter prices", e);
    }
  }

  if (savedSize) pageSize.value = parseInt(savedSize);
  if (savedSortField) isSort.value.sortFiled = savedSortField;
  if (savedSortDirection) isSort.value.sortDirection = savedSortDirection;
  if (savedIndexPage) indexPage.value = parseInt(savedIndexPage);
  if (savedTempIndexPage) tempIndexPage.value = parseInt(savedTempIndexPage);
  if (savedMinPrice) minPrice.value = parseInt(savedMinPrice);
  if (savedMaxPrice) maxPrice.value = parseInt(savedMaxPrice);
  if (savedSearchContent) searchContent.value = savedSearchContent;
  getAllBrand();
  getAllSaleItemBySortAndFilter();
  setAnimation();
});
onUnmounted(() => {
  imageUrlList.value.forEach((url) => URL.revokeObjectURL(url));
});
</script>

<template>
  <NavBar @search-sale-item="getAllSaleItemBySortAndFilter" />
  <div class="gallery-container text-white text-sm">
    <div class="promote">
      <div
        class="w-full absolute duration-500"
        :class="countImg === 2 || countImg === 4 ? 'text-black' : 'text-white'"
      >
        <div class="mt-10 flex justify-center gap-15">
          <h1
            v-for="(phoneName, index) in phoneLabels"
            :key="index"
            @click="countImg = index + 1"
            class="w-fit hover:cursor-pointer"
            :class="[
              countImg === index + 1
                ? 'border-b'
                : 'hover:scale-110 duration-200',
              phoneLabelAnimations[index]
                ? 'animation-slide-down'
                : 'opacity-0',
            ]"
          >
            {{ phoneName }}
          </h1>
        </div>
        <div
          class="animation-slide-up relative flex flex-col items-center gap-4 top-132"
        >
          <h1 v-if="countImg === 1" class="font-black text-3xl">
            Galaxy S25 Ultra
          </h1>
          <h1 v-else-if="countImg === 2" class="font-black text-3xl">
            Pixel 9 Pro
          </h1>
          <h1 v-else-if="countImg === 3" class="font-black text-3xl">
            iPhone 12 Pro Max
          </h1>
          <h1 v-else class="font-black text-3xl">Galaxy S25 | S25+</h1>
          <div class="space-x-4">
            <button class="text-base border-b">More Detail</button>
            <button
              class="px-3 py-1 text-base rounded-4xl duration-500"
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
        class="img-promote w-full h-[720px] object-cover border-b"
        :class="countImg === 2 ? 'object-top' : ''"
      />
    </div>

    <div class="filter-container mx-28 py-7 flex justify-between border-b">
      <div
        class="brand-price-filter-container gap-2 p-2 flex bg-gray-300 rounded"
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
        <div>
          <FilterItem
            label="Price Range"
            class="price"
            :is-show="isShowAllPrice"
            :option-list="prices"
            :filter-list="priceFilterList"
            @addToFilterList="addToFilterList"
            @removeFromFilterList="removeFromFilterList"
            @toggleIsShow="toggleIsShow"
          />
          <div v-if="isShowAllPrice" class="flex border-y border-gray-300">
            <input
              type="number"
              placeholder="Min Price"
              v-model="minPrice"
              min="0"
              class="itbms-price-item-min w-28 text-center py-0.5 outline-none bg-[rgba(22,22,23,255)]"
            />
            <input
              type="number"
              placeholder="Max Price"
              v-model="maxPrice"
              min="0"
              class="itbms-price-item-max w-28 text-center py-0.5 outline-none bg-[rgba(22,22,23,255)]"
            />
          </div>
          <button
            v-if="isShowAllPrice"
            @click="applyMinMaxPriceToFilterList"
            class="w-56 py-1 rounded-b duration-200"
            :class="[
              (minPrice > maxPrice && maxPrice !== '') ||
              (minPrice === '' &&
                maxPrice === '' &&
                priceFilterList.length === 0)
                ? 'opacity-50 bg-[rgba(22,22,23,255)] cursor-not-allowed'
                : 'bg-blue-300 border-[#0d47a1] text-[#0d47a1]  hover:bg-[#0d47a1] hover:text-white cursor-pointer',
            ]"
            :disabled="
              (minPrice > maxPrice && maxPrice !== '') ||
              (minPrice === '' &&
                maxPrice === '' &&
                priceFilterList.length === 0)
            "
          >
            Apply
          </button>
          <p
            v-if="minPrice > maxPrice && maxPrice !== ''"
            class="max-w-56 mt-2 text-red-500 text-xs"
          >
            The maximum price should greater than or equal minimum.
          </p>
        </div>
        <FilterItem
          label="Storage Size (s)"
          class="storage-size"
          :is-show="isShowAllStorage"
          :option-list="storages"
          :filter-list="storageFilterList"
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
      <div class="sort-page p-2 flex items-center gap-1 bg-gray-200 rounded">
        <div class="page space-x-3 text-black">
          <label>show</label>
          <select
            @change="
              (indexPage = 0),
                (tempIndexPage = 0),
                getAllSaleItemBySortAndFilter()
            "
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
        <div>
          <RouterLink
            :to="{ name: 'AddSaleItems' }"
            class="itbms-sale-item-add py-1.5 px-3 flex items-center justify-center bg-[rgba(22,22,23,255)] rounded duration-200 over:cursor-pointer hover:bg-[#0d47a1] hover:text-white"
          >
            +
          </RouterLink>
        </div>
      </div>
    </div>
    <div
      class="item-container grid grid-cols-5 gap-x-5 gap-y-10 pb-10 mt-7 mx-35"
    >
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
        class="itbms-row w-full rounded-2xl shadow-white bg-[rgba(22,22,23,255)] hover:-translate-y-[2%] hover:shadow-sm duration-300"
        :class="itemAnimations[index] ? 'animation-slide-up' : 'opacity-0'"
        ><div
          class="h-56 bg-white rounded-t-2xl flex justify-center items-center"
        >
          <img
            v-if="imageUrlList[index]"
            :src="imageUrlList[index]"
            class="max-w-44 object-cover rounded-xl hover:scale-105 duration-500"
          />
          <img
            v-else
            src="../assets/imgs/no-image.png"
            class="max-w-44 object-cover rounded-xl hover:scale-105 duration-500"
          />
        </div>

        <div
          class="item-detail flex flex-col items-center space-y-3 mt-5 text-white"
        >
          <p class="itbms-brand text-2xl font-bold">{{ item.brandName }}</p>
          <p class="itbms-model text-base">{{ item.model }}</p>
          <div class="ram-storage flex items-center gap-4 text-xs">
            <p class="itbms-ramGb py-1 w-16 border rounded-xl text-center">
              {{ item.ramGb === null || item.ramGb === "" ? "-" : item.ramGb }}
              <span
                v-show="item.ramGb !== null && item.ramGb !== ''"
                class="itbms-storageGb-unit"
              >
                GB</span
              >
            </p>

            <p class="itbms-storageGb py-1 w-16 border rounded-xl text-center">
              {{
                item.storageGb === null || item.storageGb === ""
                  ? "-"
                  : item.storageGb
              }}
              <span
                v-show="item.storageGb !== null && item.storageGb !== ''"
                class="itbms-storageGb-unit"
              >
                GB</span
              >
            </p>
          </div>

          <p class="itbms-price text-white/80">
            From ฿<span class="itbms-price-unit mx-0.5">{{
              item.price.toLocaleString()
            }}</span>
          </p>
          <button
            class="px-10 py-2 mb-5 rounded-2xl bg-white text-black hover:bg-[#0d47a1] hover:text-white hover:cursor-pointer duration-200"
          >
            Add to Cart
          </button>
        </div>
      </RouterLink>
      <div
        v-show="items.length !== 0 && totalPage > 1"
        class="nav-page mt-2 gap-1 flex items-center justify-center col-span-5 text-white"
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
  </div>
  <AlertMessageRegister
    v-if="
      statusStore.getStatus() !== null && statusStore.getMethod() === 'register'
    "
  />
  <AlertMessageSaleItem
    v-if="
      statusStore.getStatus() !== null && statusStore.getMethod() !== 'register'
    "
  />
  <Footer />
</template>

<style scoped>
.animation-opacity {
  animation-name: opacity;
  animation-duration: 1s;
}

.animation-slide-down {
  animation: slide-down 0.5s ease-in-out;
}
.animation-slide-up {
  animation: slide-up 0.5s ease-out;
}
.dropdown-brand {
  animation-name: opacity;
  animation-duration: 0.4s;
}

@keyframes slide-down {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slide-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes opacity {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>
