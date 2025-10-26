import { useStatusStore } from "@/stores/statusStore";
const getAllBrand = async (url) => {
  const response = await fetch(`${url}/v1/brands`);
  if (!response.ok) {
    let errorMessage = "";
    try {
      const errJson = await response.json();
      errorMessage = errJson?.message || JSON.stringify(errJson);
    } catch {
      errorMessage = await response.text().catch(() => "");
    }
    throw new Error(
      `Can't fetch brand with status: ${response.status} and with body: ${errorMessage}`
    );
  }
  const data = await response.json();
  return data;
};

const getBrandById = async (url, id) => {
  const response = await fetch(`${url}/v1/brands/${id}`);
  if (!response.ok) {
    let errorMessage = "";
    try {
      const errJson = await response.json();
      errorMessage = errJson?.message || JSON.stringify(errJson);
    } catch {
      errorMessage = await response.text().catch(() => "");
    }
    throw new Error(
      `Can't fetch brand with status: ${response.status} and with body: ${errorMessage}`
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
    let errorMessage = "";
    try {
      const errJson = await response.json();
      errorMessage = errJson?.message || JSON.stringify(errJson);
    } catch {
      errorMessage = await response.text().catch(() => "");
    }
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "brand",
      "get",
      response.status,
      `Can't delete brand with id: ${id}.`
    );
    throw new Error(
      `Can't delete brand with status: ${response.status} and with body: ${errorMessage}`
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
    let errorMessage = "";
    try {
      const errJson = await response.json();
      errorMessage = errJson?.message || JSON.stringify(errJson);
    } catch {
      errorMessage = await response.text().catch(() => "");
    }
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "brand",
      "add",
      response.status,
      "The brand could not be added."
    );
    throw new Error(
      `Can't create brand with status :  ${response.status} and body: ${errorMessage}`
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
    let errorMessage = "";
    try {
      const errJson = await response.json();
      errorMessage = errJson?.message || JSON.stringify(errJson);
    } catch {
      errorMessage = await response.text().catch(() => "");
    }
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "brand",
      "update",
      response.status,
      "The brand could not be updated."
    );
    throw new Error(
      `Can't update brand with status :  ${response.status} and body: ${errorMessage}`
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
