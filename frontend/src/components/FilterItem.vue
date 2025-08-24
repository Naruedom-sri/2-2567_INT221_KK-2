<script setup>
const props = defineProps({
  label: String,
  class: String,
  isShow: Boolean,
  optionList: Array,
  filterList: Array,
});

const emit = defineEmits([
  "addToFilterList",
  "removeFromFilterList",
  "toggleIsShow",
  "addMinMaxPriceToFilterList",
]);
</script>

<template>
  <div :class="`${props.class} w-56 flex`">
     <p
      class="w-4 pt-0.5 bg-[rgba(22,22,23,255)]"
      :class="[props.isShow ? 'h-[240.5px] pt-3' : 'h-10',props.isShow && props.class === 'price'  ? 'rounded-tl':'rounded-l']"
    >
    </p>
    <div class="flex flex-col flex-1">
      <div
        class="scrollbar-hide flex flex-wrap overflow-y-auto h-10 items-center gap-2 py-1.5  border-gray-300 text-gray-300 bg-[rgba(22,22,23,255)]"
        :class="[
          props.isShow ? 'border-b':'',
          `itbms-${props.class}-filter`,
        ]"
      >
        <p v-if="props.filterList.length === 0" class="text-sm">
          {{ props.label }}
        </p>
        <div
          v-for="(item, index) in props.filterList"
          :key="index"
          class="flex justify-between  bg-blue-300 text-[#0d47a1] border border-gray-300 rounded-xl text-sm"
          :class="`itbms-${props.class}-item`"
        >
          <p class="ml-4">
            {{ item === -1 ? "Not specified" : item }}
            <span
              v-if="props.class === 'storage-size' && item !== 'Not specified'"
              class="unit"
              >{{ item !== "1" ? "Gb" : "Tb" }}</span
            >
          </p>
          <button
            @click="$emit('removeFromFilterList', item, props.class)"
            class="w-5 mx-2 hover:cursor-pointer hover:text-red-500"
            :class="`itbms-${props.class}-item-clear`"
          >
            x
          </button>
        </div>
      </div>
      <div
        v-if="props.isShow"
        class="py-2 overflow-y-auto scrollbar-hide h-50 flex flex-col bg-[rgba(22,22,23,255)] text-sm"
        :class="`${props.class}-filter-dropdown`"
      >
        <button
          @click="$emit('addToFilterList', option, props.class)"
          v-for="(option, index) in props.optionList"
          :key="index"
          class="py-2 text-white duration-200"
          :class="[
            `itbms-${props.class}-item`,
            props.filterList.includes(option) ||
            props.filterList.includes(option.name)
              ? 'opacity-50 cursor-not-allowed'
              : 'hover:bg-gray-300 hover:rounded hover:text-[#0d47a1] cursor-pointer',
          ]"
          :disabled="
            props.filterList.includes(option) ||
            props.filterList.includes(option.name)
          "
        >
          {{ props.class === "brand" ? option.name : option }}
          <span v-if="props.class === 'price'" class="unit">Baht</span>
          <span
            v-if="props.class === 'storage-size' && option !== 'Not specified'"
            class="unit"
            >{{ option !== 1 ? "Gb" : "Tb" }}</span
          >
        </button>
      </div>
    </div>
    <p
      @click="emit('toggleIsShow', props.class)"
      class="w-4 pt-0.5  bg-[rgba(22,22,23,255)]"
      :class="[props.isShow ? 'h-[240.5px] pt-3' : 'h-10'], props.isShow && props.class === 'price'  ? 'rounded-tr':'rounded-r'"
    >
<<<<<<< HEAD
      <button
        @click="$emit('addToFilterList', option, props.class)"
        v-for="(option, index) in props.optionList"
        :key="index"
        class="py-2 border-x text-white duration-200"
        :class="[
          index === props.optionList.length - 1 && props.class !== 'price'
            ? 'border-b'
            : '',
          `itbms-${props.class}-item`,
          props.filterList.includes(option) ||
          props.filterList.includes(option.name) 
            ? 'opacity-50 cursor-not-allowed'
            : 'hover:bg-blue-500 cursor-pointer',
        ]"
        :disabled="
          props.filterList.includes(option) ||
          props.filterList.includes(option.name) 
        "
      >
        {{ props.class === "brand" ? option.name : option }}
        <span v-if="props.class === 'price'" class="unit">Baht</span>
        <span
          v-if="props.class === 'storage-size' && option !== 'Not specified'"
          class="unit"
          >{{ option !== 1 ? "Gb" : "Tb" }}</span
        >
      </button>
    </div>
=======
      {{ props.isShow ? "&#8963" : '&#8964' }}
    </p>
>>>>>>> cd5820a0c4ce861466b8c65ab9051713d0eb654d
  </div>
</template>

<style scoped>
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
</style>
