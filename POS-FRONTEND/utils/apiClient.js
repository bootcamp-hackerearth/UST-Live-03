export const getAuthHeaders = () => {
  const token = localStorage.getItem("token");
  return {
    "Content-Type": "application/json",
    ...(token && { Authorization: `Bearer ${token}` }),
  };
};

export const getApiUrl = (apiPath, endpoint = "") => {
  return `/api/${apiPath}${endpoint}`;
};

export const fetchListData = async (apiPath, page = 0, sizePerPage = 4) => {
  const response = await fetch(getApiUrl(apiPath, "/list"), {
    method: "POST",
    headers: getAuthHeaders(),
    body: JSON.stringify({
      page,
      sizePerPage,
      sortField: "identifier",
      sortDirection: "ASC",
    }),
  });
  return response.json();
};

export const fetchData = async (apiPath, identifier) => {
  const response = await fetch(getApiUrl(apiPath, `/get?identifier=${identifier}`), {
    headers: getAuthHeaders(),
  });
  if (!response.ok) throw new Error("Failed to fetch data");
  return response.json();
};

export const submitData = async (apiPath, data, method = "POST") => {
  const endpoint = method === "POST" ? "/add" : "/update";
  const response = await fetch(getApiUrl(apiPath, endpoint), {
    method: method === "POST" ? "POST" : "PUT",
    headers: getAuthHeaders(),
    body: JSON.stringify(data),
  });
  const text = await response.text();
  const result = text ? JSON.parse(text) : {};
  if (!response.ok || result.success === false) {
    throw new Error(result.message || "Operation failed");
  }
  return result;
};

export const deleteData = async (apiPath, identifier) => {
  const response = await fetch(getApiUrl(apiPath, `/delete?identifier=${identifier}`), {
    method: "DELETE",
    headers: getAuthHeaders(),
  });
  if (!response.ok) throw new Error("Delete request failed");
  return response.json();
};

export const toggleStatus = async (apiPath, identifier) => {
  const response = await fetch(getApiUrl(apiPath, "/toggleStatus"), {
    method: "POST",
    headers: getAuthHeaders(),
    body: JSON.stringify({ identifier }),
  });
  if (!response.ok) throw new Error("Status toggle failed");
  return response.json();
};
