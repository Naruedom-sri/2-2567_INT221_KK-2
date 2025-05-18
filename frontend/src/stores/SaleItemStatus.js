import { defineStore } from "pinia";
import { ref } from "vue";

export const useSaleItemStatusStore = defineStore("saleItemStatus", () => {
  const statusCode = ref(null);
  const method = ref(null);

  const setStatusAndMethod = (str, code) => {
    statusCode.value = code;
    method.value = str;
  };

  const clearStatusAndMethod = () => {
    statusCode.value = null;
    method.value = null;
  };
  const getStatus = () => {
    return statusCode.value;
  };
  
  const getMethod = () => {
    return method.value;
  };

  return {
    statusCode,
    method,
    getMethod,
    setStatusAndMethod,
    clearStatusAndMethod,
    getStatus,
  };
});
