import { defineStore } from "pinia";
import { ref } from "vue";

export const useStatusStore = defineStore("statusStore", () => {
  const statusCode = ref(null);
  const entity = ref(null);
  const method = ref(null);
  const message = ref(null);

  const setEntityAndMethodAndStatusAndMessage = (e, m, code, msg) => {
    entity.value = e;
    method.value = m;
    statusCode.value = code;
    message.value = msg;
  };

  const clearEntityAndMethodAndStatusAndMessage = () => {
    entity.value = null;
    method.value = null;
    statusCode.value = null;
    message.value = null;
  };

  const getEntity = () => {
    return entity.value;
  };

  const getMethod = () => {
    return method.value;
  };

  const getStatus = () => {
    return statusCode.value;
  };

  const getMessage = () => {
    return message.value;
  };

  return {
    getMethod,
    setEntityAndMethodAndStatusAndMessage,
    clearEntityAndMethodAndStatusAndMessage,
    getStatus,
    getEntity,
    getMessage,
  };
});
