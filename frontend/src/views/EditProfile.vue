<script setup>
import { reactive } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/ีuserStore";

const router = useRouter();
const userData = useUserStore();

// เตรียมข้อมูลท้องถิ่นสำหรับฟอร์ม (สำเนาจาก store)
const form = reactive({
  nickname: userData.nickname || "",
  email: userData.email || "",
  fullname: userData.fullname || "",
  role: userData.role || "buyer",
  // seller fields
  sellerMobileNumber: userData.sellerMobileNumber || "",
  sellerBankAccountNumber: userData.sellerBankAccountNumber || "",
  sellerBankName: userData.sellerBankName || "",
});

// บันทึกข้อมูลกลับไปที่ store
function saveProfile() {
  userData.nickname = form.nickname;
  userData.email = form.email;
  userData.fullname = form.fullname;
  userData.role = form.role;
  // seller-only fields
  userData.sellerMobileNumber = form.sellerMobileNumber;
  userData.sellerBankAccountNumber = form.sellerBankAccountNumber;
  userData.sellerBankName = form.sellerBankName;

  // กลับไปหน้าโปรไฟล์
  router.push({ name: "Profile" }).catch(() => {
    /* ignore */
  });
}

function cancelEdit() {
  router.back();
}
</script>

<template>
  <div class="min-h-screen grid place-items-center bg-gray-0">
    <div class="w-full max-w-2xl rounded-2xl bg-white p-6">
      <div class="p-6px text-black">
        <h2>Edit Profile</h2>

        <div class="form-row">
          <label>Nickname:</label>
          <input v-model="form.nickname" type="text" />
        </div>

        <div class="form-row">
          <label>Email:</label>
          <input v-model="form.email" type="email" />
        </div>

        <div class="form-row">
          <label>Fullname:</label>
          <input v-model="form.fullname" type="text" />
        </div>

        <div class="form-row">
          <label>Type:</label>
          <p>{{ form.role }}</p>
        </div>

        <div v-if="form.role === 'seller'">
          <div class="form-row">
            <label>Mobile</label>
            <input v-model="form.sellerMobileNumber" type="text" />
          </div>
          <div class="form-row">
            <label>Bank Account No</label>
            <input v-model="form.sellerBankAccountNumber" type="text" />
          </div>
          <div class="form-row">
            <label>Bank Name</label>
            <input v-model="form.sellerBankName" type="text" />
          </div>
        </div>

        <div class="actions">
          <button
            @click="saveProfile"
            class="border-2 border-gray-500 rounded-md px-3 py-1 bg-gray-300 hover:bg-gray-400"
          >
            Save
          </button>
          <button
            @click="cancelEdit"
            type="button"
            class="border-2 border-gray-500 rounded-md px-3 py-1 bg-gray-300 hover:bg-gray-400"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.form-row {
  margin-bottom: 8px;
  display: flex;
  flex-direction: column;
}
.actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}
</style>
