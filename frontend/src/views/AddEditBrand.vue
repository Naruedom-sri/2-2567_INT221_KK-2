DeletCan
<script setup>
import NavBar from "@/components/NavBar.vue";
import { ref, onMounted, watch } from "vue";
import { createData, updateData, getDataById } from "@/libs/api";
import { useRouter, useRoute } from "vue-router";
import { useSaleItemStatusStore } from "@/stores/SaleItemStatus";
import Footer from "@/components/Footer.vue";
import BrandNotFound from "@/components/BrandNotFound.vue";

const {
  params: { brandId },
} = useRoute();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const props = defineProps({
  isEditing: Boolean,
});
const statusStore = useSaleItemStatusStore();
const route = useRouter();
const brand = ref({});
const name = ref();
const websiteUrl = ref();
const country = ref();
const isActive = ref(true);
const isContainAllNonOtionalFiled = ref(false);
const isUpdatedFiled = ref(false);
const isDisabled = ref(true);

const nameInput = ref();
const websiteUrlInput = ref();
const countryInput = ref();

const namePass = ref(null);
const websitePass = ref(true);
const countryPass = ref(true);
const validInput = ref(false);

function isValidUrl() {
  try {
    new URL(websiteUrl.value);
    return true;
  } catch (error) {
    return false;
  }
}

const checkValidateInput = () => {
  validInput.value =
    namePass.value && websitePass.value && countryPass.value ? true : false;
};

const checkIsEditing = async () => {
  try {
    if (props.isEditing) {
      brand.value = await getDataById(`${BASE_API_DOMAIN}/v1/brands`, brandId);
      name.value = brand.value.name;
      websiteUrl.value = brand.value.websiteUrl;
      country.value = brand.value.countryOfOrigin;
      isActive.value = brand.value.isActive;
      namePass.value = true;
    }
  } catch (error) {
    console.log(error);
    brand.value = null;
  }
};

const checkAllNonOptionalFiled = () => {
  if (name.value !== undefined && name.value !== "") {
    isContainAllNonOtionalFiled.value = true;
  } else {
    isContainAllNonOtionalFiled.value = false;
  }
};

const checkUpdatedFiled = () => {
  if (
    name.value !== brand.value.name ||
    websiteUrl.value !== brand.value.websiteUrl ||
    country.value !== brand.value.countryOfOrigin ||
    (isActive.value !== brand.value.isActive &&
      isActive.value !== JSON.stringify(brand.value.isActive))
  ) {
    isUpdatedFiled.value = true;
  } else {
    isUpdatedFiled.value = false;
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

const addUpdateNewBrand = async () => {
  try {
    const newBrand = {
      name: name.value,
      websiteUrl: websiteUrl.value,
      countryOfOrigin: country.value,
      isActive: isActive.value,
    };
    if (!props.isEditing) {
      const data = await createData(`${BASE_API_DOMAIN}/v1/brands`, newBrand);
      if (data) {
        statusStore.setStatusAndMethod("add", 201);
      }
    } else {
      const data = await updateData(
        `${BASE_API_DOMAIN}/v1/brands`,
        brandId,
        newBrand
      );
      if (data) {
        statusStore.setStatusAndMethod("update", 200);
      }
    }
    goBackToPreviousPage();
  } catch (error) {
    console.log(error);
    goBackToPreviousPage();
  }
};

const focusNext = (refName) => {
  switch (refName) {
    case "nameInput":
      nameInput.value?.focus();
      break;
    case "websiteUrlInput":
      websiteUrlInput.value?.focus();
      break;
    case "countryInput":
      countryInput.value?.focus();
      break;
  }
};

const goBackToPreviousPage = () => {
  if (!props.isEditing) {
    route.push({ name: "BrandList" });
  } else {
    route.back();
  }
};
watch(
  [name, websiteUrl, country, isActive],
  () => {
    checkAllNonOptionalFiled();
    checkUpdatedFiled();
  },
  { immediate: true }
);
onMounted(() => {
  checkIsEditing();
});
</script>

<template>
  <NavBar />
  <BrandNotFound v-if="brand === null" />
  <div v-else class="add-edit-brand text-white">
    <div class="flex py-7 mx-20 border-b border-white">
      <RouterLink
        :to="{ name: 'SaleItemsList' }"
        class="itbms-home-button hover:text-blue-500 hover:cursor-pointer duration-100"
      >
        Sale Item List
      </RouterLink>
      <h1 class="mx-3">/</h1>
      <RouterLink
        :to="{ name: 'BrandList' }"
        class="itbms-manage-brand hover:text-blue-500 hover:cursor-pointer duration-100"
      >
        Brand List
      </RouterLink>
      <h1 class="mx-3">/</h1>
      <h1
        v-if="!isEditing"
        class="px-3 rounded-2xl bg-gradient-to-r from-pink-400 to-purple-500"
      >
        New Brand
      </h1>
      <button
        v-else
        @click="goBackToPreviousPage"
        class="itbms-back-button px-3 rounded-2xl bg-gradient-to-r from-pink-400 to-purple-500"
      >
        {{ brand.name }}
      </button>
    </div>
    <h1
      class="w-fit mx-20 mt-5 text-5xl font-semibold bg-gradient-to-r from-pink-400 to-purple-400 bg-clip-text text-transparent"
    >
      {{ isEditing ? `Edit ${brand.name}` : "Add New Brand" }}
    </h1>
    <p class="mx-20 mt-3 text-white/80">
      {{
        isEditing
          ? `A strong brand starts with a clear core.`
          : "A brand is not just a logo, it's the feeling people have about you."
      }}
    </p>
    <form @submit.prevent="addUpdateNewBrand" class="py-[35px] text-lg">
      <div class="grid grid-cols-2 gap-10 mx-20">
        <div class="w-full">
          <img
            src="/src/assets/imgs/symbol-brand.png"
            alt="symbol-brand"
            class="w-72 mx-auto"
          />
          <div class="w-52 flex gap-2">
            <img
              src="/src/assets/imgs/symbol-brand.png"
              alt="symbol-brand"
              class="object-cover border rounded-xl"
            />
            <img
              src="/src/assets/imgs/symbol-brand.png"
              alt="symbol-brand"
              class="object-cover border rounded-xl"
            />
            <img
              src="/src/assets/imgs/symbol-brand.png"
              alt="symbol-brand"
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
          <label>Name<span>*</span></label>
          <input
            autofocus
            @keydown.enter.prevent="focusNext('websiteUrlInput')"
            @blur="
              name?.length > 30 || name === undefined || name === ''
                ? (namePass = false)
                : (namePass = true),
                checkValidateInput(),
                checkDisabled()
            "
            placeholder="e.g. Apple"
            ref="nameInput"
            v-model.trim="name"
            required
            type="text"
            class="itbms-name"
          />
          <h1
            v-if="!namePass && namePass !== null"
            class="itbms-message text-red-400 text-sm"
          >
            Brand name must be 1-30 characters long.
          </h1>
          <label>Website Url</label>
          <input
            @keydown.enter.prevent="focusNext('countryInput')"
            @blur="
              websiteUrl?.length > 40 ||
              (!isValidUrl() && websiteUrl !== '' && websiteUrl !== undefined)
                ? (websitePass = false)
                : (websitePass = true),
                checkValidateInput(),
                checkDisabled()
            "
            placeholder="e.g. https://www.apple.com"
            ref="websiteUrlInput"
            v-model.trim="websiteUrl"
            type="text"
            class="itbms-websiteUrl"
          />
          <h1 v-if="!websitePass" class="itbms-message text-red-400 text-sm">
            Brand URL must be a valid URL or not specified.
          </h1>
          <h1>isActive</h1>
          <div class="space-x-2">
            <input
              v-model="isActive"
              type="checkbox"
              @click="
                isActive === true ? (isActive = false) : (isActive = true),
                  checkUpdatedFiled(),
                  checkDisabled()
              "
              class="itbms-isActive"
            />
          </div>

          <label>Country of Origin</label>
          <input
            @keydown.enter.prevent="focusNext('nameInput')"
            @blur="
              country?.length > 80
                ? (countryPass = false)
                : (countryPass = true),
                checkValidateInput(),
                checkDisabled()
            "
            placeholder="e.g. America"
            ref="countryInput"
            v-model.trim="country"
            type="text"
            max="80"
            class="itbms-countryOfOrigin"
          />
          <h1 v-if="!countryPass" class="itbms-message text-red-400 text-sm">
            Brand country of origin must be 1-80 characters long or not
            specified.
          </h1>
          <div class="btn-form mt-5 flex space-x-4 text-2xl">
            <button
              type="button"
              @click="goBackToPreviousPage"
              class="itbms-cancel-button w-full py-2 rounded-4xl bg-red-500 hover:cursor-pointer hover:bg-red-500/80 hover:text-white duration-150"
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
                  ? 'border border-gray-400 text-gray-400 '
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
