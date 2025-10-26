import { useCartStore } from "@/stores/cartStore";
import { useStatusStore } from "@/stores/statusStore";
const verifyEmail = async (url, token) => {
  const statusStore = useStatusStore();
  const res = await fetch(
    `${url}/v2/users/verify-email?token=${encodeURIComponent(token)}`,
    {
      method: "POST",
    }
  );

  if (!res.ok) {
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "user",
      "verify",
      res.status,
      "The account activate failed."
    );
    throw new Error(
      `Request failed with status ${res.status} and with body`,
      res.json
    );
  }
  statusStore.setEntityAndMethodAndStatusAndMessage(
    "user",
    "verify",
    res.status,
    "The account has been successfully activated."
  );
};

const loginUser = async (url, newData) => {
  const statusStore = useStatusStore();
  const response = await fetch(`${url}/v2/auth/login`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-type": "application/json; charset=UTF-8",
    },
    body: JSON.stringify({ ...newData }),
  });

  if (!response.ok) {
    let errorMessage = "";
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || JSON.stringify(errorData);
    } catch {
      errorMessage = await response.text(); // fallback ถ้าไม่ใช่ JSON
    }
    if (response.status === 401 || response.status === 403) {
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "user",
        "login",
        response.status,
        errorMessage
      );
      throw new Error(
        `Can't login (status: ${response.status}) - ${errorMessage}`
      );
    } else {
      // 400 bad request invalid email format
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "user",
        "login",
        response.status,
        errorMessage.email
      );
      throw new Error(
        `Can't login (status: ${response.status}) - ${errorMessage.email}`
      );
    }
  }
  statusStore.setEntityAndMethodAndStatusAndMessage(
    "user",
    "login",
    response.status,
    "Login successfully."
  );
  return response.json();
};

const logoutUser = async (url, accessToken, router) => {
  const statusStore = useStatusStore();
  const cartStore = useCartStore();
  const response = await fetch(`${url}/v2/auth/logout`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-type": "application/json; charset=UTF-8",
      Authorization: `Bearer ${accessToken}`,
    },
  });
  if (response.status === 401) {
    try {
      const data = await refreshAccessToken(url);
      localStorage.setItem("accessToken", data.access_token);
    } catch (error) {
      console.log(error);
      localStorage.removeItem("accessToken");
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
    const newResponse = await fetch(`${url}/v2/auth/logout`, {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-type": "application/json; charset=UTF-8",
        Authorization: `Bearer ${newAccessToken}`,
      },
    });
    if (newResponse.status !== 204) {
      let errorMessage = "";
      try {
        const errorData = await newResponse.json();
        errorMessage = errorData.message || JSON.stringify(errorData);
      } catch {
        errorMessage = await newResponse.text(); // fallback ถ้าไม่ใช่ JSON
      }
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "user",
        "logout",
        newResponse.status,
        errorMessage
      );
      throw new Error(
        `Can't logout (status: ${newResponse.status}) - ${errorMessage}`
      );
    }
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "user",
      "logout",
      newResponse.status,
      "Logout successfully."
    );
  } else if (response.status !== 204) {
    let errorMessage = "";
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || JSON.stringify(errorData);
    } catch {
      errorMessage = await response.text(); // fallback ถ้าไม่ใช่ JSON
    }
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "user",
      "logout",
      response.status,
      errorMessage
    );
    throw new Error(
      `Can't logout (status: ${response.status}) - ${errorMessage}`
    );
  }
  statusStore.setEntityAndMethodAndStatusAndMessage(
    "user",
    "logout",
    response.status,
    "Logout successfully."
  );
};

const register = async (url, form) => {
  const statusStore = useStatusStore();
  const response = await fetch(`${url}/v2/auth/register`, {
    method: "POST",
    body: form,
  });
  if (response.status !== 201) {
    let errorMessage = "";
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || JSON.stringify(errorData);
    } catch {
      errorMessage = await response.text(); // fallback ถ้าไม่ใช่ JSON
    }
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "user",
      "register",
      response.status,
      errorMessage
    );
    throw new Error(
      `Can't sign-up (status: ${response.status}) - ${errorMessage}`
    );
  }
  statusStore.setEntityAndMethodAndStatusAndMessage(
    "user",
    "register",
    response.status,
    "The user account has been successfully registered."
  );
  return response.json();
};

const getUserById = async (url, id, accessToken, router) => {
  const statusStore = useStatusStore();
  const cartStore = useCartStore();
  const response = await fetch(`${url}/v2/users/${id}`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });
  if (response.status === 401) {
    try {
      const data = await refreshAccessToken(url);
      localStorage.setItem("accessToken", data.access_token);
    } catch (error) {
      console.log(error);
      localStorage.removeItem("accessToken");
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
    const newResponse = await fetch(`${url}/v2/users/${id}`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${newAccessToken}`,
      },
    });
    if (!newResponse.ok) {
      let errorMessage = "";
      try {
        const errorData = await newResponse.json();
        errorMessage = errorData.message || JSON.stringify(errorData);
      } catch {
        errorMessage = await newResponse.text();
      }
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "user",
        "get",
        newResponse.status,
        errorMessage
      );
      throw new Error(
        `Can't get user (status: ${newResponse.status}) - ${errorMessage}`
      );
    }
    return newResponse.json();
  } else if (!response.ok) {
    let errorMessage = "";
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || JSON.stringify(errorData);
    } catch {
      errorMessage = await response.text();
    }
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "user",
      "get",
      response.status,
      errorMessage
    );
    throw new Error(
      `Can't get user (status: ${response.status}) - ${errorMessage}`
    );
  }
  return response.json();
};

const editProfile = async (url, id, accessToken, form, router) => {
  const statusStore = useStatusStore();
  const cartStore = useCartStore();
  const response = await fetch(`${url}/v2/users/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify(form),
  });
  if (response.status === 401) {
    try {
      const data = await refreshAccessToken(url);
      localStorage.setItem("accessToken", data.access_token);
    } catch (error) {
      console.log(error);
      localStorage.removeItem("accessToken");
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
    const newResponse = await fetch(`${url}/v2/users/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${newAccessToken}`,
      },
      body: JSON.stringify(form),
    });
    if (!response.ok) {
      let errorMessage = "";
      try {
        const errorData = await newResponse.json();
        errorMessage = errorData.message || JSON.stringify(errorData);
      } catch {
        errorMessage = await newResponse.text(); // fallback ถ้าไม่ใช่ JSON
      }
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "user",
        "edit",
        newResponse.status,
        errorMessage
      );
      throw new Error(
        `Can't edit user (status: ${newResponse.status}) - ${errorMessage}`
      );
    }
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "profile",
      "edit",
      newResponse.status,
      "Profile data is updated successfully."
    );
    return newResponse.json();
  } else if (!response.ok) {
    let errorMessage = "";
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || JSON.stringify(errorData);
    } catch {
      errorMessage = await response.text();
    }
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "user",
      "edit",
      response.status,
      errorMessage
    );
    throw new Error(
      `Can't edit user (status: ${response.status}) - ${errorMessage}`
    );
  }

  statusStore.setEntityAndMethodAndStatusAndMessage(
    "profile",
    "edit",
    response.status,
    "Profile data is updated successfully."
  );
  return response.json();
};

const editPassword = async (url, id, accessToken, payload, router) => {
  const statusStore = useStatusStore();
  const cartStore = useCartStore();
  const response = await fetch(`${url}/v2/users/${id}/change-password`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify(payload),
  });
  if (response.status === 401) {
    try {
      const data = await refreshAccessToken(url);
      localStorage.setItem("accessToken", data.access_token);
    } catch (error) {
      console.log(error);
      localStorage.removeItem("accessToken");
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
    const newResponse = await fetch(`${url}/v2/users/${id}/change-password`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${newAccessToken}`,
      },
      body: JSON.stringify(payload),
    });
    if (!newResponse.ok) {
      let errorMessage = "";
      try {
        const errorData = await newResponse.json();
        errorMessage = errorData.message || JSON.stringify(errorData);
      } catch {
        errorMessage = await newResponse.text();
      }
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "password",
        "edit",
        response.status,
        data.message || "Password updated successfully."
      );
      return newResponse;
    }
  } else if (!response.ok) {
    let errorMessage = "";
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || JSON.stringify(errorData);
    } catch {
      errorMessage = await response.text();
    }

    statusStore.setEntityAndMethodAndStatusAndMessage(
      "password",
      "edit",
      response.status,
      errorMessage
    );

    throw new Error(
      `Can't edit password (status: ${response.status}) - ${errorMessage}`
    );
  }

  statusStore.setEntityAndMethodAndStatusAndMessage(
    "password",
    "edit",
    response.status,
    "Password updated successfully."
  );

  return response;
};

const sendEmailForgotPassword = async (url, email) => {
  const statusStore = useStatusStore();

  const response = await fetch(
    `${url}/v2/auth/forgot-password?email=${encodeURIComponent(email)}`,
    {
      method: "POST",
    }
  );

  if (!response.ok) {
    let errorMessage = "";
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || JSON.stringify(errorData);
    } catch {
      errorMessage = await response.text();
    }

    statusStore.setEntityAndMethodAndStatusAndMessage(
      "password",
      "reset",
      response.status,
      errorMessage
    );

    throw new Error(
      `Can't reset password (status: ${response.status}) - ${errorMessage}`
    );
  }
  return await response.text();
};

const resetPassword = async (url, password, token) => {
  const statusStore = useStatusStore();

  if (!token) {
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "password",
      "edit",
      400,
      "Missing password reset token."
    );
    throw new Error("Missing password reset token.");
  }

  const params = new URLSearchParams({
    password: password,
    token: token,
  });

  const response = await fetch(
    `${url}/v2/auth/change-password?${params.toString()}`,
    {
      method: "PUT",
    }
  );

  if (!response.ok) {
    let errorMessage = "";
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || JSON.stringify(errorData);
    } catch {
      errorMessage = await response.text();
    }

    statusStore.setEntityAndMethodAndStatusAndMessage(
      "password",
      "edit",
      response.status,
      errorMessage
    );

    throw new Error(
      `Can't reset password (status: ${response.status}) - ${errorMessage}`
    );
  }

  statusStore.setEntityAndMethodAndStatusAndMessage(
    "password",
    "edit",
    response.status,
    "Password updated successfully."
  );

  return response;
};

const refreshAccessToken = async (url) => {
  const response = await fetch(`${url}/v2/auth/refresh`, {
    method: "POST",
    credentials: "include", // ส่ง cookie refresh token ไป
  });
  if (!response.ok) {
    let errorMessage = "";
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || JSON.stringify(errorData);
    } catch {
      errorMessage = await response.text(); // fallback ถ้าไม่ใช่ JSON
    }
    throw new Error(
      `Can't create new access token (status: ${response.status}) - ${errorMessage}`
    );
  }
  return response.json();
};

const getAllSaleItemOfSeller = async (url, id, accessToken, params, router) => {
  const statusStore = useStatusStore();
  const cartStore = useCartStore();
  const response = await fetch(
    `${url}/v2/sellers/${id}/sale-items?${params.toString()}`,
    {
      method: "GET",
      headers: {
        "Content-type": "application/json; charset=UTF-8",
        Authorization: `Bearer ${accessToken}`,
      },
    }
  );
  if (response.status === 401) {
    try {
      const data = await refreshAccessToken(url);
      localStorage.setItem("accessToken", data.access_token);
    } catch (error) {
      console.log(error);
      localStorage.removeItem("accessToken");
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
    const newResponse = await fetch(
      `${url}/v2/sellers/${id}/sale-items?${params.toString()}`,
      {
        method: "GET",
        headers: {
          "Content-type": "application/json; charset=UTF-8",
          Authorization: `Bearer ${newAccessToken}`,
        },
      }
    );
    if (!newResponse.ok) {
      let errorMessage = "";
      try {
        const errorData = await newResponse.json();
        errorMessage = errorData.message || JSON.stringify(errorData);
      } catch {
        errorMessage = await response.text(); // fallback ถ้าไม่ใช่ JSON
      }
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "user",
        "get",
        newResponse.status,
        errorMessage
      );
      throw new Error(
        `Can't fetch (status: ${newResponse.status}) - ${errorMessage}`
      );
    }
    return newResponse.json();
  } else if (!response.ok) {
    let errorMessage = "";
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || JSON.stringify(errorData);
    } catch {
      errorMessage = await response.text(); // fallback ถ้าไม่ใช่ JSON
    }
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "user",
      "get",
      response.status,
      errorMessage
    );
    throw new Error(
      `Can't fetch (status: ${response.status}) - ${errorMessage}`
    );
  }
  return response.json();
};

const getAllOrder = async (url, id, accessToken, params, router) => {
  const statusStore = useStatusStore();
  const cartStore = useCartStore();
  const response = await fetch(
    `${url}/v2/users/${id}/orders?${params.toString()}`,
    {
      method: "GET",
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    }
  );
  if (response.status === 401) {
    try {
      const data = await refreshAccessToken(url);
      localStorage.setItem("accessToken", data.access_token);
    } catch (error) {
      console.log(error);
      localStorage.removeItem("accessToken");
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
    const newResponse = await fetch(
      `${url}/v2/users/${id}/orders?${params.toString()}`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${newAccessToken}`,
        },
      }
    );
    if (!newResponse.ok) {
      let errorMessage = "";
      try {
        const errorData = await response.json();
        errorMessage = errorData.message || JSON.stringify(errorData);
      } catch {
        errorMessage = await response.text();
      }
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "user",
        "get",
        newResponse.status,
        errorMessage
      );
      throw new Error(
        `Can't get order (status: ${newResponse.status}) - ${errorMessage}`
      );
    }
    return newResponse.json();
  } else if (!response.ok) {
    let errorMessage = "";
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || JSON.stringify(errorData);
    } catch {
      errorMessage = await response.text();
    }
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "user",
      "get",
      response.status,
      errorMessage
    );
    throw new Error(
      `Can't get order (status: ${response.status}) - ${errorMessage}`
    );
  }
  return response.json();
};

const getAllSellerOrder = async (url, id, accessToken, params, router) => {
  const statusStore = useStatusStore();
  const cartStore = useCartStore();
  const response = await fetch(
    `${url}/v2/sellers/${id}/orders?${params.toString()}`,
    {
      method: "GET",
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    }
  );
  if (response.status === 401) {
    try {
      const data = await refreshAccessToken(url);
      localStorage.setItem("accessToken", data.access_token);
    } catch (error) {
      console.log(error);
      localStorage.removeItem("accessToken");
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
    const newResponse = await fetch(
      `${url}/v2/sellers/${id}/orders?${params.toString()}`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${newAccessToken}`,
        },
      }
    );
    if (!newResponse.ok) {
      let errorMessage = "";
      try {
        const errorData = await newResponse.json();
        errorMessage = errorData.message || JSON.stringify(errorData);
      } catch {
        errorMessage = await response.text();
      }
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "user",
        "get",
        newResponse.status,
        errorMessage
      );
      throw new Error(
        `Can't get order (status: ${newResponse.status}) - ${errorMessage}`
      );
    }
    return newResponse.json();
  } else if (!response.ok) {
    let errorMessage = "";
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || JSON.stringify(errorData);
    } catch {
      errorMessage = await response.text();
    }
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "user",
      "get",
      response.status,
      errorMessage
    );
    throw new Error(
      `Can't get order (status: ${response.status}) - ${errorMessage}`
    );
  }
  return response.json();
};

const getSellerOrderById = async (url, id, accessToken) => {
  const statusStore = useStatusStore();
  const cartStore = useCartStore();
  const response = await fetch(`${url}/v2/sellers/orders/${id}`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });
  if (response.status === 401) {
    try {
      const data = await refreshAccessToken(url);
      localStorage.setItem("accessToken", data.access_token);
    } catch (error) {
      console.log(error);
      localStorage.removeItem("accessToken");
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
    const newResponse = await fetch(`${url}/v2/sellers/orders/${id}`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${newAccessToken}`,
      },
    });
    if (!newResponse.ok) {
      let errorMessage = "";
      try {
        const errorData = await newResponse.json();
        errorMessage = errorData.message || JSON.stringify(errorData);
      } catch {
        errorMessage = await newResponse.text();
      }
      statusStore.setEntityAndMethodAndStatusAndMessage(
        "order",
        "get",
        newResponse.status,
        errorMessage
      );
      throw new Error(
        `Can't get order (status: ${newResponse.status}) - ${errorMessage}`
      );
    }
    return newResponse.json();
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
  }
  return response.json();
};

export {
  loginUser,
  register,
  getAllSaleItemOfSeller,
  refreshAccessToken,
  getUserById,
  logoutUser,
  editProfile,
  editPassword,
  sendEmailForgotPassword,
  resetPassword,
  verifyEmail,
  getAllOrder,
  getAllSellerOrder,
  getSellerOrderById,
};
