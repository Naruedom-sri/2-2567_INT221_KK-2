import AboutUs from "@/views/AboutUs.vue";
import AddEditBrand from "@/views/AddEditBrand.vue";
import AddEditSaleItems from "@/views/AddEditSaleItems.vue";
import BrandList from "@/views/BrandList.vue";
import Home from "@/views/Home.vue";
import LandingPage from "@/views/LandingPage.vue";
import NotFoundPage from "@/views/NotFoundPage.vue";
import SaleItemsDetail from "@/views/SaleItemsDetail.vue";
import SaleItemsGallery from "@/views/SaleItemsGallery.vue";
import SaleItemsList from "@/views/SaleItemsList.vue";
import { createRouter, createWebHistory } from "vue-router";
import Register from "@/views/Register.vue";
import VerifyEmail from "@/views/VerifyEmail.vue";
import Login from "@/views/Login.vue";
import Profile from "@/views/Profile.vue";
import EditProfile from "@/views/EditProfile.vue";
import Cart from "@/views/Cart.vue";
import { decodeToken, isAuth } from "@/libs/jwtToken";
import OrderUser from "@/views/OrderUser.vue";
import OrderDetail from "@/views/OrderDetail.vue";
import OrderSeller from "@/views/OrderSeller.vue";
import ChangePassword from "@/views/ChangePassword.vue";
import ForgotPassword from "@/views/ForgotPassword.vue";
import ResetPassword from "@/views/ResetPassword.vue";

const history = createWebHistory("/kk2/");
const routes = [
  {
    path: "/",
    redirect: { name: "Login" },
  },
  {
    path: "/sale-items",
    name: "SaleItemsGallery",
    component: SaleItemsGallery,
  },
  {
    path: "/home",
    name: "Home",
    component: Home,
  },
  {
    path: "/registers",
    name: "Register",
    component: Register,
  },
  {
    path: "/verify-email/:token",
    name: "VerifyEmailWithToken",
    component: VerifyEmail,
  },
  {
    path: "/verify-email",
    name: "VerifyEmail",
    component: VerifyEmail,
  },
  {
    path: "/profile",
    name: "Profile",
    component: Profile,
  },
  {
    path: "/profile/edit",
    name: "EditProfile",
    component: EditProfile,
  },
  {
    path: "/change-password",
    name: "ChangePassword",
    component: ChangePassword,
  },
  {
    path: "/reset-password",
    name: "ResetPassword",
    component: ResetPassword
  },
  {
    path: "/cart",
    name: "Cart",
    component: Cart,
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
    path: "/landing-page",
    name: "LandingPage",
    component: LandingPage,
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
  {
    path: "/about-us",
    name: "AboutUs",
    component: AboutUs,
  },
  {
    path: "/signin",
    name: "Login",
    component: Login,
  },
  {
    path: "/forgot-password",
    name: "ForgotPassword",
    component: ForgotPassword,
  },
  {
    path: "/not-founds",
    name: "NotFoundPage",
    component: NotFoundPage,
  },
  {
    path: "/your-orders",
    name: "OrderUser",
    component: OrderUser,
  },
  {
    path: "/your-orders/:orderId",
    name: "OrderDetail",
    component: OrderDetail,
  },
  {
    path: "/sale-orders",
    name: "OrderSeller",
    component: OrderSeller,
  },
  {
    path: "/sale-orders/:orderId",
    name: "OrderSellerDetail",
    component: OrderDetail,
    props: { isSeller: true },
  },
  {
    path: "/:pathMatch(.*)*",
    redirect: { name: "NotFoundPage" },
  },
];

const router = createRouter({
  history,
  routes,
  scrollBehavior(to, from, savedPosition) {
    return { top: 0 };
  },
  linkExactActiveClass: "opacity-100",
});

router.beforeEach((to, from, next) => {
  isAuth()
    .then((haveAccessToken) => {
      if (!haveAccessToken) {
        if (
          to.name === "Register" ||
          to.name === "VerifyEmail" ||
          to.name === "Home" ||
          to.name === "SaleItemsGallery" ||
          to.name === "SaleItemsDetail" ||
          to.name === "ForgotPassword" ||
          to.name === "NotFoundPage" ||
          to.name === "ResetPassword"
        ) {
          return next();
        }
        if (to.name !== "Login") {
          return next({ name: "Login" });
        } else {
          return next(); // อยู่ Login แล้ว → ไม่ต้อง redirect
        }
      }
      const accessToken = localStorage.getItem("accessToken");
      const decoded = decodeToken(accessToken);
      if (decoded?.role === "BUYER") {
        if (
          to.name === "SaleItemsList" ||
          to.name === "AddBrand" ||
          to.name === "EditBrand" ||
          to.name === "BrandList" ||
          to.name === "EditSaleItems" ||
          to.name === "AddSaleItems" ||
          to.name === "OrderSeller" ||
          to.name === "OrderSellerDetail"
        ) {
          return next({ name: "SaleItemsGallery" });
        }
      }
      next(); // อนุญาต navigation ตามปกติ
    })
    .catch(() => {
      if (to.name !== "Login") {
        return next({ name: "Login" });
      } else {
        return next();
      }
    });
});

export default router;
