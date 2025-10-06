<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import SaleItemNotFound from "@/components/SaleItemNotFound.vue";
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
import { useStatusStore } from "@/stores/statusStore";
import AlertMessage from "@/components/AlertMessage.vue";
import Notification from "@/components/Notification.vue";
import {
  deleteSaleItemById,
  getImageOfSaleItem,
  getSaleItemById,
  getFirstImageOfSaleItem,
} from "@/libs/saleItemApi";
import { decodeToken } from "@/libs/jwtToken";
import { useCartStore } from "@/stores/cartStore";

const cart = useCartStore();
const {
  params: { itemId },
} = useRoute();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const statusStore = useStatusStore();
const accessToken = localStorage.getItem("accessToken");
const decoded = decodeToken(accessToken);
const item = ref({});
const router = useRouter();
const showDialog = ref(false);
const imageUrlList = ref([]);
const mainImage = ref(null);

const getSaleItem = async () => {
  try {
    item.value = await getSaleItemById(`${BASE_API_DOMAIN}`, itemId);
    if (item.value.saleItemImages.length !== 0) {
      for (const img of item.value.saleItemImages) {
        const imgUrl = await getImageOfSaleItem(
          `${BASE_API_DOMAIN}`,
          itemId,
          img.imageViewOrder
        );
        imageUrlList.value.push(imgUrl); // set ตาม index
      }
      mainImage.value = imageUrlList.value[0];
    }
  } catch (error) {
    console.log(error);
    item.value = null;
  }
};

const deleteSaleItem = async () => {
  showDialog.value = false;
  try {
    await deleteSaleItemById(`${BASE_API_DOMAIN}`, itemId);
    sessionStorage.setItem("indexPage", String(0));
    sessionStorage.setItem("tempIndexPage", String(0));
    router.push({ name: "SaleItemsGallery" });
  } catch (error) {
    console.log(error);
  }
};
onMounted(() => getSaleItem());
onUnmounted(() => {
  imageUrlList.value.forEach((url) => URL.revokeObjectURL(url));
});

const buyQty = ref(1);

function increaseQty() {
  if (buyQty.value < (item.value?.quantity ?? 9999)) buyQty.value++;
}
function decreaseQty() {
  if (buyQty.value > 1) buyQty.value--;
}

async function addItemToCart(saleItem, qty = 1) {
  if (!saleItem) return;

  const token = localStorage.getItem("accessToken");
  let currentUserId = null;

  if(accessToken === null) {
    router.push({ name: "Login" });
    return;
  }

  if (token) {
    const decoded = decodeToken(token);
    currentUserId = decoded?.buyerId || decoded?.id || decoded?.sub || null;
  }

  const sellerId =
    saleItem.seller?.id ??
    saleItem.sellerId ??
    saleItem.userId ??
    saleItem.shopId ??
    saleItem.ownerId ??
    "unknown";

  if (sellerId === currentUserId) {
  statusStore.setEntityAndMethodAndStatusAndMessage(
    "cart",
    "add",
    400,
    "You cannot add your own product to the cart."
  );
  return;
}

  let imageUrl = null;
  try {
    imageUrl = await getFirstImageOfSaleItem(`${BASE_API_DOMAIN}`, saleItem.id);
  } catch (e) {
    console.warn("Failed to fetch image:", e);
  }

  if (!imageUrl && saleItem.saleItemImages?.length) {
    imageUrl = saleItem.saleItemImages[0].url ?? null;
  }

  const itemPayload = {
    itemId: saleItem.id,
    brand: saleItem.brandName ?? "Brand",
    color: saleItem.color ?? null,
    storageGb: saleItem.storageGb ?? null,
    name: saleItem.model ?? saleItem.brandName ?? "Item",
    price: Number(saleItem.price ?? 0),
    availableStock: saleItem.quantity ?? 0,
    image: imageUrl ?? "",
  };

  const sellerPayload = {
    sellerId,
    sellerNickname:
      saleItem.seller?.nickName ??
      saleItem.seller?.fullName ??
      saleItem.sellerName ??
      saleItem.shopName ??
      saleItem.brandName ??
      "Seller",
  };

  cart.addToCart(itemPayload, sellerPayload, Number(qty));

  statusStore.setEntityAndMethodAndStatusAndMessage(
    "cart",
    "add",
    200,
    "Add to cart successfully."
  );
}
</script>

<template>
  <NavBar />
  <Notification v-show="statusStore.getStatus() !== null" />
  <SaleItemNotFound v-if="item === null" />
  <div v-else class="detail-container text-sm text-white">
    <AlertMessage
      v-if="showDialog"
      title="Are you sure?"
      message="Do you want to delete this sale item?"
      @confirm="deleteSaleItem"
      @cancel="showDialog = false"
    />
    <div class="itbms-row grid grid-cols-2 mb-10">
      <div class="flex col-span-2 py-7 mx-20 mb-6 border-b border-white">
        <RouterLink
          @click="statusStore.clearEntityAndMethodAndStatusAndMessage()"
          :to="{ name: 'SaleItemsGallery' }"
          class="itbms-home-button hover:text-blue-500 hover:cursor-pointer duration-100"
        >
          Home
        </RouterLink>
        <h1 class="mx-1">-</h1>
        <h1 class="px-3 rounded-2xl bg-gradient-to-r from-blue-500 to-blue-300">
          {{ item.model }}
          {{ item.color }}
        </h1>
      </div>
      <div class="model-price mx-20 space-y-4">
        <h1 class="itbms-model text-4xl font-semibold">
          <span class="itbms-brand">{{ item.brandName }}</span> {{ item.model }}
        </h1>
        <p class="itbms-price text-white/80 text-base">
          From
          <span class="itbms-price-unit">฿</span>
          {{ item.price?.toLocaleString() }}
        </p>
        <div class="flex h-4 items-center gap-2">
          <p class="w-3 h-3 rounded-full bg-green-500"></p>
          <h1 class="my-4 text-green-500">Instock</h1>
          <p class="itbms-quantity text-white/80">
            / available {{ item.quantity }}
            <span class="itbms-quantity-unit">units</span>
          </p>
        </div>
      </div>
      <div class="truck-bag flex justify-end gap-6 h-7 mx-20">
        <div class="flex gap-2">
          <img
            src="/src/assets/imgs/truck-symbol.png"
            alt="truck"
            class="object-cover"
          />
          <p class="self-center">Free shipping</p>
        </div>
        <p>|</p>
        <div class="flex gap-2">
          <img
            src="/src/assets/imgs/bag-symbol.png"
            alt="bag"
            class="object-cover"
          />
          <p class="self-center">Pick up from Store</p>
        </div>
      </div>
      <div class="detail-content-left">
        <div
          class="w-xl mx-auto my-10 py-10 rounded-xl bg-[rgba(22,22,23,255)]"
        >
          <img
            v-if="mainImage !== null"
            :src="mainImage"
            class="w-72 h-96 mx-auto object-cover rounded-xl"
          />
          <img
            v-else="mainImage !== null"
            src="../assets/imgs/no-image.png"
            class="w-72 mx-auto object-cover"
          />
        </div>
        <div class="w-full flex justify-center gap-2">
          <div
            v-for="(url, index) in imageUrlList"
            :key="index"
            class="p-3 rounded-xl bg-[rgba(22,22,23,255)]"
          >
            <img
              @click="mainImage = url"
              :src="url"
              class="w-28 h-28 object-cover rounded-xl"
            />
          </div>
        </div>
      </div>
      <div class="detail-content-right self-center mr-20">
        <h1 class="text-2xl mb-5">Overview</h1>
        <p class="itbms-description">{{ item.description }}</p>
        <h1 class="text-2xl my-5">
          Model. <span class="text-white/80">Which is best for you?</span>
        </h1>
        <div
          class="detail-model flex justify-between items-center w-md p-3 border rounded-2xl"
        >
          <div>
            <p class="itbms-model">
              {{ item.model }}
            </p>
            <p class="itbms-screenSizeInch text-white/80">
              {{
                item.screenSizeInch === null || item.screenSizeInch === ""
                  ? "-"
                  : item.screenSizeInch
              }}
              <span
                v-show="
                  item.screenSizeInch !== null && item.screenSizeInch !== ''
                "
                class="itbms-screenSizeInch-unit"
                >Inches</span
              >
              display
            </p>
          </div>
          <p class="text-base text-white/80">
            From ฿ {{ item.price?.toLocaleString() }}
          </p>
        </div>
        <h1 class="text-2xl my-5">
          Finish. <span class="text-white/80">Pick your favorite.</span>
        </h1>
        <h1 class="mb-5">
          Color -
          <span class="itbms-color w-fit">{{
            item.color === null || item.color === "" ? "-" : item.color
          }}</span>
        </h1>
        <div class="flex gap-5">
          <p class="w-8 h-8 bg-orange-400 rounded-full"></p>
          <p class="w-8 h-8 bg-green-300 rounded-full"></p>
          <p class="w-8 h-8 bg-purple-300 rounded-full"></p>
          <p class="w-8 h-8 border-4 bg-blue-300 rounded-full"></p>
        </div>

        <h1 class="text-2xl my-5">
          Storage.
          <span class="text-white/80">How much space do you need?</span>
        </h1>
        <div
          class="detail-ram-rom flex items-center justify-between w-xl px-3 py-5 border rounded-2xl"
        >
          <div class="flex">
            <p class="itbms-ramGb">
              {{ item.ramGb === null || item.ramGb === "" ? "-" : item.ramGb
              }}<span
                v-show="item.ramGb !== null && item.ramGb !== ''"
                class="itbms-ramGb-unit"
              >
                GB</span
              >
            </p>
            <p class="mx-2">+</p>
            <p class="itbms-storageGb">
              {{
                item.storageGb === null || item.storageGb === ""
                  ? "-"
                  : item.storageGb
              }}<span
                v-show="item.storageGb !== null && item.storageGb !== ''"
                class="itbms-storageGb-unit"
              >
                GB</span
              >
            </p>
          </div>
          <p class="itbms-price text-base text-white/80">
            From<span class="itbms-price-unit"> ฿</span>
            {{ item.price?.toLocaleString() }}
          </p>
        </div>
        <div class="quantity">
          <h1 class="text-2xl my-5">
            Quantity. <span class="text-white/80">How many do you want?</span>
          </h1>
          <button
            class="w-7 border rounded-md hover:bg-white hover:text-black hover:cursor-pointer duration-100"
            type="button"
            @click="decreaseQty"
          >
            -
          </button>
          <input
            type="number"
            min="1"
            v-model.number="buyQty"
            :max="item.quantity"
            class="mx-3 border rounded-md text-center"
          />
          <button
            class="w-7 border rounded-md hover:bg-white hover:text-black hover:cursor-pointer duration-100"
            type="button"
            @click="increaseQty"
          >
            +
          </button>
        </div>
        <div class="btn-add-buy mt-10">
          <div
            v-if="decoded?.role === 'SELLER'"
            class="flex justify-between gap-4 space-y-5"
          >
            <button
              @click="showDialog = true"
              class="itbms-delete-button w-full flex-1 py-3 rounded-4xl border text-base hover:bg-white hover:text-black hover:cursor-pointer duration-200"
            >
              Delete
            </button>
            <RouterLink
              :to="{
                name: 'EditSaleItems',
                params: { itemId: item.id },
              }"
              class="itbms-edit-button flex-1"
              ><button
                class="w-full py-3 rounded-4xl border text-base hover:bg-white hover:text-black hover:cursor-pointer duration-200"
              >
                Edit
              </button></RouterLink
            >
          </div>
          <button
            class="w-full py-3 rounded-4xl bg-white text-black text-base hover:cursor-pointer hover:bg-blue-500 hover:text-white duration-200"
            @click.stop="addItemToCart(item, 1)"
          >
            Add to Cart
          </button>
        </div>
      </div>
    </div>
  </div>
  <Footer />
</template>

<style scoped></style>
