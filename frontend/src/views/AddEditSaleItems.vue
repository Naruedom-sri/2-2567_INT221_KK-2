<script setup>
import NavBar from "@/components/NavBar.vue";
import SaleItemNotFound from "@/components/SaleItemNotFound.vue";
import { ref, onMounted, watch } from "vue";
import { getAllData, createData, updateData, getDataById } from "@/libs/api";
import { useRouter, useRoute } from "vue-router";
import { useSaleItemStatusStore } from "@/stores/SaleItemStatus";
import Footer from "@/components/Footer.vue";

const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const props = defineProps({
  isEditing: Boolean,
});
const {
  params: { itemId },
} = useRoute();
const statusStore = useSaleItemStatusStore();
const route = useRouter();
const item = ref({});
const brands = ref([]);
const brandItem = ref("");
const model = ref();
const color = ref();
const description = ref();
const price = ref();
const ramGb = ref();
const storageGb = ref();
const quantity = ref();
const screenSizeInch = ref();
const isContainAllNonOtionalFiled = ref(false);
const isUpdatedFiled = ref(false);
const isDisabled = ref(true);

const modelInput = ref();
const colorInput = ref();
const descriptionInput = ref();
const priceInput = ref();
const ramInput = ref();
const storageInput = ref();
const screenSizeInput = ref();
const quantityInput = ref();

const brandPass = ref(null);
const modelPass = ref(null);
const descriptionPass = ref(null);
const pricePass = ref(null);
const colorPass = ref(true);
const quantityPass = ref(true);
const ramGbPass = ref(true);
const storageGbPass = ref(true);
const screenSizeInchPass = ref(true);

const validInput = ref(false);

const checkVaildateInput = () => {
  validInput.value =
    brandPass.value &&
    modelPass.value &&
    colorPass.value &&
    descriptionPass.value &&
    pricePass.value &&
    quantityPass.value &&
    ramGbPass.value &&
    storageGbPass.value &&
    screenSizeInchPass.value
      ? true
      : false;
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

const goBackToPreviousPage = () => {
  route.back();
};

const checkIsEditing = async () => {
  try {
    if (props.isEditing) {
      item.value = await getDataById(
        `${BASE_API_DOMAIN}/v1/sale-items/edit`,
        itemId
      );
      model.value = item.value.model;
      brandItem.value = item.value.brand;
      description.value = item.value.description;
      price.value = item.value.price;
      ramGb.value = item.value.ramGb;
      screenSizeInch.value = item.value.screenSizeInch;
      quantity.value = item.value.quantity;
      storageGb.value = item.value.storageGb;
      color.value = item.value.color;
      brandPass.value = true;
      modelPass.value = true;
      descriptionPass.value = true;
      pricePass.value = true;
    }
  } catch (error) {
    console.log(error);
    item.value = null;
  }
};

const checkAllNonOptionalFiled = () => {
  if (
    brandItem.value !== undefined &&
    brandItem.value !== "" &&
    model.value !== undefined &&
    model.value !== "" &&
    price.value !== undefined &&
    price.value !== "" &&
    description.value !== undefined &&
    description.value !== ""
  ) {
    isContainAllNonOtionalFiled.value = true;
  } else {
    isContainAllNonOtionalFiled.value = false;
  }
};

const checkUpdatedFiled = () => {
  if (
    model.value !== item.value.model ||
    brandItem.value?.id !== item.value.brand?.id ||
    description.value !== item.value.description ||
    price.value !== item.value.price ||
    ramGb.value !== item.value.ramGb ||
    screenSizeInch.value !== item.value.screenSizeInch ||
    quantity.value !== item.value.quantity ||
    storageGb.value !== item.value.storageGb ||
    color.value !== item.value.color
  ) {
    isUpdatedFiled.value = true;
  } else {
    isUpdatedFiled.value = false;
  }
  console.log(isUpdatedFiled.value);
};

const addUpdateNewSaleItem = async () => {
  try {
    const newItem = {
      model: model.value,
      brand: brandItem.value,
      description: description.value,
      price: price.value,
      ramGb: ramGb.value,
      screenSizeInch: screenSizeInch.value,
      quantity: quantity.value === undefined ? null : quantity.value,
      storageGb: storageGb.value,
      color: color.value,
    };

    if (!props.isEditing) {
      const data = await createData(
        `${BASE_API_DOMAIN}/v1/sale-items`,
        newItem
      );
      if (data) {
        statusStore.setStatusAndMethod("add", 201);
      }
    } else {
      const data = await updateData(
        `${BASE_API_DOMAIN}/v1/sale-items`,
        itemId,
        newItem
      );
      if (data) {
        statusStore.setStatusAndMethod("update", 200);
      }
    }
    goBackToPreviousPage();
  } catch (error) {
    console.log(error);
  }
};
const checkDisabled = () => {
  if (
    !isContainAllNonOtionalFiled.value &&
    !isUpdatedFiled.value &&
    !validInput.value
  ) {
    isDisabled.value = true;
  } else if (
    isContainAllNonOtionalFiled.value &&
    isUpdatedFiled.value &&
    !validInput.value
  ) {
    isDisabled.value = true;
  } else if (
    isContainAllNonOtionalFiled.value &&
    !isUpdatedFiled.value &&
    !validInput.value
  ) {
    isDisabled.value = true;
  } else if (
    !isContainAllNonOtionalFiled.value &&
    isUpdatedFiled.value &&
    validInput.value
  ) {
    isDisabled.value = true;
  } else if (
    !isContainAllNonOtionalFiled.value &&
    !isUpdatedFiled.value &&
    validInput.value
  ) {
    isDisabled.value = true;
  } else if (
    !isContainAllNonOtionalFiled.value &&
    isUpdatedFiled.value &&
    !validInput.value
  ) {
    isDisabled.value = true;
  } else if (
    isContainAllNonOtionalFiled.value &&
    !isUpdatedFiled.value &&
    validInput.value
  ) {
    isDisabled.value = true;
  } else {
    isDisabled.value = false;
  }
};

const focusNext = (refName) => {
  switch (refName) {
    case "modelInput":
      modelInput.value?.focus();
      break;
    case "colorInput":
      colorInput.value?.focus();
      break;
    case "descriptionInput":
      descriptionInput.value?.focus();
      break;
    case "priceInput":
      priceInput.value?.focus();
      break;
    case "ramInput":
      ramInput.value?.focus();
      break;
    case "storageInput":
      storageInput.value?.focus();
      break;
    case "screenSizeInput":
      screenSizeInput.value?.focus();
      break;
    case "quantityInput":
      quantityInput.value?.focus();
      break;
  }
};
onMounted(() => {
  checkIsEditing();
  getAllBrand();
});

watch(
  [
    brandItem,
    model,
    price,
    description,
    quantity,
    ramGb,
    screenSizeInch,
    storageGb,
    color,
  ],
  () => {
    checkAllNonOptionalFiled();
    checkUpdatedFiled();
  },
  { immediate: true }
);
</script>

<template>
  <NavBar />
  <SaleItemNotFound v-if="item === null" />
  <div v-else class="add-edit-container text-white">
    <div class="flex py-7 mx-20 border-b border-white">
      <RouterLink
        :to="{ name: 'SaleItemsGallery' }"
        class="itbms-home-button hover:text-blue-500 hover:cursor-pointer duration-100"
      >
        Home
      </RouterLink>
      <h1 class="mx-3">/</h1>
      <h1
        v-if="!isEditing"
        class="px-3 rounded-2xl bg-gradient-to-r from-purple-800 to-blue-400"
      >
        New Sale Item
      </h1>
      <button
        v-else
        @click="goBackToPreviousPage"
        class="itbms-back-button px-3 rounded-2xl bg-gradient-to-r from-purple-800 to-blue-400"
      >
        {{ item.model }}
        {{ item.color }}
      </button>
    </div>
    <h1
      class="w-fit mx-20 mt-5 text-5xl font-semibold bg-gradient-to-r from-purple-400 to-blue-400 bg-clip-text text-transparent"
    >
      {{ isEditing ? `Edit ${item.model} ${item.color}` : "Add New Sale Item" }}
    </h1>
    <p class="mx-20 mt-3 text-white/80">
      {{
        isEditing
          ? `It's the little details that make a product truly complete.`
          : "Every product you add is another step toward your success."
      }}
    </p>
    <form @submit.prevent="addUpdateNewSaleItem" class="py-[35px] text-lg">
      <div class="grid grid-cols-2 gap-10 mx-20">
        <div class="self-center">
          <img
            src="/src/assets/imgs/iphone-item.png"
            alt="iphone-item"
            class="mx-auto"
          />
          <div class="w-52 flex mx-2.5 gap-2">
            <img
              src="/src/assets/imgs/iphone-item.png"
              alt="iphone-item"
              class="object-cover border rounded-xl"
            />
            <img
              src="/src/assets/imgs/iphone-item.png"
              alt="iphone-item"
              class="object-cover border rounded-xl"
            />
            <img
              src="/src/assets/imgs/iphone-item.png"
              alt="iphone-item"
              class="object-cover border rounded-xl"
            />
          </div>
          <div class="w-fit mx-auto my-4">
            <button
              class="py-1 px-2 text-xl border rounded hover:bg-white hover:text-black hover:cursor-pointer duration-200"
            >
              Upload file image
            </button>
          </div>
        </div>
        <div class="flex flex-col space-y-4">
          <h1 class="pb-1 text-3xl border-b">Overview</h1>
          <label>Brand<span>*</span></label>
          <select
            autofocus
            @blur="
              brandItem === '' ? (brandPass = false) : (brandPass = true),
                checkVaildateInput(),
                checkDisabled()
            "
            v-model.trim="brandItem"
            required
            class="itbms-brand px-5 py-2 rounded-lg bg-[rgba(22,22,23,255)]"
          >
            <option value="">Please an option</option>
            <option
              v-for="(brand, index) in brands"
              :key="index"
              :value="brand"
              class="bg-[rgba(22,22,23,255)]"
            >
              {{ brand.name }}
            </option>
          </select>
          <h1
            v-if="!brandPass && brandPass !== null"
            class="itbms-message text-red-400 text-sm"
          >
            Brand must be selected.
          </h1>
          <label>Model<span>*</span> </label>
          <input
            @keydown.enter.prevent="focusNext('descriptionInput')"
            @blur="
              model?.length > 60 || model === undefined || model === ''
                ? (modelPass = false)
                : (modelPass = true),
                checkVaildateInput(),
                checkDisabled()
            "
            placeholder="e.g. iPhone 15 Pro Max"
            ref="modelInput"
            v-model.trim="model"
            required
            type="text"
            class="itbms-model"
            maxlength="60"
          />
          <h1
            v-if="!modelPass && modelPass !== null"
            class="itbms-message text-red-400 text-sm"
          >
            Model must be 1-60 characters long.
          </h1>
          <label>Description<span>*</span></label>
          <textarea
            @keydown.enter.prevent="focusNext('priceInput')"
            @blur="
              description?.length > 65535 ||
              description === undefined ||
              description === ''
                ? (descriptionPass = false)
                : (descriptionPass = true),
                checkVaildateInput(),
                checkDisabled()
            "
            placeholder="e.g. Flagship smartphone with A17 chip and 256GB storage"
            ref="descriptionInput"
            v-model.trim="description"
            required
            maxlength="65535"
            class="itbms-description px-4 py-2 h-32 rounded-xl bg-[rgba(22,22,23,255)]"
          ></textarea>
          <h1
            v-if="!descriptionPass && descriptionPass !== null"
            class="itbms-message text-red-400 text-sm"
          >
            Description must be 1-65,535 characters long.
          </h1>
          <h1 class="pb-1 text-3xl border-b mt-10">Pricing</h1>
          <label>Price ( ฿ )<span>*</span></label>
          <input
            @keydown.enter.prevent="focusNext('quantityInput')"
            @blur="
              price < 0 || price === undefined || price === ''
                ? (pricePass = false)
                : (pricePass = true),
                checkVaildateInput(),
                checkDisabled()
            "
            placeholder="e.g. 12990"
            ref="priceInput"
            v-model="price"
            required
            type="number"
            class="itbms-price"
            min="0"
          />
          <h1
            v-if="!pricePass && pricePass !== null"
            class="itbms-message text-red-400 text-sm"
          >
            Price must be non-negative integer.
          </h1>
          <label>Quantity</label>
          <input
            @keydown.enter.prevent="focusNext('colorInput')"
            @blur="
              quantity < 0 ? (quantityPass = false) : (quantityPass = true),
                checkVaildateInput(),
                checkDisabled()
            "
            placeholder="e.g. 5"
            ref="quantityInput"
            v-model="quantity"
            type="number"
            class="itbms-quantity"
            min="0"
          />
          <h1 v-if="!quantityPass" class="itbms-message text-red-400 text-sm">
            Quantity must be non-negative integer.
          </h1>
          <h1 class="text-3xl mt-10 pb-1 border-b">Specifications</h1>
          <label>Color</label>
          <input
            @keydown.enter.prevent="focusNext('ramInput')"
            @blur="
              color?.length > 40 ? (colorPass = false) : (colorPass = true),
                checkVaildateInput(),
                checkDisabled()
            "
            placeholder="e.g. Midnight Blue"
            ref="colorInput"
            v-model.trim="color"
            type="text"
            maxlength="40"
            class="itbms-color"
          />
          <h1 v-if="!colorPass" class="itbms-message text-red-400 text-sm">
            Color must be 1-40 characters long.
          </h1>
          <label>Ram ( GB )</label>
          <input
            @keydown.enter.prevent="focusNext('screenSizeInput')"
            @blur="
              ramGb < 0 ? (ramGbPass = false) : (ramGbPass = true),
                checkVaildateInput(),
                checkDisabled()
            "
            placeholder="e.g. 8"
            ref="ramInput"
            v-model="ramGb"
            type="number"
            class="itbms-ramGb"
            min="0"
          />
          <h1 v-if="!ramGbPass" class="itbms-message text-red-400 text-sm">
            RAM size must be positive integer or not specified.
          </h1>
          <label>Screen Size ( Inches )</label>
          <input
            @keydown.enter.prevent="focusNext('storageInput')"
            @blur="
              screenSizeInch < 0 ||
              (!/^\d+(\.\d{1,2})?$/.test(parseFloat(screenSizeInch)) &&
                screenSizeInch !== undefined &&
                screenSizeInch !== '')
                ? (screenSizeInchPass = false)
                : (screenSizeInchPass = true),
                checkVaildateInput(),
                checkDisabled()
            "
            placeholder="e.g. 6.7"
            v-model="screenSizeInch"
            type="number"
            class="itbms-screenSizeInch"
            min="0"
            max="99"
            step="0.01"
          />
          <h1
            v-if="!screenSizeInchPass"
            class="itbms-message text-red-400 text-sm"
          >
            Screen size must be positive number with at most 2 decimal points or
            not specified.
          </h1>
          <label>Storage ( GB )</label>
          <input
            @keydown.enter.prevent="focusNext('modelInput')"
            @blur="
              storageGb < 0 ? (storageGbPass = false) : (storageGbPass = true),
                checkVaildateInput(),
                checkDisabled()
            "
            placeholder="e.g. 256"
            ref="storageInput"
            v-model="storageGb"
            type="number"
            class="itbms-storageGb"
            min="0"
          />
          <h1 v-if="!storageGbPass" class="itbms-message text-red-400 text-sm">
            Storage size must be positive integer or not specified.
          </h1>
          <div class="btn-form mt-5 flex space-x-4 text-2xl">
            <button
              type="button"
              @click="goBackToPreviousPage"
              class="itbms-cancel-button w-full py-2 rounded-4xl border border-red-500 text-red-500 hover:cursor-pointer hover:bg-red-500 hover:text-white duration-150"
            >
              Cancel
            </button>
            <button
              v-if="!isEditing"
              :disabled="isDisabled"
              type="submit"
              class="itbms-save-button w-full py-2 rounded-4xl duration-150"
              :class="
                isDisabled
                  ? 'border border-gray-400 text-gray-400'
                  : 'bg-blue-500 hover:text-white hover:cursor-pointer'
              "
            >
              Add
            </button>
            <button
              v-else
              :disabled="isDisabled"
              type="submit"
              class="itbms-save-button w-full py-2 rounded-4xl duration-150"
              :class="
                isDisabled
                  ? 'border border-gray-400 text-gray-400'
                  : 'bg-blue-500 hover:text-white hover:cursor-pointer'
              "
            >
              Save
            </button>
          </div>
        </div>
      </div>
    </form>
  </div>
  <Footer />
</template>

<style scoped>
input {
  background-color: rgba(22, 22, 23, 255);
  border-radius: 10px;
  padding: 10px 20px;
}

span {
  color: red;
  margin: 0 4px;
}
</style>
