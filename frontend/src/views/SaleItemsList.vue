<script setup>
import { onMounted, ref } from "vue";
import { useStatusStore } from "@/stores/statusStore";
import Footer from "@/components/Footer.vue";
import NavBar from "@/components/NavBar.vue";
import AlertMessage from "@/components/AlertMessage.vue";
import Notification from "@/components/Notification.vue";
import { deleteSaleItemById } from "@/libs/saleItemApi";
import { getAllSaleItemOfSeller, refreshAccessToken } from "@/libs/userApi";
import { getAllBrand } from "@/libs/brandApi";
import { useTokenStore } from "@/stores/tokenStore";
import { useRouter } from "vue-router";
import { jwtDecode } from "jwt-decode";
const statusStore = useStatusStore();
const tokenStore = useTokenStore();
const router = useRouter();
const params = new URLSearchParams();
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
const indexPage = ref(0);
const tempIndexPage = ref(0);

const getAllSaleItems = async () => {
  try {
    if (tokenStore.getAccessToken() === null) {
      try {
        const data = await refreshAccessToken(`${BASE_API_DOMAIN}`);
        const decoded = jwtDecode(data.access_token);
        tokenStore.setAccessToken(data.access_token);
        tokenStore.setDecode(decoded);
      } catch (e) {
        console.log(e);
        router.push({ name: "Login" });
      }
    }

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
      tokenStore.getDecode().jti,
      tokenStore.getAccessToken(),
      params
    );
    items.value = data.content;
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
  statusStore.clearEntityAndMethodAndStatusAndMessage();
  showDialog.value = false;
  try {
    const status = await deleteSaleItemById(`${BASE_API_DOMAIN}`, itemId);
    items.value = items.value.filter((item) => item.id !== itemId);
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

onMounted(() => {
  getAllSaleItems();
  getAllBrands();
});
</script>

<template>
  <NavBar />
  <Notification v-if="statusStore.getStatus() !== null" />
  <div class="list-container text-white">
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
    <div class="table w-full px-10 my-10">
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
              {{ items.length }}
            </p>
          </div>
        </div>
      </div>
      <h1 class="text-4xl mb-10">My Product</h1>
      <table v-if="items.length !== 0" class="w-full">
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
        class="itmbs-row pt-10 text-white text-5xl text-center border-t"
      >
        no sale item
      </h1>
    </div>
  </div>

  <Footer />
</template>

<style scoped>
th,
td {
  border: 1px solid;
  text-align: center;
  padding: 10px 0;
}

.promote {
  background-image: url("/src/assets/imgs/bg-gif.gif");
  background-size: contain;
  background-repeat: no-repeat;
}
</style>
