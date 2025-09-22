import { defineStore } from "pinia";
import { ref } from "vue";

export const useTokenStore = defineStore("tokenStore", () => {
  const accessToken = ref(null);
  const decode = ref(null);

  const setAccessToken = (token) => {
    accessToken.value = token;
  };

  const setDecode = (d) => {
    decode.value = d;
  };

  const clearAccessToken = () => {
    accessToken.value = null;
  };

  const clearDecode = () => {
    decode.value = null;
  };

  const getAccessToken = () => {
    return accessToken.value;
  };

  const getDecode = () => {
    return decode.value;
  };

  return {
    setAccessToken,
    setDecode,
    clearDecode,
    clearAccessToken,
    getAccessToken,
    getDecode,
  };
});
