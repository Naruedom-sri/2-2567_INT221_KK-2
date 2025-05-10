<script setup>
defineProps({
  visible: Boolean,
  title: { type: String, default: 'Delete Confirmation' },
  message: { type: String, default: 'Do you want to delete this sale item?' },
})

const emit = defineEmits(['confirm', 'cancel'])

const confirm = () => emit('confirm')
const cancel = () => emit('cancel')
</script>

<template>
  <div v-if="visible" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white p-6 rounded shadow w-80 text-center">
      <h2 class="font-semibold mb-4">{{ title }}</h2>
      <p class="mb-6 text-sm text-gray-600">{{ message }}</p>
      <div class="flex justify-around">
        <button class="bg-gray-300 px-4 py-2 rounded" @click="cancel">Cancel</button>
        <button class="bg-red-600 text-white px-4 py-2 rounded" @click="confirm">Confirm</button>
      </div>
    </div>
  </div>
</template>

<style scoped></style>