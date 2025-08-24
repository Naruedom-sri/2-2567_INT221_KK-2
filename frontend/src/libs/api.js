import { useSaleItemStatusStore } from "@/stores/SaleItemStatus";
const updateData = async (url, id, newData) => {
  const statusStore = useSaleItemStatusStore();
  const response = await fetch(`${url}/${id}`, {
    method: "PUT",
    headers: {
      "Content-type": "application/json",
    },
    body: JSON.stringify({ ...newData }),
  });
  if (!response.ok) {
    statusStore.setStatusAndMethod("update", response.status);
    throw new Error(`Can't update data with status : ${response.status}`);
  }
  const data = await response.json();
  return data;
};

const getDataById = async (url, id) => {
  const response = await fetch(`${url}/${id}`);
  if (!response.ok)
    throw new Error(`Can't fetch data with status : ${response.status}`);
  const data = await response.json();
  return data;
};

const deleteData = async (url, id) => {
  const response = await fetch(`${url}/${id}`, {
    method: "DELETE",
  });

  if (response.status !== 204) {
    return response.status;
  } else {
    return response.status;
  }
};

const getAllData = async (url) => {
  const response = await fetch(`${url}`);
  if (!response.ok)
    throw new Error(`Can't fetch data with status : ${response.status}`);
  const data = await response.json();
  return data;
};

const getAllDataWithParam = async (url, params) => {
  const response = await fetch(`${url}?${params.toString()}`);
  if (!response.ok)
    throw new Error(`Can't fetch data with status : ${response.status}`);
  const data = await response.json();
  return data;
};

const createData = async (url, newData) => {
  const statusStore = useSaleItemStatusStore();
  const response = await fetch(`${url}`, {
    method: "POST",
    headers: {
      "Content-type": "application/json; charset=UTF-8",
    },
    body: JSON.stringify({ ...newData }),
  });
  if (response.status !== 201) {
    statusStore.setStatusAndMethod("add", response.status);
    throw new Error(`Can't create data with status :  ${response.status}`);
  }

  const data = await response.json();
  return data;
};

const createDataWithFile = async (url, formData) => {
  const statusStore = useSaleItemStatusStore();
  const response = await fetch(`${url}`, {
    method: "POST",
    body: formData,
  });
  if (response.status !== 201) {
    statusStore.setStatusAndMethod("add", response.status);
    throw new Error(`Can't create data with status :  ${response.status}`);
  }
  const data = await response.json();
  return data;
};

const getImageOfData = async (url, itemId, imgViewOrder) => {
  const response = await fetch(`${url}/${itemId}/images/${imgViewOrder}`);
  if (!response.ok) {
    return null;
  }
  const blob = await response.blob();
  const urlImg = window.URL.createObjectURL(blob);
  return urlImg;
};

const updateDataWithFile = async (url, id, formData) => {
  const statusStore = useSaleItemStatusStore();
  const response = await fetch(`${url}/${id}`, {
    method: "PUT",
    body: formData,
  });
  if (!response.ok) {
    statusStore.setStatusAndMethod("update", response.status);
    throw new Error(`Can't update data with status : ${response.status}`);
  }
  const data = await response.json();
  return data;
};

export {
  updateData,
  getDataById,
  deleteData,
  getAllData,
  createData,
  createDataWithFile,
  getAllDataWithParam,
  getImageOfData,
  updateDataWithFile
};
