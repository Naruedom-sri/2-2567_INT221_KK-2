<script setup>
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
import { ref, onMounted } from "vue";
const brands = ["Apple", "Samsung", "Vivo", "Oppo", "Huawei", "Sony"];
const phones = ref([
  {
    brand: "Apple",
    name: "iPhone 16 Pro Max",
    price: 48900,
  },
  {
    brand: "Samsung",
    name: "Galaxy S25 Ultra 5G",
    price: 42900,
  },
  {
    brand: "Oppo",
    name: "A78 5G",
    price: 9999,
  },
  {
    brand: "Vivo",
    name: "X200 Pro",
    price: 39999,
  },
  {
    brand: "Xiaomi",
    name: "Xiaomi 14T",
    price: 17990,
  },
]);
const stilAnimation = ref(false);
const isShowDetail = ref(null);
const animationLeftImg = ref(null);
const animationMidImg = ref(null);
const animationRightImg = ref(null);
const scale = ref("scale-75");

const toggleImgLeft = () => {
  setTimeout(() => {
    const phone = phones.value.shift();
    phones.value.push(phone);
    scale.value = "scale-75";
  }, 1000);
};

const toggleImgRight = () => {
  setTimeout(() => {
    const phone = phones.value.pop();
    phones.value.unshift(phone);
    scale.value = "scale-75";
  }, 1000);
};
const slideLeft = () => {
  scale.value = "";
  stilAnimation.value = true;
  animationLeftImg.value = "left-shrink";
  animationMidImg.value = "mid-slide-left";
  animationRightImg.value = "right-slide-left";
};

const slideRight = () => {
  scale.value = "";
  stilAnimation.value = true;
  animationRightImg.value = "right-shrink";
  animationMidImg.value = "mid-slide-right";
  animationLeftImg.value = "left-slide-right";
};
const clearAnimation = () => {
  setTimeout(() => {
    isShowDetail.value = null;
    stilAnimation.value = false;
    animationMidImg.value = "";
    animationLeftImg.value = "";
    animationRightImg.value = "";
  }, 1000);
};
onMounted(() => {
  animationLeftImg.value = "left-slide-up";
  setTimeout(() => (animationMidImg.value = "mid-slide-up"), 500);
  setTimeout(() => (animationRightImg.value = "right-slide-up"), 250);
});
</script>

<template>
  <NavBar />
  <div class="home-container mx-36 text-white">
    <div class="img-slide w-full mx-auto my-10">
      <button
        :disabled="stilAnimation"
        @click="slideLeft(), toggleImgLeft(), clearAnimation()"
        class="absolute top-72 w-12 h-12 rounded-full bg-white/20 text-lg hover:cursor-pointer hover:bg-white hover:text-black hover:scale-105 duration-300"
      >
        <
      </button>
      <button
        :disabled="stilAnimation"
        @click="slideRight(), toggleImgRight(), clearAnimation()"
        class="absolute top-72 right-36 w-12 h-12 rounded-full bg-white/20 text-lg hover:cursor-pointer hover:bg-white hover:text-black hover:scale-105 duration-300"
      >
        >
      </button>
      <div class="flex justify-between">
        <div
          class="flex flex-col justify-center items-center"
          :class="[
            animationLeftImg === 'left-shrink' ? 'animation-shrink' : '',
            animationLeftImg === 'left-slide-right'
              ? 'animation-left-slide-right'
              : '',
            animationLeftImg === 'left-slide-up' ? 'animation-slide-up' : '',
            animationLeftImg === 'left-grow' ? 'animation-grow' : '',
            animationLeftImg === null ? 'invisible' : '',
            scale,
          ]"
        >
          <img
            v-if="!stilAnimation"
            @mouseover="isShowDetail = 'left'"
            @mouseleave="isShowDetail = null"
            @click="slideRight(), toggleImgRight(), clearAnimation()"
            :src="`/kk2/imgs/phones/${phones[0].name}.png`"
            :alt="`${phones[0]}`"
            class="w-96 object-cover hover:scale-105 hover:cursor-pointer opacity-50 hover:opacity-100 duration-500"
          />
          <img
            v-else
            :src="`/kk2/imgs/phones/${phones[0].name}.png`"
            :alt="`${phones[0]}`"
            class="w-96 object-cover"
          />
          <div
            class="text-center space-y-2"
            :class="[
              isShowDetail !== 'left' || stilAnimation
                ? 'invisible'
                : 'animation-slide-up',
            ]"
          >
            <h1 class="text-2xl font-bold">{{ phones[0].brand }}</h1>
            <h1 class="text-lg">{{ phones[0].name }}</h1>
            <h1 class="text-white/80">
              ฿ {{ phones[0].price.toLocaleString() }}
            </h1>
          </div>
        </div>
        <div
          class="flex flex-col justify-center items-center"
          :class="[
            animationMidImg === 'mid-slide-left'
              ? 'animation-mid-slide-left'
              : '',
            animationMidImg === 'mid-slide-right'
              ? 'animation-mid-slide-right'
              : '',
            animationMidImg === 'mid-slide-up' ? 'animation-slide-up' : '',
            animationMidImg === null ? 'invisible' : '',
          ]"
        >
          <img
            :src="`/kk2/imgs/phones/${phones[1].name}.png`"
            :alt="`${phones[1]}`"
            class="w-96 object-cover hover:scale-105 hover:cursor-pointer duration-500"
          />
          <div
            class="text-center space-y-2"
            :class="[stilAnimation ? 'invisible' : 'animation-slide-up']"
          >
            <h1 class="text-2xl font-bold">{{ phones[1].brand }}</h1>
            <h1 class="text-lg">{{ phones[1].name }}</h1>
            <h1 class="text-white/80">
              ฿ {{ phones[1].price.toLocaleString() }}
            </h1>
          </div>
          <div
            class="space-x-5 mt-4"
            :class="[stilAnimation ? 'invisible' : 'animation-slide-up']"
          >
            <button class="border-b">More Detail</button>
            <button
              class="px-4 py-1 rounded-2xl bg-blue-500 hover:bg-blue-500/80 hover:cursor-pointer"
            >
              Buy Now
            </button>
          </div>
        </div>
        <div
          class="flex flex-col justify-center items-center"
          :class="[
            animationRightImg === 'right-slide-left'
              ? 'animation-right-slide-left'
              : '',
            animationRightImg === 'right-shrink' ? 'animation-shrink' : '',
            animationRightImg === 'right-slide-up' ? 'animation-slide-up' : '',
            animationRightImg === 'right-grow' ? 'animation-grow' : '',
            animationRightImg === null ? 'invisible' : '',
            scale,
          ]"
        >
          <img
            v-if="!stilAnimation"
            @mouseover="isShowDetail = 'right'"
            @mouseleave="isShowDetail = null"
            @click="slideLeft(), toggleImgLeft(), clearAnimation()"
            :src="`/kk2/imgs/phones/${phones[2].name}.png`"
            :alt="`${phones[2]}`"
            class="w-96 object-cover hover:scale-105 hover:cursor-pointer opacity-50 hover:opacity-100 duration-500"
          />
          <img
            v-else
            :src="`/kk2/imgs/phones/${phones[2].name}.png`"
            :alt="`${phones[2]}`"
            class="w-96 object-cover"
          />
          <div
            class="text-center space-y-2"
            :class="[
              isShowDetail !== 'right' || stilAnimation
                ? 'invisible'
                : 'animation-slide-up',
            ]"
          >
            <h1 class="text-2xl font-bold">{{ phones[2].brand }}</h1>
            <h1 class="text-lg">{{ phones[2].name }}</h1>
            <h1 class="text-white/80">
              ฿ {{ phones[2].price.toLocaleString() }}
            </h1>
          </div>
        </div>
      </div>
    </div>

    <h1 class="text-2xl">
      Brands.
      <span class="text-white/70">Recommend for you.</span>
    </h1>
    <div class="nav-brand flex justify-around my-10">
      <RouterLink
        :to="{ name: 'SaleItemsGallery' }"
        v-for="(brand, index) in brands"
        :key="index"
        class="w-32 flex flex-col items-center"
      >
        <img
          :src="`/kk2/imgs/brands/${brand.toLocaleLowerCase()}.png`"
          :alt="`${brand.toLocaleLowerCase()}`"
          class=" object-cover hover:scale-110 duration-300"
        />
      </RouterLink>
    </div>
    <h1 class="text-2xl">
      Our latest products.
      <span class="text-white/70">Come see what's new.</span>
    </h1>
    <div class="product flex my-7 gap-6">
      <div
        class="w-md rounded-2xl text-lg px-10 pt-14 shadow-white bg-[rgba(22,22,23,255)] hover:scale-[101%] hover:cursor-pointer hover:shadow-xs duration-200"
      >
        <p class="mb-3 text-2xl font-semibold">iPhone 16 Pro</p>
        <p class="mb-1">The best of iPhone</p>
        <p class="text-white/70 text-base">From ฿ 39,900</p>
        <img
          src="/src/assets/imgs/homes/iphone-16.png"
          alt="iphone"
          class="w-64 mx-auto mt-6 object-cover"
        />
      </div>
      <div
        class="w-md rounded-2xl text-lg px-10 pt-14 shadow-white bg-[rgba(22,22,23,255)] hover:scale-[101%] hover:cursor-pointer hover:shadow-xs duration-200"
      >
        <p class="mb-3 text-2xl font-semibold">Reno13 F 5G</p>
        <p class="mb-1">Thinner than before</p>
        <p class="text-white/70 text-base">From ฿ 24,999</p>
        <img
          src="/src/assets/imgs/homes/reno.png"
          alt="iphone"
          class="w-full h-96 mx-auto object-cover"
        />
      </div>
      <div
        class="w-md flex flex-col justify-between rounded-2xl shadow-white text-lg px-10 pt-14 bg-[rgba(22,22,23,255)] hover:scale-[101%] hover:cursor-pointer hover:shadow-xs duration-200"
      >
        <div>
          <p class="mb-3 text-2xl font-semibold">Galaxy S24 Ultra</p>
          <p class="mb-1">Galaxy AI is here</p>
          <p class="text-white/70 text-base">From ฿ 32,900</p>
        </div>

        <img
          src="/src/assets/imgs/homes/galaxy-s24.png"
          alt="iphone"
          class="w-full h-[250px] object-cover"
        />
      </div>
    </div>
    <div class="flex justify-between border-y py-10 mb-10 text-sm">
      <div class="w-72 flex flex-col gap-7 items-center text-center">
        <img
          src="/src/assets/imgs/icons/education-icon.png"
          alt="education"
          class="w-14 object-cover"
        />
        <h1 class="text-2xl mb-8">Education</h1>
        <p class="mb-[24px] ">Save on a new iPhone or iPad with Apple education pricing.</p>
        <div class="flex gap-2 hover:gap-3 duration-300">
          <button class="text-blue-500 cursor-pointer">Learn more</button>
          <button class="text-blue-500">></button>
        </div>
      </div>
      <div class="w-72 flex flex-col items-center gap-7 text-center">
        <img
          src="/src/assets/imgs/icons/credit-icon.png"
          alt="education"
          class="w-14 object-cover"
        />
        <h1 class="text-2xl">Financing and Credit Plans</h1>
        <p>
          Enjoy up to 24 months of installment payments and receive cashback
          with participating credit cards.
        </p>
        <div class="flex gap-2 hover:gap-3 duration-300">
          <button class="text-blue-500 cursor-pointer">Learn more</button>
          <button class="text-blue-500">></button>
        </div>
      </div>
      <div class="w-72 flex flex-col gap-7 items-center text-center">
        <img
          src="/src/assets/imgs/icons/shop-icon.png"
          alt="education"
          class="w-14 object-cover"
        />
        <h1 class="text-2xl">Buy online, pick up in store</h1>
        <p class="mb-[24px]">Place your order online and pick it up at a local Store.</p>
        <div class="flex gap-2 hover:gap-3 duration-300">
          <button class="text-blue-500 cursor-pointer">Learn more</button>
          <button class="text-blue-500">></button>
        </div>
      </div>
    </div>
  </div>
  <Footer />
</template>

<style scoped>
.animation-mid-slide-left {
  animation-name: mid-slide-left;
  animation-duration: 1s;
  animation-timing-function: ease-in-out;
  animation-fill-mode: both;
}

@keyframes mid-slide-left {
  from {
    opacity: 100%;
    transform: translateX(0px) scale(100%);
  }
  to {
    opacity: 50%;
    transform: translateX(-424px) translateY(18px) scale(75%);
  }
}
.animation-mid-slide-right {
  animation-name: mid-slide-right;
  animation-duration: 1s;
  animation-timing-function: ease-in-out;
  animation-fill-mode: both;
}

@keyframes mid-slide-right {
  from {
    opacity: 100%;
    transform: translateX(0px) scale(100%);
  }
  to {
    opacity: 50%;
    transform: translateX(424px) translateY(18px) scale(75%);
  }
}

.animation-right-slide-left {
  animation-name: right-slide-left;
  animation-duration: 1s;
  animation-timing-function: ease-in-out;
  animation-fill-mode: both;
}

@keyframes right-slide-left {
  from {
    transform: translateX(0px) scale(75%);
  }
  to {
    transform: translateX(-424px) translateY(-24px) scale(100%);
  }
}

.animation-left-slide-right {
  animation-name: left-slide-right;
  animation-duration: 1s;
  animation-timing-function: ease-in-out;
  animation-fill-mode: both;
}

@keyframes left-slide-right {
  from {
    transform: translateX(0px) scale(75%);
  }
  to {
    transform: translateX(424px) translateY(-24px) scale(100%);
  }
}

.animation-shrink {
  animation-name: shrink;
  animation-duration: 0.5s;
  animation-timing-function: ease-in-out;
  animation-fill-mode: both;
}

@keyframes shrink {
  from {
    transform: scale(75%);
    opacity: 50%;
  }
  to {
    transform: scale(0%);
    opacity: 0;
  }
}
.animation-grow {
  animation-name: grow;
  animation-duration: 0.5s;
  animation-timing-function: ease-in-out;
  animation-fill-mode: both;
}

@keyframes grow {
  from {
    transform: scale(0%);
    opacity: 0;
  }
  to {
    transform: scale(100%);
    opacity: 1;
  }
}

.animation-slide-up {
  animation: slide-up 0.5s ease-in-out;
}
@keyframes slide-up {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
