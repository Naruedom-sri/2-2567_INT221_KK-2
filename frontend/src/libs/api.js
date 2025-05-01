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
  try {
    const response = await fetch(`${url}/${id}`);
    const data = await response.json();
    return data;
  } catch (error) {
    console.log(error);
  }
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
  try {
    const response = await fetch(`${url}`);
    const data = await response.json();
    return data;
  } catch (error) {
    console.log(error);
  }
};

const createData = async (url, newData) => {
  try {
    const response = await fetch(`${url}`, {
      method: "POST",
      headers: {
        "Content-type": "application/json; charset=UTF-8",
      },
      body: JSON.stringify({ ...newData }),
    });
    const data = await response.json();
    return data;
  } catch (error) {
    console.log(error);
  }
};
export {
  updateSomeData,
  updateAllData,
  getDataById,
  deleteData,
  getAllData,
  createData,
};
