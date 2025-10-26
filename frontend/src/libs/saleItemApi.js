import { useStatusStore } from "@/stores/statusStore";

const getAllSaleItem = async (url) => {
  const response = await fetch(`${url}/v1/sale-items`);
  if (!response.ok) {
    let errorMessage = "";
    try {
      const errJson = await response.json();
      errorMessage = errJson?.message || JSON.stringify(errJson);
    } catch {
      errorMessage = await response.text().catch(() => "");
    }
    throw new Error(
      `Can't fetch sale-items with status: ${response.status} and with body: ${errorMessage}`
    );
  }
  const data = await response.json();
  return data;
};

const getAllSaleItemV2 = async (url, params) => {
  const response = await fetch(`${url}/v2/sale-items?${params.toString()}`);
  if (!response.ok) {
    let errorMessage = "";
    try {
      const errJson = await response.json();
      errorMessage = errJson?.message || JSON.stringify(errJson);
    } catch {
      errorMessage = await response.text().catch(() => "");
    }
    throw new Error(
      `Can't fetch sale-items with status: ${response.status} and with body: ${errorMessage}`
    );
  }
  const data = await response.json();
  return data;
};

const getSaleItemById = async (url, id) => {
  const response = await fetch(`${url}/v2/sale-items/${id}`);
  if (!response.ok) {
    let errorMessage = "";
    try {
      const errJson = await response.json();
      errorMessage = errJson?.message || JSON.stringify(errJson);
    } catch {
      errorMessage = await response.text().catch(() => "");
    }
    throw new Error(
      `Can't fetch sale-items with status: ${response.status} and with body: ${errorMessage}`
    );
  }
  const data = await response.json();
  return data;
};

const getSaleItemByIdForEdit = async (url, id) => {
  const response = await fetch(`${url}/v2/sale-items/edit/${id}`);
  if (!response.ok) {
    let errorMessage = "";
    try {
      const errJson = await response.json();
      errorMessage = errJson?.message || JSON.stringify(errJson);
    } catch {
      errorMessage = await response.text().catch(() => "");
    }
    throw new Error(
      `Can't fetch sale-items with status: ${response.status} and with body: ${errorMessage}`
    );
  }
  const data = await response.json();
  return data;
};

const getImageOfSaleItem = async (url, itemId, imgViewOrder) => {
  const response = await fetch(
    `${url}/v2/sale-items/${itemId}/images/${imgViewOrder}`
  );
  if (!response.ok) {
    let errorMessage = "";
    try {
      const errJson = await response.json();
      errorMessage = errJson?.message || JSON.stringify(errJson);
    } catch {
      errorMessage = await response.text().catch(() => "");
    }
    throw new Error(
      ` Can't get image with status:  ${response.status} and with body: ${errorMessage}`
    );
  }
  const blob = await response.blob();
  const urlImg = window.URL.createObjectURL(blob);
  return urlImg;
};

const getFirstImageOfSaleItem = async (url, itemId) => {
  try {
    const data = await getSaleItemById(url, itemId);
    if (
      data &&
      Array.isArray(data.saleItemImages) &&
      data.saleItemImages.length
    ) {
      const firstOrder = data.saleItemImages[0].imageViewOrder ?? 1;
      const imgUrl = await getImageOfSaleItem(url, itemId, firstOrder);
      return imgUrl;
    }
    return null;
  } catch {
    return null;
  }
};

const deleteSaleItemById = async (url, id) => {
  const statusStore = useStatusStore();
  const response = await fetch(`${url}/v2/sale-items/${id}`, {
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
      "sale-items",
      "delete",
      response.status,
      `Can't delete sale-items with id: ${id}.`
    );
    throw new Error(
      `Can't fetch sale-items with status: ${response.status} and with body: ${errorMessage}`
    );
  }
  statusStore.setEntityAndMethodAndStatusAndMessage(
    "sale-items",
    "delete",
    response.status,
    "The sale-items has been deleted."
  );
  return response.status;
};

const createSaleItem = async (url, formData) => {
  const statusStore = useStatusStore();
  const response = await fetch(`${url}/v2/sale-items`, {
    method: "POST",
    body: formData,
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
      "sale-items",
      "add",
      response.status,
      "The sale-items could not be added."
    );
    throw new Error(
      `Can't create sale-items with status :  ${response.status} and body: ${errorMessage}`
    );
  }
  const data = await response.json();
  return data;
};

const updateSaleItem = async (url, id, formData) => {
  const statusStore = useStatusStore();
  const response = await fetch(`${url}/v2/sale-items/${id}`, {
    method: "PUT",
    body: formData,
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
      "sale-items",
      "update",
      response.status,
      "The sale-items could not be updated."
    );
    throw new Error(
      `Can't update sale-items with status :  ${response.status} and body: ${errorMessage}`
    );
  }
  statusStore.setEntityAndMethodAndStatusAndMessage(
    "sale-items",
    "update",
    response.status,
    "The sale-items has been updated."
  );
  const data = await response.json();
  return data;
};

const createSaleItemSeller = async (url, id, accessToken, formData, router) => {
  const statusStore = useStatusStore();
  const response = await fetch(`${url}/v2/sellers/${id}/sale-items`, {
    method: "POST",
    headers: { Authorization: `Bearer ${accessToken}` },
    body: formData,
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
    const newResponse = await fetch(`${url}/v2/sellers/${id}/sale-items`, {
      method: "POST",
      headers: { Authorization: `Bearer ${newAccessToken}` },
      body: formData,
    });
    if (!newResponse.ok) {
      let errorMessage = "";
      try {
        const errJson = await newResponse.json();
        errorMessage = errJson?.message || JSON.stringify(errJson);
      } catch {
        errorMessage = await newResponse.text().catch(() => "");
      }

      statusStore.setEntityAndMethodAndStatusAndMessage(
        "sale-items",
        "post",
        newResponse.status,
        errorMessage || "The sale-items could not be added."
      );
      throw new Error(
        `Can't create sale-items (status: ${newResponse.status}) - ${errorMessage}`
      );
    }
    statusStore.setEntityAndMethodAndStatusAndMessage(
      "sale-items",
      "post",
      response.status,
      "The sale item has been successfully added."
    );
    return newResponse.json();
  } else if (!response.ok) {
    let errorMessage = "";
    try {
      const errJson = await response.json();
      errorMessage = errJson?.message || JSON.stringify(errJson);
    } catch {
      errorMessage = await response.text().catch(() => "");
    }

    statusStore.setEntityAndMethodAndStatusAndMessage(
      "sale-items",
      "post",
      response.status,
      errorMessage || "The sale-items could not be added."
    );
    throw new Error(
      `Can't create sale-items (status: ${response.status}) - ${errorMessage}`
    );
  }
  statusStore.setEntityAndMethodAndStatusAndMessage(
    "sale-items",
    "post",
    response.status,
    "The sale item has been successfully added."
  );
  return response.json();
};

export {
  getAllSaleItem,
  getAllSaleItemV2,
  getImageOfSaleItem,
  getSaleItemByIdForEdit,
  getSaleItemById,
  deleteSaleItemById,
  createSaleItem,
  updateSaleItem,
  createSaleItemSeller,
  getFirstImageOfSaleItem,
};
