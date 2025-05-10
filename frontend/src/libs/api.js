const updateSomeData = async (url, id, newData) => {
  try {
    const response = await fetch(`${url}/${id}`, {
      method: "PATCH",
      headers: {
        "Content-type": "application/json",
      },
      body: JSON.stringify({ ...newData }),
    });
    const data = await response.json();
    return data;
  } catch (error) {
    console.log(error);
  }
};

const updateAllData = async (url, id, newData) => {
  try {
    const response = await fetch(`${url}/${id}`, {
      method: "PUT",
      headers: {
        "Content-type": "application/json",
      },
      body: JSON.stringify({ ...newData }),
    });
    const data = await response.json();
    return data;
  } catch (error) {
    console.log(error);
  }
};

const getDataById = async (url, id) => {
  const response = await fetch(`${url}/${id}`);
  if (!response.ok)
    throw new Error(`Can't fetch data with status : ${response.status}`);
  const data = await response.json();
  return data;
};

const deleteData = async (url, id) => {
  try {
    const response = await fetch(`${url}/${id}`, {
      method: "DELETE",
    });
    const data = await response.json();
    return data;
  } catch (error) {
    console.log(error);
  }
};

const getAllData = async (url) => {
  const response = await fetch(`${url}`);
  if (!response.ok)
    throw new Error(
      `Can't fetch data with status : ${response.status} and ${response.error}`
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
  updateSomeData,
  updateAllData,
  getDataById,
  deleteData,
  getAllData,
  createData,
};
