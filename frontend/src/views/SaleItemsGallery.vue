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
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const countImg = ref(1);

const brandFilterList = ref([]);
const pageSize = ref(10);
const isSort = ref({ sortFiled: "createOn", sortDirection: "none" });
const isShowAllBrand = ref(false);
const totalPage = ref(0);
const pageList = ref([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]);
const isLastPage = ref();
const isFirstPage = ref();
const indexPage = ref(0);
const tempIndexPage = ref(0);
const params = new URLSearchParams();

const onHover = ref(null);
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

const animations = ref([false, false, false, false]);
const phoneLabels = [
  "Galaxy S25 Ultra",
  "Pixel 9 Pro",
  "iPhone 12 Pro Max",
  "Galaxy S25 | S25+",
];
const setAnimation = () => {
  animations.value.forEach((boolean, index) => {
    setTimeout(() => {
      animations.value[index] = true;
    }, index * 200);
  });
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

  getAllSaleItemBySortAndFilter();
  getAllBrand();
  setAnimation();
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
        <div class="my-8 flex justify-center gap-5">
          <h1
            v-for="(phoneName, index) in phoneLabels"
            :key="index"
            @click="countImg = index + 1"
            class="w-44 py-2 text-center hover:cursor-pointer"
            :class="[
              countImg === index + 1
                ? 'underline'
                : 'hover:scale-110 duration-200',
              animations[index] ? 'animation-slide-down' : 'opacity-0',
            ]"
          >
            {{ phoneName }}
          </h1>
        </div>
        <div
          class="animation-slide-up relative flex flex-col items-center gap-4 top-125"
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
            <button class="text-base underline">More Detail</button>
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

    <div class="mx-7 pt-7 flex justify-between">
      <div class="gap-2 flex">
        <div
          @click="isShowAllBrand = !isShowAllBrand"
          class="itbms-brand-filter flex flex-wrap items-center gap-2 w-96 px-4 border"
          :class="isShowAllBrand ? 'rounded-t' : 'rounded'"
        >
          <p
            v-if="brandFilterList.length === 0"
            class="text-white/80"
            :class="brandFilterList.length === 0 ? '' : 'py-2'"
          >
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
        <div class="max-h-8 flex gap-2">
          <img
            @mouseenter="onHover = 'filter'"
            @mouseleave="onHover = null"
            @click="isShowAllBrand = !isShowAllBrand"
            :src="`/kk2/imgs/${
              isShowAllBrand || onHover === 'filter' ? 'filter-black' : 'filter'
            }.png`"
            alt="filter"
            class="itbms-brand-filter-button w-8 object-cover border rounded hover:cursor-pointer hover:bg-white"
            :class="isShowAllBrand ? 'bg-white' : ''"
          />
          <button
            @click="clearFilter"
            class="itbms-brand-filter-clear px-7 border rounded hover:bg-white hover:text-black hover:cursor-pointer duration-200"
          >
            Clear
          </button>
        </div>
      </div>
      <div class="sort-page max-h-8 flex gap-2">
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
          @mouseenter="onHover = 'asc'"
          @mouseleave="onHover = null"
          @click="sortAsc"
          :src="`/kk2/imgs/${
            isSort.sortDirection === 'asc' || onHover === 'asc'
              ? 'asc-sort-black'
              : 'asc-sort'
          }.png`"
          alt="asc"
          class="itbms-brand-asc w-8 object-cover border rounded hover:cursor-pointer hover:bg-white"
          :class="isSort.sortDirection === 'asc' ? 'bg-white' : ''"
        />
        <img
          @mouseenter="onHover = 'none'"
          @mouseleave="onHover = null"
          @click="clearSort"
          :src="`/kk2/imgs/${
            isSort.sortDirection === 'none' || onHover === 'none'
              ? 'none-sort-black'
              : 'none-sort'
          }.png`"
          alt="none"
          class="itbms-brand-none w-8 object-cover border rounded hover:cursor-pointer hover:bg-white"
          :class="isSort.sortDirection === 'none' ? 'bg-white' : ''"
        />
        <img
          @mouseenter="onHover = 'desc'"
          @mouseleave="onHover = null"
          @click="sortDesc"
          :src="`/kk2/imgs/${
            isSort.sortDirection === 'desc' || onHover === 'desc'
              ? 'desc-sort-black'
              : 'desc-sort'
          }.png`"
          alt="desc"
          class="itbms-brand-desc w-8 object-cover border rounded hover:cursor-pointer hover:bg-white"
          :class="isSort.sortDirection === 'desc' ? 'bg-white' : ''"
        />
        <RouterLink
          :to="{ name: 'AddSaleItems' }"
          class="itbms-sale-item-add flex items-center justify-center w-8 text-2xl border rounded duration-200 over:cursor-pointer hover:bg-white hover:text-black"
        >
          +
        </RouterLink>
      </div>
    </div>
    <div
      v-if="isShowAllBrand"
      class="dropdown-brand w-96 h-50 mx-7 flex flex-col bg-white text-black text-sm z-50 overflow-y-auto"
      :class="isShowAllBrand ? '' : ''"
    >
      <div
        @click="addToFilterList(brand.name)"
        v-for="(brand, index) in brands"
        :key="index"
        class="itbms-filter-item px-4 py-2 bg-[rgba(22,22,23,255)] border-l text-white hover:bg-blue-500 hover:text-white hover:cursor-pointer duration-200"
        :class="index === brands.length - 1 ? 'border-b' : ''"
      >
        <p>{{ brand.name }}</p>
      </div>
    </div>

    <div
      class="item-container grid grid-cols-5 gap-x-5 gap-y-10 py-10 border-t mt-7 mx-7"
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
        class="itbms-row w-full rounded-2xl shadow-white bg-[rgba(22,22,23,255)] hover:-translate-y-[5%] hover:shadow-sm duration-300"
      >
        <img
          src="/src/assets/imgs/iphone-item.png"
          alt="sale-item"
          class="w-60 mx-auto rounded-4xl"
        />
        <div
          class="item-detail flex flex-col items-center space-y-3 text-white text-lg"
        >
          <p class="itbms-brand text-2xl font-bold">{{ item.brandName }}</p>
          <p class="itbms-model">{{ item.model }}</p>
          <div class="ram-storage flex items-center gap-4 text-xs">
            <p class="itbms-ramGb py-1 w-16 border rounded-xl text-center">
              {{ item.ramGb === null || item.ramGb === "" ? "-" : item.ramGb }}
              <span
                v-show="item.storageGb !== null && item.storageGb !== ''"
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

          <p class="itbms-price text-sm text-white/80">
            From ฿<span class="itbms-price-unit mx-0.5">{{
              item.price.toLocaleString()
            }}</span>
          </p>
          <button
            class="w-60 py-2 mb-5 rounded-2xl bg-blue-500 text-sm hover:cursor-pointer hover:bg-blue-500/90"
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
  <AlertMessageSaleItem v-if="statusStore.getStatus() !== null" />
  <Footer />
</template>

<style scoped>
.animation-opacity {
  animation-name: opacity;
  animation-duration: 1s;
}

.animation-slide-down {
  animation: slide-down 0.5s ease-out;
}
.animation-slide-up {
  animation: slide-up 0.5s ease-out;
}
.dropdown-brand {
  animation-name: opacity-2;
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

@keyframes opacity-2 {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
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
