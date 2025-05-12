const updateData = async (url, id, newData) => {
  const response = await fetch(`${url}/${id}`, {
    method: "PUT",
    headers: {
      "Content-type": "application/json",
    },
    body: JSON.stringify({ ...newData }),
  });
  if (!response.ok)
    throw new Error(
      `Can't update data with status : ${response.status}`
    );
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
    throw new Error(
      `Can't delete data with status : ${response.status}`
    );
  } else {
    return response.status;
  }
};

const getAllData = async (url) => {
  const response = await fetch(`${url}`);
  if (!response.ok)
    throw new Error(
      `Can't fetch data with status : ${response.status}`
    );
  const data = await response.json();
  return data;
};

const createData = async (url, newData) => {
  const response = await fetch(`${url}`, {
    method: "POST",
    headers: {
      "Content-type": "application/json; charset=UTF-8",
    },
    body: JSON.stringify({ ...newData }),
  });
  if (response.status !== 201)
    throw new Error(`Can't create data with status :  ${response.status}`);
  const data = await response.json();
  return data;
};
export {
  updateData,
  getDataById,
  deleteData,
  getAllData,
  createData,
};
