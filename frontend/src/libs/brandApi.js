import { useStatusStore } from "@/stores/statusStore";
const getAllBrand = async (url) => {
  const response = await fetch(`${url}/v1/brands`);
  if (!response.ok) {
    throw new Error(
      `Can't fetch brand with status: ${response.status} and with body: ${response.json}`
    );
  }
  const data = await response.json();
  return data;
};

const getBrandById = async (url, id) => {
  const response = await fetch(`${url}/v1/brands/${id}`);
  if (!response.ok) {
    throw new Error(
      `Can't fetch brand with status: ${response.status} and with body: ${response.json}`
    );
  }

  const data = await response.json();
  return data;
};

const deleteBrandById = async (url, id) => {
  const statusStore = useStatusStore();
  const response = await fetch(`${url}/v1/brands/${id}`, {
    method: "DELETE",
  });
  if (response.status !== 204) {
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "brand",
      "get",
      response.status,
      `Can't fetch brand with id: ${id}.`
    );
    throw new Error(
      `Can't delete brand with status: ${response.status} and with body: ${response.json}`
    );
  }
  statusStore.setEntityAndMethodAndStatusAndMessage(
    "brand",
    "delete",
    response.status,
    "The brand has been deleted."
  );
  return response.status;
};

const createBrand = async (url, newData) => {
  const statusStore = useSaleItemStatusStore();
  const response = await fetch(`${url}/v1/brands`, {
    method: "POST",
    headers: {
      "Content-type": "application/json; charset=UTF-8",
    },
    body: JSON.stringify({ ...newData }),
  });
  if (response.status !== 201) {
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "brand",
      "add",
      response.status,
      "The brand could not be added."
    );
    throw new Error(
      `Can't create brand with status :  ${response.status} and body: ${response.json}`
    );
  }
  statusStore.setEntityAndMethodAndStatusAndMessage(
    "brand",
    "add",
    response.status,
    "The brand has been added."
  );
  const data = await response.json();
  return data;
};

const updateBrand = async (url, id, newData) => {
  const statusStore = useStatusStore();
  const response = await fetch(`${url}/v1/brands/${id}`, {
    method: "PUT",
    headers: {
      "Content-type": "application/json",
    },
    body: JSON.stringify({ ...newData }),
  });
  if (!response.ok) {
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "brand",
      "update",
      response.status,
      "The brand could not be updated."
    );
    throw new Error(
      `Can't update brand with status :  ${response.status} and body: ${response.json}`
    );
  }
  statusStore.setEntityAndMethodAndStatusAndMessage(
    "brand",
    "update",
    response.status,
    "The brand has been updated."
  );
  const data = await response.json();
  return data;
};

export { getAllBrand, getBrandById, deleteBrandById, createBrand, updateBrand };
