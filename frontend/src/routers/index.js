import AddEditSaleItems from "@/views/AddEditSaleItems.vue";
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
  {
    path: "/sale-items/add",
    name: "AddSaleItems",
    component: AddEditSaleItems,
    props: { isEditing: false },
  },
  {
    path: "/sale-items/:itemId/edit",
    name: "EditSaleItems",
    component: AddEditSaleItems,
    props: { isEditing: true },
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
