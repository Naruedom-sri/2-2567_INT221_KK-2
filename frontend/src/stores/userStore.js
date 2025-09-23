import { defineStore } from "pinia";
import { ref } from "vue";

export const useUserStore = defineStore("user", () => {
  const role = ref(""); 
  const email = ref("");
  const nickname = ref("");
  const fullname = ref("");

  const sellerMobileNumber = ref("");
  const sellerBankName = ref("");
  const sellerBankAccountNumber = ref("");

  const setUser = (userData) => {
    role.value = userData.role || "";
    email.value = userData.email || "";
    nickname.value = userData.nickname || "";
    fullname.value = userData.fullName || "";

    if (role.value === "seller") {
      sellerMobileNumber.value = userData.sellerMobileNumber || "";
      sellerBankName.value = userData.sellerBankName || "";
      sellerBankAccountNumber.value = userData.sellerBankAccountNumber || "";
    } else {
      sellerMobileNumber.value = "";
      sellerBankName.value = "";
      sellerBankAccountNumber.value = "";
    }
  };

  const clearUser = () => {
    role.value = "";
    email.value = "";
    nickname.value = "";
    fullname.value = "";
    sellerMobileNumber.value = "";
    sellerBankName.value = "";
    sellerBankAccountNumber.value = "";
  };

  return {
    role,
    email,
    nickname,
    fullname,
    sellerMobileNumber,
    sellerBankName,
    sellerBankAccountNumber,
    setUser,
    clearUser,
  };
});
