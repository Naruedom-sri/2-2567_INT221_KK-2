import LandingPage from "@/views/LandingPage.vue";
import SaleItemsGallery from "@/views/SaleItemsGallery.vue";
import { createRouter, createWebHistory } from "vue-router";
const history = createWebHistory();
const routes = [
  {
    path: "/sale-items",
    name: "SaleItemGallery",
    component: SaleItemsGallery,
  },
  {
    path: "/",
    name: "LandingPage",
    component:LandingPage,
  },
];

const router = createRouter({
  history,
  routes,
});

export default router;
