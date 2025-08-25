<script setup>
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
</script>

<template>
  <NavBar />
  <div class="about-us-container mt-10 text-white">
    <div class="animation-slide-up consulter w-fit mx-auto mb-10 space-y-3 text-center">
      <img
        src="/src/assets/imgs/persons/consulter.png"
        alt="consulter"
        class="object-cover w-72 h-72 rounded-full"
      />
      <h1 class="text-xl font-semibold">Consulter</h1>
      <p>Asst. Prof. Dr. Kriengkrai Porkaew</p>
      <p>porkaew@sit.kmutt.ac.th</p>
      <p>0-2470-9868</p>
    </div>
    <hr>
    <div class="animation-slide-up member grid grid-cols-3 my-10">
      <div class="space-y-3 text-center">
        <img
          src="/src/assets/imgs/persons/member.png"
          alt="consulter"
          class="object-cover w-60 h-60 mx-auto rounded-full"
        />
        <h1 class="text-xl font-semibold">Member</h1>
        <p>66130500040</p>
        <p>Mr. Thanarat Pudpoh</p>
        <p>66130500040@ad.sit.kmutt.ac.th</p>
        <p>096-947-7650</p>
      </div>
      <div class="space-y-3 text-center">
        <img
          src="/src/assets/imgs/persons/member.png"
          alt="consulter"
          class="object-cover w-60 h-60 mx-auto rounded-full"
        />
        <h1 class="text-xl font-semibold">Member</h1>
        <p>66130500051</p>
        <p>Mr. Naruedom Srirukkaew</p>
        <p>66130500051@ad.sit.kmutt.ac.th</p>
        <p>092-678-7173</p>
      </div>
      <div class="space-y-3 text-center">
        <img
          src="/src/assets/imgs/persons/member.png"
          alt="consulter"
          class="object-cover w-60 h-60 mx-auto rounded-full"
        />
        <h1 class="text-xl font-semibold">Member</h1>
        <p>66130500114</p>
        <p>Mr. Noppawut Phomchana</p>
        <p>66130500114@ad.sit.kmutt.ac.th</p>
        <p>094-653-8565</p>
      </div>
    </div>
  </div>
  <Footer />
</template>

<style scoped>
.animation-slide-up {
  animation: slide-up 0.5s ease-out;
}
@keyframes slide-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
