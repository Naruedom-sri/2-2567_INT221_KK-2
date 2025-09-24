import { defineStore } from "pinia";
import { ref } from "vue";

export const useUserStore = defineStore("user", () => {
  const role = ref("");
  const email = ref("");
  const nickname = ref("");
  const fullname = ref("");

  const mobileNumber = ref("");
  const bankName = ref("");
  const bankAccountNumber = ref("");

  const setUser = (userData) => {
    role.value = userData.role || "";
    email.value = userData.email || "";
    nickname.value = userData.nickname || "";
    fullname.value = userData.fullName || "";

    if (role.value === "SELLER") {
      mobileNumber.value = userData.mobileNumber || "";
      bankName.value = userData.bankName || "";
      bankAccountNumber.value = userData.bankAccountNumber || "";
    } else {
      mobileNumber.value = "";
      bankName.value = "";
      bankAccountNumber.value = "";
    }
  };

  const clearUser = () => {
    role.value = "";
    email.value = "";
    nickname.value = "";
    fullname.value = "";
    mobileNumber.value = "";
    sellerBankName.value = "";
    bankAccountNumber.value = "";
  };

  return {
    role,
    email,
    nickname,
    fullname,
    mobileNumber,
    bankName,
    bankAccountNumber,
    setUser,
    clearUser,
  };
});
