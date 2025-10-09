<script setup>
import { onMounted, ref } from "vue";
import { useStatusStore } from "@/stores/statusStore";
import Footer from "@/components/Footer.vue";
import NavBar from "@/components/NavBar.vue";
import AlertMessage from "@/components/AlertMessage.vue";
import Notification from "@/components/Notification.vue";
import { deleteSaleItemById } from "@/libs/saleItemApi";
import { getAllSaleItemOfSeller } from "@/libs/userApi";
import { getAllBrand } from "@/libs/brandApi";
import { decodeToken } from "@/libs/jwtToken";
const statusStore = useStatusStore();
const params = new URLSearchParams();
const accessToken = localStorage.getItem("accessToken");
const decoded = decodeToken(accessToken);
const items = ref([]);
const brands = ref([]);
const itemId = ref();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const showDialog = ref(false);
const pageSize = ref(10);
const isSort = ref({ sortFiled: "createOn", sortDirection: "none" });
const pageList = ref([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]);
const isLastPage = ref();
const isFirstPage = ref();
const totalPage = ref(0);
const totalSaleItems = ref(0);
const indexPage = ref(0);
const tempIndexPage = ref(0);

const getAllSaleItems = async () => {
  try {
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

    const data = await getAllSaleItemOfSeller(
      `${BASE_API_DOMAIN}`,
      decoded.jti,
      accessToken,
      params
    );

    sessionStorage.setItem("pageSize-list", String(pageSize.value));
    sessionStorage.setItem("indexPage-list", String(indexPage.value));
    sessionStorage.setItem("tempIndexPage-list", String(tempIndexPage.value));
    sessionStorage.setItem("sortField-list", isSort.value.sortFiled);
    sessionStorage.setItem("sortDirection-list", isSort.value.sortDirection);
    sessionStorage.setItem("pageList-list", JSON.stringify(pageList.value));

    items.value = data.content;
    totalSaleItems.value = data.totalElements;
    totalPage.value = data.totalPages;
    isLastPage.value = data.last;
    isFirstPage.value = data.first;
  } catch (error) {
    console.log(error);
  }
};

const getAllBrands = async () => {
  try {
    brands.value = await getAllBrand(`${BASE_API_DOMAIN}`);
  } catch (error) {
    console.log(error);
    brands.value = [];
  }
};
const deleteSaleItem = async (itemId) => {
  try {
    statusStore.clearEntityAndMethodAndStatusAndMessage();
    showDialog.value = false;
    await deleteSaleItemById(`${BASE_API_DOMAIN}`, itemId);
    indexPage.value = 0;
    tempIndexPage.value = 0;
    getAllSaleItems();
    sessionStorage.setItem("indexPage", 0);
    sessionStorage.setItem("tempIndexPage", 0);
  } catch (error) {
    console.log(error);
  }
};

const clearSort = () => {
  isSort.value.sortFiled = "createOn";
  isSort.value.sortDirection = "none";
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllSaleItems();
};

const sortAsc = () => {
  isSort.value.sortFiled = "brand.name";
  isSort.value.sortDirection = "asc";
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllSaleItems();
};

const sortDesc = () => {
  isSort.value.sortFiled = "brand.name";
  isSort.value.sortDirection = "desc";
  indexPage.value = 0;
  tempIndexPage.value = 0;
  getAllSaleItems();
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
  getAllSaleItems();
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
  getAllSaleItems();
};

const clickPageNumber = (numPage) => {
  indexPage.value = pageList.value.findIndex((page) => page === numPage);
  tempIndexPage.value = numPage - 1;
  getAllSaleItems();
};

const firstPage = () => {
  indexPage.value = 0;
  tempIndexPage.value = 0;
  pageList.value = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
  getAllSaleItems();
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
  getAllSaleItems();
};

onMounted(() => {
  const savedSize = sessionStorage.getItem("pageSize-list");
  const savedSortField = sessionStorage.getItem("sortField-list");
  const savedSortDirection = sessionStorage.getItem("sortDirection-list");
  const savedIndexPage = sessionStorage.getItem("indexPage-list");
  const savedTempIndexPage = sessionStorage.getItem("tempIndexPage-list");
  const savedPageList = sessionStorage.getItem("pageList-list");

  if (savedPageList) pageList.value = JSON.parse(savedPageList);
  if (savedSize) pageSize.value = parseInt(savedSize);
  if (savedSortField) isSort.value.sortFiled = savedSortField;
  if (savedSortDirection) isSort.value.sortDirection = savedSortDirection;
  if (savedIndexPage) indexPage.value = parseInt(savedIndexPage);
  if (savedTempIndexPage) tempIndexPage.value = parseInt(savedTempIndexPage);
  getAllSaleItems();
  getAllBrands();
});
</script>

<template>
  <NavBar />
  <Notification v-if="statusStore.getStatus() !== null" />
  <div class="list-container text-white text-sm">
    <AlertMessage
      v-if="showDialog"
      title="Delete Confirmation"
      message="Do you want to delete this sale item?"
      @confirm="deleteSaleItem(itemId)"
      @cancel="showDialog = false"
    />
    <div class="promote h-96 flex flex-col justify-center items-center gap-8">
      <h1 class="text-6xl">It's your lifestyle</h1>
      <p class="text-white">Portable, fast to use, new model</p>
      <RouterLink
        :to="{ name: 'AddSaleItems' }"
        class="itbms-sale-item-add w-36 py-2 rounded-2xl bg-blue-500 text-center hover:bg-blue-500/90"
        >Add Model</RouterLink
      >
    </div>
    <div
      class="option-for-sale-item flex justify-between items-center h-20 bg-[rgba(22,22,23,255)] text-white"
    >
      <button
        class="filter w-36 h-full hover:inset-shadow-xs hover:inset-shadow-[rgba(22,22,23,255)] hover:bg-blue-500 hover:cursor-pointer duration-200"
      >
        Filter
      </button>
      <RouterLink
        @click="statusStore.clearEntityAndMethodAndStatusAndMessage"
        :to="{ name: 'BrandList' }"
        class="itbms-manage-brand w-36 h-full flex justify-center items-center hover:inset-shadow-xs hover:inset-shadow-[rgba(22,22,23,255)] hover:bg-blue-500 hover:cursor-pointer duration-200"
      >
        Manage Brand
      </RouterLink>
    </div>
    <div class="filter-container mx-28 py-7 flex justify-center border-b">
      <div class="sort-page p-2 flex items-center gap-1 bg-gray-200 rounded">
        <div class="page space-x-3 text-black">
          <label>show</label>
          <select
            @change="(indexPage = 0), (tempIndexPage = 0), getAllSaleItems()"
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
    <div class="table w-full px-10 mt-10">
      <div class="flex justify-center mb-10">
        <div class="w-sm flex bg-[rgba(22,22,23,255)] rounded-2xl">
          <img
            src="/src/assets/imgs/phone-symbol.png"
            alt="phone"
            class="w-36 object-cover"
          />
          <div class="self-center space-y-2">
            <h1 class="text-2xl">Available Model</h1>
            <p class="w-fit mx-auto p-1 rounded-full text-xl bg-blue-500">
              {{ totalSaleItems }}
            </p>
          </div>
        </div>
      </div>
      <h1 class="text-4xl">My Product</h1>
      <table v-if="items.length !== 0" class="w-full my-5">
        <tr class="bg-[rgba(22,22,23,255)]">
          <th>Id</th>
          <th>Brand</th>
          <th>Model</th>
          <th>Ram</th>
          <th>Storage</th>
          <th>Color</th>
          <th>Price</th>
          <th>Action</th>
        </tr>
        <tr v-for="(item, index) in items" :key="index" class="itbms-row">
          <td class="itbms-id">{{ item.id }}</td>
          <td class="itbms-brand">{{ item.brandName }}</td>
          <td class="itbms-model">{{ item.model }}</td>
          <td class="itbms-ramGb">
            {{ item.ramGb === null || item.ramGb === "" ? "-" : item.ramGb }}
          </td>
          <td class="itbms-storageGb">
            {{
              item.storageGb === null || item.storageGb === ""
                ? "-"
                : item.storageGb
            }}
          </td>
          <td class="itbms-color">
            {{ item.color === null || item.color === "" ? "-" : item.color }}
          </td>
          <td class="itbms-price">฿ {{ item.price.toLocaleString() }}</td>
          <td class="flex justify-center gap-5">
            <RouterLink
              @click="statusStore.clearStatusAndMethod()"
              :to="{ name: 'EditSaleItems', params: { itemId: item.id } }"
              class="itbms-edit-button w-10 border rounded border-blue-500 text-blue-500 hover:bg-blue-500 hover:text-white hover:cursor-pointer duration-200"
              >E</RouterLink
            >
            <button
              @click="(showDialog = true), (itemId = item.id)"
              class="itbms-delete-button w-10 border rounded border-red-500 text-red-500 hover:bg-red-500 hover:text-white hover:cursor-pointer duration-200"
            >
              D
            </button>
          </td>
        </tr>
      </table>
      <h1
        v-else
        class="itmbs-row py-10 text-white text-5xl text-center border-t"
      >
        no sale item
      </h1>
      <div
        v-show="items.length !== 0 && totalPage > 1"
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
  </div>

  <Footer />
</template>

<style scoped>
th,
td {
  border: 1px solid gray;
  text-align: center;
  padding: 10px 0;
}

.promote {
  background-image: url("/src/assets/imgs/bg-gif.gif");
  background-size: contain;
  background-repeat: no-repeat;
}
</style>
