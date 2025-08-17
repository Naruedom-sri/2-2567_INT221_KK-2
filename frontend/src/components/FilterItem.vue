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
  <div :class="`${props.class} w-80 flex flex-col`">
    <div
      @click="emit('toggleIsShow', props.class)"
      class="flex flex-wrap items-center gap-2 px-4 py-1.5 border"
      :class="[
        props.isShow ? 'rounded-t' : 'rounded',
        `itbms-${props.class}-filter`,
      ]"
    >
      <p v-if="props.filterList.length === 0" class="text-white/80">
        {{ props.label }}
      </p>
      <div
        v-for="(item, index) in props.filterList"
        :key="index"
        class="flex justify-between bg-[rgba(22,22,23,255)] rounded"
        :class="`itbms-${props.class}-item`"
      >
        <p class="mx-4">
          {{ item === -1 ? "Not specified" : item }}
          <span
            v-if="props.class === 'storage-size' && item !== -1"
            class="unit"
            >{{ item !== "1" ? "Gb" : "Tb" }}</span
          >
        </p>
        <button
          @click="$emit('removeFromFilterList', item, props.class)"
          class="w-5 bg-gray-300 rounded-r text-black hover:cursor-pointer"
          :class="`itbms-${props.class}-item-clear`"
        >
          x
        </button>
      </div>
    </div>
    <div
      v-if="props.isShow"
      class="overflow-y-scroll h-50 bg-[rgba(22,22,23,255)] flex flex-col"
      :class="`${props.class}-filter-dropdown`"
    >
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
        :disabled="props.filterList.includes(option)"
      >
        {{ props.class === "brand" ? option.name : option }}
        <span v-if="props.class === 'price'" class="unit">Bath</span>
        <span
          v-if="props.class === 'storage-size' && option !== 'Not specified'"
          class="unit"
          >{{ option !== "1" ? "Gb" : "Tb" }}</span
        >
      </button>
    </div>
  </div>
</template>

<style scoped></style>
