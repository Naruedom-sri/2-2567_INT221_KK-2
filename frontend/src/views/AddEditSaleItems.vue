<script setup>
import NavBar from "@/components/NavBar.vue";
import SaleItemNotFound from "@/components/SaleItemNotFound.vue";
import { ref, onMounted, watch, onBeforeUnmount, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import Footer from "@/components/Footer.vue";
import AlertMessage from "@/components/AlertMessage.vue";
import { getAllBrand } from "@/libs/brandApi";
import {
  createSaleItemSeller,
  getImageOfSaleItem,
  getSaleItemByIdForEdit,
  updateSaleItem,
} from "@/libs/saleItemApi";
import { useStatusStore } from "@/stores/statusStore";
import { decodeToken } from "@/libs/jwtToken";

const props = defineProps({
  isEditing: Boolean,
});
const {
  params: { itemId },
} = useRoute();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const accessToken = localStorage.getItem("accessToken");
const decoded = decodeToken(accessToken);
const statusStore = useStatusStore();

const route = useRouter();
const item = ref({});
const brands = ref([]);
const brandItem = ref("");
const model = ref("");
const color = ref("");
const description = ref("");
const price = ref("");
const ramGb = ref("");
const storageGb = ref("");
const quantity = ref("");
const screenSizeInch = ref("");
const isContainAllNonOptionalFiled = ref(false);
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

const fileInputRef = ref(null);
// Single source of truth for images in UI: [{ name, url, file, size }]
const imageItems = ref([]);
const imageEditList = ref([]);
const tempImageList = ref([]);
const uploadError = ref("");
const priority = ref(0);

const openFileDialog = () => fileInputRef.value?.click();
const onFilesSelected = (e) => {
  const files = Array.from(e.target.files || []);
  if (!files.length) {
    // No change when dialog canceled
    uploadError.value = "";
    e.target.value = "";
    return;
  }

  const MAX_FILES = 4;
  const MAX_PER_FILE = 2 * 1024 * 1024; // 2MB
  const MAX_TOTAL = 5 * 1024 * 1024; // 5MB

  const existingCount = imageItems.value.length;
  const availableSlots = Math.max(0, MAX_FILES - existingCount);
  if (availableSlots === 0) {
    uploadError.value = `Maximum 4 pictures are allowed.`;
    e.target.value = "";
    return;
  }

  let candidates = files;

  // ถ้าเลือกไฟล์เกิน availableSlots → ตัดออก
  if (files.length > availableSlots) {
    candidates = files.slice(0, availableSlots);
    uploadError.value = `Maximum 4 pictures are allowed.`;
  }

  // Per-file size check
  const tooLarge = candidates.find((f) => f.size > MAX_PER_FILE);
  if (tooLarge) {
    uploadError.value = `The picture file size cannot be larger than 2MB.`;
    e.target.value = "";
    return;
  }

  // Total size check: existing + new
  const existingTotal = imageItems.value.reduce(
    (sum, it) => sum + (it.size || 0),
    0
  );
  const newTotal = candidates.reduce((sum, f) => sum + f.size, 0);
  if (existingTotal + newTotal > MAX_TOTAL) {
    uploadError.value = `Total size of images must be ≤ 5MB. Current total would be ${(
      (existingTotal + newTotal) /
      (1024 * 1024)
    ).toFixed(2)}MB.`;
    e.target.value = "";
    return;
  }

  // Append new selections
  candidates.forEach((f, index) => {
    const url = URL.createObjectURL(f);

    imageItems.value.push({
      name: f.name,
      url,
      file: f,
      size: f.size,
      state: "CREATE",
      order: existingCount + index + 1,
    });

    handleUpdateImage({ name: f.name, file: f }, "CREATE");
  });
  // reset so selecting the same file again retriggers change
  e.target.value = "";
};

// UI-only helpers: remove and reorder images
const removeImageAt = (index) => {
  const it = imageItems.value[index];
  if (it?.url && typeof it.url === "string" && it.url.startsWith("blob:")) {
    URL.revokeObjectURL(it.url);
  }
  const imgRm = imageItems.value.splice(index, 1);
  // Adjust selected preview index if needed
  if (selectedMainIndex.value >= imageItems.value.length) {
    selectedMainIndex.value = Math.max(0, imageItems.value.length - 1);
  } else if (index <= selectedMainIndex.value) {
    selectedMainIndex.value = Math.max(0, selectedMainIndex.value - 1);
  }
  handleUpdateImage(
    imgRm[0],
    imgRm[0].state !== "CREATE" ? "DELETE" : "CREATE"
  );
};

const swap = (arr, i, j) => {
  const tmp = arr[i];
  arr[i] = arr[j];
  arr[j] = tmp;
};

const moveImage = (index, dir) => {
  const newIndex = index + dir;
  if (newIndex < 0 || newIndex >= imageItems.value.length) return;
  swap(imageItems.value, index, newIndex);
  // Keep selected preview consistent when swapping
  if (selectedMainIndex.value === index) selectedMainIndex.value = newIndex;
  else if (selectedMainIndex.value === newIndex)
    selectedMainIndex.value = index;
};

const hasImages = computed(() => imageItems.value.length > 0);

// Main preview selection (does not change order)
const selectedMainIndex = ref(0);
const setPreview = (idx) => {
  if (idx >= 0 && idx < imageItems.value.length) {
    selectedMainIndex.value = idx;
  }
};

const handleUpdateImage = (img, state) => {
  if (state === "DELETE") {
    imageEditList.value.push({
      priority: priority.value++,
      fileName: img.fileName,
      state,
      imageViewOrder: img.imageViewOrder,
    });
  } else if (state === "CREATE") {
    const index = imageEditList.value.findIndex(
      (i) => i.fileName === img.name && i.state === img.state
    );
    if (index !== -1) {
      imageEditList.value.splice(index, 1);
    } else {
      imageEditList.value.push({
        priority: priority.value++,
        fileName: img.name,
        state,
        imageFile: img.file,
      });
    }
  }
};

const checkScreenSize = () => {
  if (screenSizeInch.value === undefined || screenSizeInch.value === "") {
    screenSizeInchPass.value = true;
  } else if (
    screenSizeInch.value <= 0 ||
    !/^\d+(\.\d{1,2})?$/.test(parseFloat(screenSizeInch.value))
  ) {
    screenSizeInchPass.value = false;
  }
};

const getAllBrands = async () => {
  try {
    brands.value = await getAllBrand(`${BASE_API_DOMAIN}`);
    brands.value.sort((a, b) => a.name.localeCompare(b.name));
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
      item.value = await getSaleItemByIdForEdit(`${BASE_API_DOMAIN}`, itemId);
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

      for (const img of item.value.saleItemImages) {
        const imgUrl = await getImageOfSaleItem(
          `${BASE_API_DOMAIN}`,
          itemId,
          img.imageViewOrder
        );
        imageItems.value.push({
          fileName: img.fileName,
          url: imgUrl,
          name: img.ogFileName,
          imageViewOrder: img.imageViewOrder,
        });
      }
    }
    tempImageList.value = [...imageItems.value];
  } catch (error) {
    console.log(error);
    item.value = null;
  }
};

const checkValidateInput = () => {
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

const checkAllNonOptionalFiled = () => {
  if (
    brandItem.value !== "" &&
    model.value !== "" &&
    price.value !== "" &&
    description.value !== ""
  ) {
    isContainAllNonOptionalFiled.value = true;
  } else {
    isContainAllNonOptionalFiled.value = false;
  }
};

const checkUpdatedFiled = () => {
  let updated =
    (model.value !== "" && model.value !== item.value.model) ||
    (brandItem.value?.id !== "" &&
      brandItem.value?.id !== item.value.brand?.id) ||
    (description.value !== "" &&
      description.value !== item.value.description) ||
    (price.value !== "" && price.value !== item.value.price) ||
    (ramGb.value !== "" && ramGb.value !== item.value.ramGb) ||
    (screenSizeInch.value !== "" &&
      screenSizeInch.value !== item.value.screenSizeInch) ||
    (quantity.value !== "" && quantity.value !== item.value.quantity) ||
    (storageGb.value !== "" && storageGb.value !== item.value.storageGb) ||
    (color.value !== "" && color.value !== item.value.color);

  // ==== เช็คภาพ ====
  if (!updated) {
    // ความยาวต่างกัน -> มีการเพิ่ม/ลบ
    if (imageItems.value.length !== tempImageList.value.length) {
      updated = true;
    } else {
      // เทียบทีละภาพ
      for (let i = 0; i < imageItems.value.length; i++) {
        const curr = imageItems.value[i];
        const orig = tempImageList.value[i];

        // ถ้า url ไม่ตรง (เช่น อัพโหลดใหม่) หรือ order เปลี่ยน
        if (
          curr.url !== orig.url ||
          curr.imageViewOrder !== orig.imageViewOrder ||
          curr.name !== orig.name
        ) {
          updated = true;
          break;
        }
      }
    }
  }
  isUpdatedFiled.value = updated;
};

const addUpdateNewSaleItem = async () => {
  try {
    const formData = new FormData();
    if (!props.isEditing) {
      formData.append("model", model.value);
      formData.append("brandId", brandItem.value.id);
      formData.append("description", description.value);
      formData.append("price", price.value);
      formData.append("ramGb", ramGb.value);
      formData.append("screenSizeInch", screenSizeInch.value);
      formData.append("quantity", quantity.value);
      formData.append("storageGb", storageGb.value);
      formData.append("color", color.value);
      imageItems.value.forEach((image) => {
        formData.append("images", image.file);
      });
    } else {
      for (const img of imageItems.value) {
        const res = await fetch(img.url);
        const blob = await res.blob();
        const file = new File([blob], img.name, { type: blob.type });
        if (img.state !== "CREATE") {
          handleUpdateImage(img, "DELETE");
          handleUpdateImage({ name: img.name, file: file }, "CREATE");
        }
      }
      const stateOrder = { DELETE: 0, CREATE: 1 };
      imageEditList.value.sort((a, b) => {
        // เปรียบเทียบ state ก่อน
        const stateDiff = stateOrder[a.state] - stateOrder[b.state];
        if (stateDiff !== 0) return stateDiff;

        // ถ้า state เท่ากัน → ใช้ priority ต่อ
        return a.priority - b.priority;
      });
      if (model.value) {
        formData.append("saleItem.model", model.value);
      }
      if (brandItem.value?.id) {
        formData.append("saleItem.brandId", brandItem.value.id);
      }
      if (description.value) {
        formData.append("saleItem.description", description.value);
      }
      if (price.value) {
        formData.append("saleItem.price", price.value);
      }
      if (ramGb.value != null) {
        formData.append("saleItem.ramGb", ramGb.value);
      }
      if (screenSizeInch.value != null) {
        formData.append("saleItem.screenSizeInch", screenSizeInch.value);
      }
      if (quantity.value != null) {
        formData.append("saleItem.quantity", quantity.value);
      }
      if (storageGb.value != null) {
        formData.append("saleItem.storageGb", storageGb.value);
      }
      if (color.value) {
        formData.append("saleItem.color", color.value);
      }
      imageEditList.value.forEach((img, index) => {
        formData.append(`imageInfos[${index}].status`, img.state);
        if (img.fileName && img.state !== "CREATE") {
          formData.append(`imageInfos[${index}].fileName`, img.fileName);
        }
        if (img.imageViewOrder !== undefined && img.imageViewOrder !== null) {
          formData.append(
            `imageInfos[${index}].imageViewOrder`,
            img.imageViewOrder.toString()
          );
        }
        if (img.imageFile) {
          formData.append(`imageInfos[${index}].imageFile`, img.imageFile);
        }
      });
    }

    if (!props.isEditing) {
      await createSaleItemSeller(
        `${BASE_API_DOMAIN}`,
        decoded.jti,
        accessToken,
        formData
      );
    } else {
      await updateSaleItem(`${BASE_API_DOMAIN}`, itemId, formData);
    }
    goBackToPreviousPage();
  } catch (error) {
    console.log(error);
  }
};

const checkDisabled = () => {
  if (
    !isContainAllNonOptionalFiled.value &&
    !isUpdatedFiled.value &&
    !validInput.value
  ) {
    isDisabled.value = true;
  } else if (
    isContainAllNonOptionalFiled.value &&
    isUpdatedFiled.value &&
    !validInput.value
  ) {
    isDisabled.value = true;
  } else if (
    isContainAllNonOptionalFiled.value &&
    !isUpdatedFiled.value &&
    !validInput.value
  ) {
    isDisabled.value = true;
  } else if (
    !isContainAllNonOptionalFiled.value &&
    isUpdatedFiled.value &&
    validInput.value
  ) {
    isDisabled.value = true;
  } else if (
    !isContainAllNonOptionalFiled.value &&
    !isUpdatedFiled.value &&
    validInput.value
  ) {
    isDisabled.value = true;
  } else if (
    !isContainAllNonOptionalFiled.value &&
    isUpdatedFiled.value &&
    !validInput.value
  ) {
    isDisabled.value = true;
  } else if (
    isContainAllNonOptionalFiled.value &&
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
  getAllBrands();
});

onBeforeUnmount(() => {
  imageItems.value.forEach((it) => {
    if (it?.url && typeof it.url === "string" && it.url.startsWith("blob:")) {
      URL.revokeObjectURL(it.url);
    }
  });
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
    imageItems.value,
  ],
  () => {
    checkAllNonOptionalFiled();
    checkUpdatedFiled();
  },
  { immediate: true }
);

watch(
  imageItems.value,
  () => {
    checkDisabled();
  },
  { immediate: true }
);
</script>

<template>
  <NavBar />
  <SaleItemNotFound v-if="item === null" />
  <div v-else class="add-edit-container text-white">
    <AlertMessage
      v-if="uploadError"
      title="Warning"
      :message="uploadError"
      :over-image="uploadError ? true : false"
      @toggleShowModal="uploadError = ''"
    />
    <div class="flex py-7 mx-20 border-b border-white">
      <RouterLink
        @click="statusStore.clearEntityAndMethodAndStatusAndMessage()"
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
      {{
        isEditing
          ? `Edit ${item.model} ${item.color === null ? "" : item.color}`
          : "Add New Sale Item"
      }}
    </h1>
    <p class="mx-20 mt-3 text-white/80">
      {{
        isEditing
          ? `It's the little details that make a product truly complete.`
          : "Every product you add is another step toward your success."
      }}
    </p>
    <form @submit.prevent="addUpdateNewSaleItem" class="py-[35px] text-lg">
      <div class="grid grid-cols-2 gap-20 mx-20">
        <div class="self-center">
          <div v-if="hasImages" class="flex flex-col items-center">
            <img
              :src="imageItems[selectedMainIndex]?.url"
              alt="primary"
              class="mx-auto object-cover w-150 h-100 border rounded-xl"
            />
            <div class="flex mt-3 md:gap-3">
              <img
                v-for="(it, idx) in imageItems.slice(0, 4)"
                :key="it.url + ':' + idx + '-thumb'"
                :src="it.url"
                :alt="`thumb-${idx + 1}`"
                @click="setPreview(idx)"
                class="object-cover w-30 h-30 border rounded-xl hover:cursor-pointer"
                :class="{ 'ring-4 ring-blue-400': selectedMainIndex === idx }"
              />
            </div>
          </div>
          <div
            v-else
            class="mx-auto w-150 h-100 border-2 border-dashed border-white/40 rounded-xl flex flex-col gap-2 items-center justify-center text-white/60"
          >
            <p>No images uploaded</p>
            <p class="mt-1 text-xs text-white/60">
              Max 4 images • ≤ 2MB each • ≤ 5MB total
            </p>
          </div>
          <div class="w-fit mx-3 my-4">
            <button
              type="button"
              @click="openFileDialog()"
              class="py-1 px-2 border rounded text-sm hover:bg-white hover:text-black hover:cursor-pointer duration-200"
            >
              Upload Images
            </button>
            <input
              ref="fileInputRef"
              type="file"
              class="hidden"
              multiple
              accept="image/*"
              @change="onFilesSelected"
            />
          </div>
          <div v-if="imageItems.length" class="mx-3 w-[360px] space-y-2">
            <div
              v-for="(it, idx) in imageItems"
              :key="it.url + ':' + idx"
              class="grid grid-cols-[40px_1fr_40px_40px] items-center gap-2"
            >
              <div
                class="text-black bg-white/80 rounded text-center py-1 select-none"
              >
                {{ idx + 1 }}
              </div>
              <div class="bg-white/60 text-black rounded py-1 px-3 truncate">
                {{ it.name }}
              </div>
              <button
                type="button"
                class="bg-white/80 text-black rounded hover:bg-white duration-150"
                @click="removeImageAt(idx)"
                title="Remove"
              >
                ✖
              </button>
              <div class="flex flex-col gap-1">
                <button
                  type="button"
                  class="bg-white/80 text-black rounded hover:bg-white duration-150 disabled:opacity-50"
                  :disabled="idx === 0"
                  @click="moveImage(idx, -1)"
                  title="Move up"
                >
                  ▲
                </button>
                <button
                  type="button"
                  class="bg-white/80 text-black rounded hover:bg-white duration-150 disabled:opacity-50"
                  :disabled="idx === imageItems.length - 1"
                  @click="moveImage(idx, 1)"
                  title="Move down"
                >
                  ▼
                </button>
              </div>
            </div>
          </div>
        </div>
        <div class="flex flex-col space-y-4">
          <h1 class="pb-1 text-3xl border-b">Overview</h1>
          <label>Brand<span>*</span></label>
          <select
            autofocus
            @blur="
              brandItem === '' ? (brandPass = false) : (brandPass = true),
                checkValidateInput(),
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
                checkValidateInput(),
                checkDisabled()
            "
            placeholder="e.g. iPhone 15 Pro Max"
            ref="modelInput"
            v-model.trim="model"
            required
            type="text"
            class="itbms-model"
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
              description?.length > 16384 ||
              description === undefined ||
              description === ''
                ? (descriptionPass = false)
                : (descriptionPass = true),
                checkValidateInput(),
                checkDisabled()
            "
            placeholder="e.g. Flagship smartphone with A17 chip and 256GB storage"
            ref="descriptionInput"
            v-model.trim="description"
            required
            class="itbms-description px-4 py-2 h-32 rounded-xl bg-[rgba(22,22,23,255)]"
          ></textarea>
          <h1
            v-if="!descriptionPass && descriptionPass !== null"
            class="itbms-message text-red-400 text-sm"
          >
            Description must be 1-16,384 characters long.
          </h1>
          <h1 class="pb-1 text-3xl border-b mt-10">Pricing</h1>
          <label>Price ( ฿ )<span>*</span></label>
          <input
            @keydown.enter.prevent="focusNext('quantityInput')"
            @blur="
              price < 0 || price === undefined || price === ''
                ? (pricePass = false)
                : (pricePass = true),
                checkValidateInput(),
                checkDisabled()
            "
            placeholder="e.g. 12990"
            ref="priceInput"
            v-model="price"
            required
            type="number"
            class="itbms-price"
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
                checkValidateInput(),
                checkDisabled()
            "
            placeholder="e.g. 5"
            ref="quantityInput"
            v-model="quantity"
            type="number"
            class="itbms-quantity"
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
                checkValidateInput(),
                checkDisabled()
            "
            placeholder="e.g. Midnight Blue"
            ref="colorInput"
            v-model.trim="color"
            type="text"
            class="itbms-color"
          />
          <h1 v-if="!colorPass" class="itbms-message text-red-400 text-sm">
            Color must be 1-40 characters long or not specified.
          </h1>
          <label>Ram ( GB )</label>
          <input
            @keydown.enter.prevent="focusNext('screenSizeInput')"
            @blur="
              ramGb <= 0 && ramGb !== ''
                ? (ramGbPass = false)
                : (ramGbPass = true),
                checkValidateInput(),
                checkDisabled()
            "
            placeholder="e.g. 8"
            ref="ramInput"
            v-model="ramGb"
            type="number"
            class="itbms-ramGb"
          />
          <h1 v-if="!ramGbPass" class="itbms-message text-red-400 text-sm">
            RAM size must be positive integer or not specified.
          </h1>
          <label>Screen Size ( Inches )</label>
          <input
            @keydown.enter.prevent="focusNext('storageInput')"
            @blur="checkScreenSize(), checkValidateInput(), checkDisabled()"
            placeholder="e.g. 6.7"
            v-model="screenSizeInch"
            type="number"
            class="itbms-screenSizeInch"
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
              storageGb <= 0 && storageGb !== ''
                ? (storageGbPass = false)
                : (storageGbPass = true),
                checkValidateInput(),
                checkDisabled()
            "
            placeholder="e.g. 256"
            ref="storageInput"
            v-model="storageGb"
            type="number"
            class="itbms-storageGb"
          />
          <h1 v-if="!storageGbPass" class="itbms-message text-red-400 text-sm">
            Storage size must be positive integer or not specified.
          </h1>
          <div class="btn-form mt-5 flex space-x-4 text-2xl">
            <button
              type="button"
              @click="goBackToPreviousPage"
              class="itbms-cancel-button w-full py-2 rounded-4xl bg-red-500 text-white hover:cursor-pointer hover:bg-red-500/80 duration-150"
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
                  : 'bg-blue-500 hover:text-white hover:cursor-pointer hover:bg-blue-500/80'
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
                  : 'bg-blue-500 hover:text-white hover:cursor-pointer hover:bg-blue-500/80'
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
