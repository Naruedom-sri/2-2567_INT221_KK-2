import { useStatusStore } from "@/stores/statusStore";
const getOrderById = async (url, id, accessToken) => {
  const statusStore = useStatusStore();
  const response = await fetch(`${url}/v2/orders/${id}`, {
    method: "GET",
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
export { getOrderById };
