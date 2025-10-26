<script setup>
import { ref, watch, computed, onMounted } from "vue";
import { useCartStore } from "@/stores/cartStore";
import { useStatusStore } from "@/stores/statusStore";
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
import AlertMessage from "@/components/AlertMessage.vue";
import { placeOrder as placeOrderApi } from "@/libs/orderApi";
import { decodeToken } from "@/libs/jwtToken";
import router from "@/routers";
import Notification from "@/components/Notification.vue";
import noImage from "../assets/imgs/no-image.png";

const cart = useCartStore();
const statusStore = useStatusStore();
const loading = ref(false);
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const accessToken = localStorage.getItem("accessToken");
const showConfirmModal = ref(false);
const confirmTarget = ref(null);

function onIncrement(item) {
  cart.increment(item);
}

function onDecrement(item) {
  if (item.quantity === 1) {
    confirmTarget.value = item;
    showConfirmModal.value = true;
  } else {
    cart.decrement(item);
  }
}

function onConfirmRemove() {
  if (confirmTarget.value) {
    cart.removeItem(confirmTarget.value);
  }
  confirmTarget.value = null;
  showConfirmModal.value = false;
}

function onCancel() {
  confirmTarget.value = null;
  showConfirmModal.value = false;
}

const shippingAddress = ref("");
const orderNote = ref("");

const selectAll = ref(false);
const selectedItems = ref(new Set());
const selectedSellers = ref(new Set());

const itemKey = (sellerId, itemId) => `${sellerId}::${itemId}`;

function toggleSelectAll() {
  if (selectAll.value) {
    selectedItems.value = new Set();
    selectedSellers.value = new Set();
    for (const seller of cart.groupedBySeller) {
      selectedSellers.value.add(String(seller.sellerId));
      for (const it of seller.items) {
        selectedItems.value.add(itemKey(seller.sellerId, it.itemId));
      }
    }
  } else {
    selectedItems.value.clear();
    selectedSellers.value.clear();
  }
}

function isSellerSelected(seller) {
  return selectedSellers.value.has(String(seller.sellerId));
}

function isItemSelected(seller, it) {
  return selectedItems.value.has(itemKey(seller.sellerId, it.itemId));
}

function toggleSeller(seller) {
  const sId = String(seller.sellerId);
  if (selectedSellers.value.has(sId)) {
    selectedSellers.value.delete(sId);
    for (const it of seller.items) {
      selectedItems.value.delete(itemKey(seller.sellerId, it.itemId));
    }
  } else {
    selectedSellers.value.add(sId);
    for (const it of seller.items) {
      selectedItems.value.add(itemKey(seller.sellerId, it.itemId));
    }
  }
  syncSelectAllFlag();
}

function toggleItem(seller, it) {
  const key = itemKey(seller.sellerId, it.itemId);
  if (selectedItems.value.has(key)) {
    selectedItems.value.delete(key);
  } else {
    selectedItems.value.add(key);
  }
  const allSelected = seller.items.every((x) =>
    selectedItems.value.has(itemKey(seller.sellerId, x.itemId))
  );
  if (allSelected) selectedSellers.value.add(String(seller.sellerId));
  else selectedSellers.value.delete(String(seller.sellerId));
  syncSelectAllFlag();
}

function syncSelectAllFlag() {
  const totalItems = cart.items.length;
  selectAll.value = totalItems > 0 && selectedItems.value.size === totalItems;
}

const selectedCartItems = computed(() => {
  const items = [];
  for (const seller of cart.groupedBySeller) {
    for (const it of seller.items) {
      if (selectedItems.value.has(itemKey(seller.sellerId, it.itemId))) {
        items.push(it);
      }
    }
  }
  return items;
});

const selectedTotalItems = computed(() =>
  selectedCartItems.value.reduce((sum, i) => sum + i.quantity, 0)
);

const selectedTotalPrice = computed(() =>
  selectedCartItems.value.reduce((sum, i) => sum + i.quantity * i.price, 0)
);

watch(
  () => cart.items.length,
  () => {
    const existingKeys = new Set(
      cart.items.map((it) => itemKey(it.sellerId, it.itemId))
    );
    for (const k of Array.from(selectedItems.value)) {
      if (!existingKeys.has(k)) selectedItems.value.delete(k);
    }
    selectedSellers.value.clear();
    for (const k of selectedItems.value) {
      const [sId] = k.split("::");
      selectedSellers.value.add(sId);
    }
    syncSelectAllFlag();
  }
);

async function placeOrder() {
  loading.value = true;
  try {
    let buyerId = null;
    let decoded = null;
    if (accessToken) {
      decoded = decodeToken(accessToken);
      buyerId = decoded?.buyerId || decoded?.id || decoded?.sub || null;
    }

    const selectedGrouped = cart.groupedBySeller
      .map((seller) => {
        const items = seller.items.filter((it) =>
          selectedItems.value.has(`${seller.sellerId}::${it.itemId}`)
        );
        if (!items.length) return null;
        return {
          buyerId,
          sellerId: Number(seller.sellerId),
          orderDate: new Date().toISOString(),
          shippingAddress: shippingAddress.value || "",
          orderNote: orderNote.value || "",
          orderItems: items.map((it) => ({
            saleItemId: it.itemId,
            quantity: it.quantity,
            description: it.brandName || it.name || "",
            price: it.price,
          })),
          orderStatus: "COMPLETED",
        };
      })
      .filter(Boolean);

    if (!selectedGrouped.length) {
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "orders",
        "place",
        400,
        "No selected items to place order."
      );
      return;
    }

    const { status, data } = await placeOrderApi(
      BASE_API_DOMAIN,
      selectedGrouped,
      accessToken,
      router
    );
    if (status === 201) {
      cart.items = cart.items.filter(
        (it) => !selectedItems.value.has(`${it.sellerId}::${it.itemId}`)
      );
      cart._save();
      selectedItems.value.clear();
      selectedSellers.value.clear();
      selectAll.value = false;
      localStorage.setItem(
        `shippingAddress-user-${decoded.id}`,
        shippingAddress.value
      );
      router.push({ name: "OrderBuyer" });
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "orders",
        "place",
        201,
        "Your order has been successfully processed."
      );
    } else {
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "orders",
        "place",
        status,
        data?.message || `Failed to place order (status ${status})`
      );
    }
  } catch (err) {
    console.error("Place order error:", err);
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "orders",
      "place",
      500,
      "Network or unexpected error when placing order."
    );
  } finally {
    loading.value = false;
  }
}

function formatPrice(price) {
  return Number(price).toLocaleString("th-TH", {
    minimumFractionDigits: 2,
  });
}
onMounted(() => {
  const savedAddress = localStorage.getItem(
    `shippingAddress-user-${decodeToken(accessToken).id}`
  );
  if (savedAddress) shippingAddress.value = savedAddress;
});
</script>

<template>
  <NavBar />
  <Notification v-if="statusStore.getStatus() !== null" />
  <div class="w-fullscreen grid grid-cols-3 gap-20 text-white p-4 ml-30 mr-30">
    <div class="text-left space-y-4 col-span-2">
      <div class="flex items-center gap-3">
        <img
          src="../assets/imgs/cart-shopping-solid-full.svg"
          class="w-10 h-10"
        />
        <p class="text-3xl font-semibold">Shopping Cart</p>
      </div>

      <div v-if="!cart.items.length" class="text-lg">Your cart is empty.</div>

      <div class="itbms-select-all border rounded p-3 mb-3">
        <label class="flex items-center gap-2">
          <input
            type="checkbox"
            v-model="selectAll"
            @change="toggleSelectAll"
          />
          <span>Select All</span>
          <span class="ml-2 text-sm text-gray-400"
            >({{ cart.totalItems }} items)</span
          >
        </label>
      </div>

      <div
        v-for="seller in cart.groupedBySeller"
        :key="seller.sellerId"
        class="border p-3 rounded-md"
      >
        <div class="font-semibold mb-2 flex items-center justify-between">
          <label class="flex items-center gap-2">
            <input
              type="checkbox"
              :checked="isSellerSelected(seller)"
              @change="toggleSeller(seller)"
            />
            <img src="../assets/imgs/store-solid-full.svg" class="w-7 inline" />
            <span>{{ seller.sellerNickname }}</span>
          </label>
        </div>

        <div class="space-y-2">
          <div
            v-for="it in seller.items"
            :key="it.itemId"
            class="itbms-item-row flex items-center justify-between bg-gray-800 p-2 rounded"
          >
            <div class="flex items-center gap-3 flex-1">
              <input
                type="checkbox"
                :checked="isItemSelected(seller, it)"
                @change="toggleItem(seller, it)"
              />
              <img
                :src="it.image ? it.image : noImage"
                class="w-20 h-20 object-cover rounded"
              />
              <div>
                <div class="font-light">
                  <span class="font-semibold">{{ it.brand }}</span>
                  {{ it.name }} ({{ it.storageGb }}GB,
                  {{ it.color == null ? "-" : it.color }})
                </div>
                <div class="text-sm">
                  Stock:
                  {{ it.availableStock }}
                </div>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <button class="px-2 bg-gray-600 rounded" @click="onDecrement(it)">
                -
              </button>
              <div class="px-3">{{ it.quantity }}</div>
              <button 
              @click="onIncrement(it)"
              :disabled="it.quantity >= it.availableStock"
              class="px-2 bg-gray-500 rounded disabled:opacity-30 disabled:cursor-not-allowed"
              >
                +
              </button>
              <div class="w-32 text-right text-sm">
                <p>
                  price:
                  <span>{{ formatPrice(it.price * it.quantity) }}</span>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div
      class="flex flex-col border border-white text-left p-4 rounded-md h-120 w-auto font-semibold"
    >
      <p class="text-3xl mb-4">Cart Summary</p>
      <div>
        <p class="text-xl">Ship To</p>
        <div class="flex flex-col">
          <label>Address</label>
          <textarea
            v-model.trim="shippingAddress"
            class="border border-white rounded-md h-20 w-auto p-1 font-light text-sm"
            placeholder="Address NO, Street, Subdistrict, District, Province, Postal Code"
          ></textarea>
        </div>
        <div class="flex flex-col">
          <label>Note</label>
          <textarea
            v-model.trim="orderNote"
            class="border border-white rounded-md h-20 w-auto p-1 font-light text-sm"
            placeholder="Additional instruction or requests"
          ></textarea>
        </div>
      </div>

      <div class="mt-auto">
        <div class="justify-between flex">
          <p class="text-sm">Total Items:</p>
          <p>
            <span>{{ selectedTotalItems }}</span>
          </p>
        </div>
        <div class="justify-between flex">
          <p class="text-sm">Total Price:</p>
          <p>
            <span>Baht {{ formatPrice(selectedTotalPrice) }}</span>
          </p>
        </div>
        <div>
          <button
            :disabled="
              !cart.items.length ||
              !shippingAddress ||
              loading ||
              selectedCartItems.length === 0
            "
            @click="placeOrder"
            class="border-none mt-5 bg-blue-500 rounded-md p-2 w-full hover:bg-blue-800 disabled:opacity-50 disabled:bg-[rgba(22,22,23,255)] disabled:cursor-not-allowed"
          >
            <span v-if="!loading">Place Order</span>
            <span v-else>Placing...</span>
          </button>
        </div>
      </div>
    </div>
  </div>
  <AlertMessage
    v-if="showConfirmModal"
    :is-selected-item="isItemSelected"
    title="Remove Item"
    message="Do you want to remove the sale item from the cart?"
    @confirm="onConfirmRemove"
    @cancel="onCancel"
  />
  <Footer />
</template>

<style scoped>
.itbms-select-all input[type="checkbox"],
.itbms-item-row input[type="checkbox"] {
  width: 16px;
  height: 16px;
}
</style>
