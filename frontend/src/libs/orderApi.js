import { useStatusStore } from "@/stores/statusStore";
import { refreshAccessToken } from "./userApi";
import { useCartStore } from "@/stores/cartStore";

async function placeOrder(url, requestPayload, accessToken, router) {
  const statusStore = useStatusStore();
  const cartStore = useCartStore();
  const res = await fetch(`${url}/v2/orders`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
    },
    body: JSON.stringify(
      Array.isArray(requestPayload) ? requestPayload : [requestPayload]
    ),
  });
  if (res.status === 401) {
    try {
      const data = await refreshAccessToken(url);
      localStorage.setItem("accessToken", data.access_token);
    } catch (error) {
      console.log(error);
      localStorage.clear();
      sessionStorage.clear();
      cartStore.clearCart();
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "user",
        "logout",
        400,
        "Session expired. Please log in again."
      );
      router.push({ name: "Login" });
      return;
    }
    const newAccessToken = localStorage.getItem("accessToken");
    const res = await fetch(`${url}/v2/orders`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${newAccessToken}`,
      },
      body: JSON.stringify(
        Array.isArray(requestPayload) ? requestPayload : [requestPayload]
      ),
    });
    const data = await res.json().catch(() => null);
    return { status: res.status, data };
  }
  const data = await res.json().catch(() => null);
  return { status: res.status, data };
}
  
const getOrderById = async (url, id, accessToken, router) => {
  const statusStore = useStatusStore();
  const cartStore = useCartStore();
  const response = await fetch(`${url}/v2/orders/${id}`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });
  if (res.status === 401) {
    try {
      const data = await refreshAccessToken(url);
      localStorage.setItem("accessToken", data.access_token);
    } catch (error) {
      console.log(error);
      localStorage.clear();
      sessionStorage.clear();
      cartStore.clearCart();
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "user",
        "logout",
        400,
        "Session expired. Please log in again."
      );
      router.push({ name: "Login" });
      return;
    }
    const newAccessToken = localStorage.getItem("accessToken");
    const response = await fetch(`${url}/v2/orders/${id}`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${newAccessToken}`,
      },
    });
    if (!response.ok) {
      let errorMessage = "";
      try {
        const errorData = await response.json();
        errorMessage = errorData.message || JSON.stringify(errorData);
      } catch {
        errorMessage = await response.text();
      }
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "order",
        "get",
        response.status,
        errorMessage
      );
      throw new Error(
        `Can't get order (status: ${response.status}) - ${errorMessage}`
      );
    }
    return response.json();
  } else if (!response.ok) {
    let errorMessage = "";
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || JSON.stringify(errorData);
    } catch {
      errorMessage = await response.text();
    }
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "order",
      "get",
      response.status,
      errorMessage
    );
    throw new Error(
      `Can't get order (status: ${response.status}) - ${errorMessage}`
    );
  } else {
    return response.json();
  }
};

async function markOrderAsViewed(url, id, accessToken) {
  const statusStore = useStatusStore();
  const response = await fetch(`${url}/v2/orders/${id}/viewed`, {
    method: "PUT",
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  if (!response.ok) {
    let errorMessage = "";
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || JSON.stringify(errorData);
    } catch {
      errorMessage = await response.text();
    }
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "order",
      "update",
      response.status,
      errorMessage
    );
    throw new Error(
      `Can't update order (status: ${response.status}) - ${errorMessage}`
    );
  }
  statusStore.setEntityAndMethodAndStatusAndMessage(
    "order",
    "update",
    response.status,
    "The order has been successfully updated."
  );

  return response.json();
}

export { getOrderById, placeOrder, markOrderAsViewed };
