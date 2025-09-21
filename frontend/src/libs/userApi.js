import { useStatusStore } from "@/stores/statusStore";
const statusStore = useStatusStore();
const loginUser = async (url, newData) => {
  const response = await fetch(`${url}/v2/auth/login`, {
    method: "POST",
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

const register = async (url, form) => {
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

export { loginUser, register };
