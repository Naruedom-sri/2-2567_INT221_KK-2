import AddEditBrand from "@/views/AddEditBrand.vue";
import AddEditSaleItems from "@/views/AddEditSaleItems.vue";
import BrandList from "@/views/BrandList.vue";
import Home from "@/views/Home.vue";
import LandingPage from "@/views/LandingPage.vue";
import SaleItemsDetail from "@/views/SaleItemsDetail.vue";
import SaleItemsGallery from "@/views/SaleItemsGallery.vue";
import SaleItemsList from "@/views/SaleItemsList.vue";
import { createRouter, createWebHistory } from "vue-router";
const history = createWebHistory('/kk2/');
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
    path: "/home",
    name: "Home",
    component: Home,
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
  {
    path: "/sale-items/list",
    name: "SaleItemsList",
    component: SaleItemsList,
  },
  {
    path: "/brands",
    name: "BrandList",
    component: BrandList,
  },
  {
    path: "/brands/add",
    name: "AddBrand",
    component: AddEditBrand,
    props: { isEditing: false },
  },
  {
    path: "/brands/:brandId/edit",
    name: "EditBrand",
    component: AddEditBrand,
    props: { isEditing: true },
  },
];

const router = createRouter({
  history,
  routes,
  scrollBehavior(to, from, savedPosition) {
    return { top: 0 };
  },
  linkExactActiveClass: "border-b-blue-500",
});

export default router;
