<script setup>
import { ref } from "vue";
import { useCartStore } from "@/stores/cartStore";
import { useUserStore } from "@/stores/userStore";
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
import AlertMessage from "@/components/AlertMessage.vue";

const cart = useCartStore();
const user = useUserStore();
const loading = ref(false);

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

</script>

<template>
  <div>
    <NavBar />
    <div class="w-fullscreen grid grid-cols-3 gap-20 text-white p-4 ml-30 mr-30">
      <div class="text-left space-y-4 col-span-2">
        <p class="text-3xl font-semibold">Shopping Cart</p>

        <div v-if="!cart.items.length" class="text-lg">Your cart is empty.</div>

        <div
          v-for="seller in cart.groupedBySeller"
          :key="seller.sellerId"
          class="border p-3 rounded-md"
        >
          <div class="font-semibold mb-2">Seller: {{ user.nickname }}</div>
          <div class="space-y-2">
            <div
              v-for="it in seller.items"
              :key="it.itemId"
              class="flex items-center justify-between bg-gray-800 p-2 rounded"
            >
              <div class="flex items-center gap-3 flex-1">
                <img
                  :src="it.image"
                  alt="item image"
                  class="w-20 h-20 object-cover rounded"
                />
                <div>
                  <div class="font-medium">{{ it.name }}</div>
                  <!-- <div class="text-sm">
                    Unit: {{ it.price.toFixed(2) }} | Stock:
                    {{ it.availableStock }}
                  </div> -->
                </div>
              </div>
              <div class="flex items-center gap-2">
                <button
                  class="px-2 bg-red-600 rounded"
                  @click="onDecrement(it)"
                >
                  -
                </button>
                <div class="px-3">{{ it.quantity }}</div>
                <button
                  class="px-2 bg-green-600 rounded"
                  @click="onIncrement(it)"
                >
                  +
                </button>
                <div class="w-32 text-right text-sm">
                  <p>price: <span>{{ (it.price * it.quantity).toFixed(2) }}</span></p>
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
              type="text"
              class="border border-white rounded-md h-20 w-auto p-1 font-light text-sm"
              placeholder="Address NO, Street, Subdistrict, District, Province, Postal Code"
            ></textarea>
          </div>
          <div class="flex flex-col">
            <label>Note</label>
            <textarea
              type="text"
              class="border border-white rounded-md h-20 w-auto p-1 font-light text-sm"
              placeholder="Additional instruction or requests"
            ></textarea>
          </div>
        </div>

        <div class="mt-auto">
          <div>
            <p class="text-sm">
              Total Items:
              <span>{{ cart.totalItems }}</span>
            </p>
          </div>
          <div>
            <p class="text-sm">
              Total Price:
              <span>{{ cart.totalPrice.toFixed(2) }}</span>
            </p>
          </div>  
          <div>
            <button
              :disabled="!cart.items.length || loading"
              @click="debugLog"
              class="border-none bg-blue-700 rounded-md p-2 w-full hover:bg-blue-800 disabled:opacity-50 mt-5"
            >
              <span v-if="!loading">Place Order</span>
              <span v-else>Placing...</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <Footer />

    <AlertMessage
      v-if="showConfirmModal"
      :visible="showConfirmModal"
      title="Remove Item"
      message="Do you want to remove the sale item from the cart?"
      @confirm="onConfirmRemove"
      @cancel="onCancel"
    />
  </div>
</template>

<style scoped></style>
