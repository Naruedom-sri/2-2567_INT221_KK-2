import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useSaleItemStatusStore = defineStore('saleItemStatus', () => {
  const statusCode = ref(null);

  const setStatus = (code) => {
    statusCode.value = code;
  };

  const clearStatus = () => {
    statusCode.value = null;
  };
  const getStatus = ()=>{
    return statusCode.value
  }

  return {
    statusCode,
    setStatus,
    clearStatus,
    getStatus,
  };
});
