<script setup></script>

<template>
  <div
    class="footer-container px-70 bg-[rgba(22,22,23,255)] text-white/40 text-xs"
  >
    <div class="py-5 border-b space-y-5 ">
      <p>
        Prices are inclusive of VAT. Free delivery for all orders. 
      </p>
      <p class="w-3xl">*Total order
        value must be less than 30,000 THB. Admin fee may be charged by
        bank/processing entity in addition to order value. **For approved
        customers only. Subject to approval.</p>
    </div>
    <div class="content-footer flex justify-between gap-30 pt-5">
      <div class="space-y-2.5">
        <h1>Shop</h1>
        <p>Apple</p>
        <p>Samsung</p>
        <p>Vivo</p>
        <p>Oppo</p>
        <p>Huawei</p>
        <p>Sony</p>
      </div>
      <div class="space-y-2.5">
        <h1>Services</h1>
        <p>Find a Store</p>
        <p>Financing</p>
        <p>In-Store Classes</p>
        <p>Request e-Tax Invoice</p>
      </div>
      <div class="space-y-2.5">
        <h1>ITBMS</h1>
        <p>Find a Store</p>
        <p>About us ITBMS</p>
        <p>SIGN UP ITBMS CLUB PLUS</p>
        <p>Terms and Conditions</p>
        <p>Work with Com7</p>
      </div>
      <div class="space-y-2.5">
        <h1>Information</h1>
        <p>FAQ</p>
        <p>Privacy Policy</p>
        <p>Services</p>
      </div>
    </div>
    <div class="icon flex justify-end gap-5 w-full h-6">
      <img
        src="/src/assets/imgs/homes/icons/facebook.png"
        alt="facebook"
        class="object-cover"
      />
      <img
        src="/src/assets/imgs/homes/icons/ig.png"
        alt="ig"
        class="object-cover"
      />
      <img
        src="/src/assets/imgs/homes/icons/line.png"
        alt="line"
        class="object-cover"
      />
      <img
        src="/src/assets/imgs/homes/icons/tiktok.png"
        alt="tiktok"
        class="object-cover"
      />
      <img
        src="/src/assets/imgs/homes/icons/twitter.png"
        alt="twitter"
        class="object-cover"
      />
      <img
        src="/src/assets/imgs/homes/icons/youtube.png"
        alt="youtube"
        class="object-cover"
      />
    </div>
    <p class=" py-3 border-b">
      More ways to shop:
      <span class="text-blue-500 underline">Find an ITBMS</span> or
      <span class="text-blue-500 underline">other retailer</span> near you. Or
      call 1800-01-9209.
    </p>
    <p class=" py-3">Copyright © 2025 Apple Inc. All rights reserved.</p>
  </div>
</template>

<style scoped>
h1 {
  font-weight: 600;
  color: white;
}
.content-footer p:hover {
  color: white;
  cursor: pointer;
}
</style>
