import { useStatusStore } from "@/stores/statusStore";
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
    if (response.status === 401) {
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

const logoutUser = async (url, accessToken) => {
  const statusStore = useStatusStore();
  const response = await fetch(`${url}/v2/auth/logout`, {
    method: "POST",
    headers: {
      "Content-type": "application/json; charset=UTF-8",
      Authorization: `Bearer ${accessToken}`,
    },
  });

  if (response.status !== 204) {
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
  const res = await fetch(`${url}/v2/auth/register`, {
    method: "POST",
    body: form,
  });
  if (res.status !== 201) {
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
      res.status,
      errorMessage
    );
    throw new Error(
      `Can't sign-up (status: ${response.status}) - ${errorMessage}`
    );
  }
  statusStore.setEntityAndMethodAndStatusAndMessage(
    "user",
    "register",
    res.status,
    "The user account has been successfully registered."
  );
  return res.json();
};

const getUserById = async (url, id, token = null) => {
  const statusStore = useStatusStore();
  const headers = {};
  if (token) headers["Authorization"] = `Bearer ${token}`;

  const response = await fetch(`${url}/v2/users/${id}`, {
    method: "GET",
    headers,
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
      "user",
      "get",
      response.status,
      errorMessage
    );
    throw new Error(`Can't get user (status: ${response.status}) - ${errorMessage}`);
  }

  statusStore.setEntityAndMethodAndStatusAndMessage(
    "user",
    "get",
    response.status,
    "Get user successfully."
  );
  return response.json();
};

const refreshAccessToken = async (url) => {
  const statusStore = useStatusStore();
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
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "user",
      "refresh",
      response.status,
      errorMessage
    );
    throw new Error(
      `Can't create new access token (status: ${response.status}) - ${errorMessage}`
    );
  }
  return response.json();
};

const getAllSaleItemOfSeller = async (url, id, accessToken, params) => {
  const statusStore = useStatusStore();
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

  if (!response.ok) {
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

export { loginUser, register, getAllSaleItemOfSeller, refreshAccessToken, getUserById, logoutUser };
