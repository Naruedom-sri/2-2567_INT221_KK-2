<script setup>
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;

const isVerifying = ref(true);
const isSuccess = ref(false);
const errorMessage = ref("");

const isResending = ref(false);
const resendEmail = ref("");
const resendMessage = ref("");

const verify = async (token) => {
  try {
    console.log(token);
    let res = await fetch(
      `${BASE_API_DOMAIN}/v2/users/verify-email?token=${encodeURIComponent(
        token
      )}`,
      {
        method: "POST",
      }
    );

    // if (!res.ok) {
    //   res = await fetch(`${BASE_API_DOMAIN}/v2/users/verify-email`, {
    //     method: "POST",
    //     headers: { "Content-Type": "application/json" },
    //     body: JSON.stringify({ token }),
    //   });
    // }

    if (!res.ok) {
      const text = await res.text().catch(() => "");
      throw new Error(text || `Request failed with status ${res.status}`);
    }

    isSuccess.value = true;
  } catch (err) {
    console.error("Email verification failed:", err);
    errorMessage.value =
      "An error occurred, or the verification link has expired. Please request a new verification email.";
  } finally {
    isVerifying.value = false;
  }
};

const handleResend = async () => {
  resendMessage.value = "";
  if (
    !resendEmail.value ||
    !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(resendEmail.value)
  ) {
    resendMessage.value = "Please enter a valid email address.";
    return;
  }
  try {
    isResending.value = true;
    const res = await fetch(
      `${BASE_API_DOMAIN}/v2/users/resend-verification-email`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: resendEmail.value.trim() }),
      }
    );
    if (!res.ok) {
      const text = await res.text().catch(() => "");
      throw new Error(text || `Resend failed with status ${res.status}`);
    }
    resendMessage.value =
      "If an account exists for this email, a new verification link has been sent.";
  } catch (err) {
    console.error("Resend verification error:", err);
    resendMessage.value = "Unable to resend verification email right now.";
  } finally {
    isResending.value = false;
  }
};

const goHome = () => router.push({ name: "SaleItemsGallery" });
onMounted(() => {
  const tokenRaw = route.query.token ?? route.params?.token;
  const token = typeof tokenRaw === "string" ? tokenRaw.trim() : "";
  if (!token) {
    isVerifying.value = false;
    errorMessage.value = "Invalid verification link.";
    return;
  }
  verify(token);
});
</script>

<template>
  <div class="min-h-screen grid place-items-center bg-gray-0">
    <div class="w-full max-w-xl rounded-2xl bg-white p-6 text-center">
      <h1 class="text-2xl font-semibold mb-4">Email Verification</h1>

      <div v-if="isVerifying" class="py-8">
        <div
          class="mx-auto mb-3 h-8 w-8 border-4 border-gray-200 border-t-black rounded-full animate-spin"
        ></div>
        <p>Verifying your email…</p>
      </div>

      <div v-else>
        <div v-if="isSuccess" class="py-6">
          <p class="text-green-600 font-medium mb-6">
            Your account has been successfully activated.
          </p>
          <button
            class="px-4 py-2 bg-black text-white rounded-lg"
            @click="goHome"
          >
            Continue
          </button>
        </div>

        <div v-else class="py-4">
          <p class="text-red-600 mb-4">{{ errorMessage }}</p>
          <div class="mt-4 text-left">
            <label class="block text-sm mb-1">Email</label>
            <input
              v-model="resendEmail"
              type="email"
              placeholder="your@email.com"
              class="w-full border rounded-lg px-3 py-2"
            />
            <button
              class="mt-3 px-4 py-2 bg-black text-white rounded-lg disabled:opacity-60"
              :disabled="isResending"
              @click="handleResend"
            >
              {{ isResending ? "Sending…" : "Resend verification email" }}
            </button>
            <p v-if="resendMessage" class="mt-3 text-sm text-gray-600">
              {{ resendMessage }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
