import { useSaleItemStatusStore } from "@/stores/SaleItemStatus";
const loginUser = async (url, newData) => {
  const statusStore = useSaleItemStatusStore();
  const response = await fetch(`${url}/v2/users/authentications`, {
    method: "POST",
    headers: {
      "Content-type": "application/json; charset=UTF-8",
    },
    body: JSON.stringify({ ...newData }),
  });
  if (response.status !== 200) {
    statusStore.setStatusAndMethod("login", response.status);
    throw new Error(`Can't create data with status :  ${response.status}`);
  }
  statusStore.setStatusAndMethod("login", response.status);
  return response.json();
};

export { loginUser };
