<script setup>
import { onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useStatusStore } from "@/stores/statusStore";

const route = useRoute();
const router = useRouter();
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;
const statusStore = useStatusStore();

const verify = async (token) => {
  try {
    const res = await fetch(
      `${BASE_API_DOMAIN}/v2/users/verify-email?token=${encodeURIComponent(
        token
      )}`,
      {
        method: "POST",
      }
    );

    if (!res.ok) {
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "user",
        "verify",
        res.status,
        "The account activate failed."
      );
      throw new Error(
        `Request failed with status ${res.status} and with body`,
        res.json
      );
    }
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "user",
      "verify",
      res.status,
      "The account has been successfully activated."
    );
    router.push({ name: "SaleItemsGallery" });
  } catch (err) {
    console.error(err);
  }
};

onMounted(() => {
  const tokenRaw = route.query.token ?? route.params?.token;
  const token = typeof tokenRaw === "string" ? tokenRaw.trim() : "";
  if (!token) {
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "user",
      "verify",
      400,
      "Invalid link verify."
    );
  }
  verify(token);
});
</script>

<template></template>

<style scoped></style>
