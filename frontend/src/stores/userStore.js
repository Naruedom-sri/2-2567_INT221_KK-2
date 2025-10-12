import { defineStore } from "pinia";
import { ref } from "vue";

export const useUserStore = defineStore("user", () => {
  const id = ref("")
  const role = ref("");
  const email = ref("");
  const nickname = ref("");
  const fullname = ref("");

  const mobileNumber = ref("");
  const bankName = ref("");
  const bankAccountNumber = ref("");

  const setUser = (userData) => {
    id.value = userData.id || userData.userId || userData._id || "";
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
    id.value = "";
    role.value = "";
    email.value = "";
    nickname.value = "";
    fullname.value = "";
    mobileNumber.value = "";
    sellerBankName.value = "";
    bankAccountNumber.value = "";
  };

  const getUser = () => {
    return {
      id: id.value,
      role: role.value,
      email: email.value,
      nickname: nickname.value,
      fullname: fullname.value,
      mobileNumber: mobileNumber.value,
      bankName: bankName.value,
      bankAccountNumber: bankAccountNumber.value,
    };
  };

  return {
    id,
    role,
    email,
    nickname,
    fullname,
    mobileNumber,
    bankName,
    bankAccountNumber,
    setUser,
    clearUser,
    getUser,
  };
});
