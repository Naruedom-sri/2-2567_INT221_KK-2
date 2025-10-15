import { jwtDecode } from "jwt-decode";
import { refreshAccessToken } from "./userApi";
import { useCartStore } from "@/stores/cartStore";
const BASE_API_DOMAIN = import.meta.env.VITE_APP_URL;

function isAccessTokenExpired(token) {
  try {
    const decoded = jwtDecode(token);
    const now = Date.now() / 1000; // current time in seconds
    return decoded.exp < now; // true = expired
  } catch (err) {
    return true; // invalid token
  }
}
const isAuth = async () => {
  const accessToken = localStorage.getItem("accessToken");
  const cartStore = useCartStore();
  if (!accessToken || isAccessTokenExpired(accessToken)) {
    try {
      const data = await refreshAccessToken(`${BASE_API_DOMAIN}`);
      localStorage.setItem("accessToken", data.access_token);
      return true;
    } catch (error) {
      localStorage.clear();
      sessionStorage.clear();
      cartStore.clearCart();
      console.log(error);
      return false;
    }
  }
  return true;
};

const decodeToken = (token) => {
  try {
    return jwtDecode(token);
  } catch (error) {
    console.log(error);
    return null;
  }
};

export { isAuth, decodeToken };
