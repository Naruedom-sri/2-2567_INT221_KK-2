import { useStatusStore } from "@/stores/statusStore";
const loginUser = async (url, newData) => {
  const statusStore = useStatusStore();
  const response = await fetch(`${url}/v2/users/authentications`, {
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

const register = () => {};

export { loginUser, register };
