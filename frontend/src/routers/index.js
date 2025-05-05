import LandingPage from "@/views/LandingPage.vue";
import SaleItemsDetail from "@/views/SaleItemsDetail.vue";
import SaleItemsGallery from "@/views/SaleItemsGallery.vue";
import { createRouter, createWebHistory } from "vue-router";
const history = createWebHistory();
const routes = [
  {
    path: "/sale-items",
    name: "SaleItemsGallery",
    component: SaleItemsGallery,
  },
  {
    path: "/",
    name: "LandingPage",
    component: LandingPage,
  },
  {
    path: "/sale-items/:itemId",
    name: "SaleItemsDetail",
    component: SaleItemsDetail,
  },
];

const router = createRouter({
  history,
  routes,
  scrollBehavior(to, from, savedPosition) {
    return { top: 0 };
  },
});

export default router;
