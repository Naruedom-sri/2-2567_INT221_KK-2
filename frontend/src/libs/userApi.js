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
  if (response.status !== 200) {
    statusStore.setStatusAndMethod(
      "user",
      "login",
      response.status,
      "Login filed."
    );
    throw new Error(
      `Can't login with status:  ${response.status} and with body: ${response.json}`
    );
  }
  statusStore.setStatusAndMethod(
    "user",
    "login",
    response.status,
    "Login successfully."
  );
  return response.json();
};
const register = () => {};

export { loginUser, register };
