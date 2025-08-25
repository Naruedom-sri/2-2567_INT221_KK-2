<script setup>
</script>

<template>

  <div class="container-system h-full bg-black overflow-hidden ">
    <RouterView :key="$route.fullPath" />
  </div>
</template>

<style scoped>
.container-system {
  /* font-family: "Prompt", sans-serif; */
   font-family: "Montserrat", sans-serif;
}
</style>
