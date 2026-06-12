import axios from "axios";
import { getToken } from "@/utils/auth";
 
const api = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});
 
// ================= TOKEN INTERCEPTOR =================
api.interceptors.request.use(
  (config) => {
    const token = getToken();
 
     if (token && !config.url.includes("/api/authenticate") && !config.url.includes("/api/user/register")
      && !config.url.includes("/api/register")) {
        config.headers.Authorization = `Bearer ${token}`;
      }
 
    return config;
  },
  (error) => Promise.reject(error)
);
 
// ================= GLOBAL ERROR HANDLING =================
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
      globalThis.location.href = "/login";
    }
    return Promise.reject(error);
  }
);
 
// ================= DEFAULT PAGINATION =================
const DEFAULT_PAGINATION = {
  page: 0,
  sizePerPage: 10,
  sortDirection: "ASC",
  sortField: "identifier",
};
 
// ================= LIST =================
export const listItems = async (model, params = {}) => {
  const response = await api.post(
    `/api/${model}/list`,
    {
      ...DEFAULT_PAGINATION,
      ...params,
    }
  );

  return response.data;
};

// ================= GET ALL =================
export const getAllItems = async (model) => {
  const response = await api.get(
    `/api/${model}/all`
  );

  return response.data;
};
 
// ================= ADD =================
export const addItem = async (model, data) => {
  const response = await api.post(
    `/api/${model}/add`,
    data
  );
 
  return response.data;
};
 
// ================= GET =================
export const getItem = async (model, identifier) => {
  const response = await api.get(
    `/api/${model}/get`,
    {
      params: { identifier },
    }
  );
 
  return response.data;
};
 
// ================= UPDATE =================
export const updateItem = async (model, data) => {
  const response = await api.post(
    `/api/${model}/update`,
    data
  );
 
  return response.data;
};
 
export const deleteItem = async (model, value, key = "identifier") => {
  const response = await api.get(`/api/${model}/delete`, {
    params: {
      [key]: value,
    },
  });
 
  return response.data;
};
 
// ================= TOGGLE STATUS =================
export const toggleItem = async (model, identifier) => {
  const response = await api.post(
    `/api/${model}/toggleStatus`,
    null,
    {
      params: { identifier },
    }
  );
 
  return response.data;
};

// ================= GET SUBCATEGORIES =================
export const getSubCategories = async () => {
  const response = await api.get(
    "/api/category/subcategories"
  );

  return response.data;
};

// ================= AUTH =================
export const loginUser = async (username, password) => {
  const response = await api.post(
    "/api/authenticate",
    { username, password }
  );
 
  return response.data;
};
 
export const registerUser = async (userData) => {
  const response = await api.post(
    "/register",
    userData
  );
 
  return response.data;
};
 
export const getCurrentUser = async () => {
  const response = await api.get("/api/user/me");
  return response.data;
};
 
export const updateUser = async (userData) => {
  const response = await api.post(
    "/api/user/update",
    userData
  );
 
  return response.data;
};
 
export const fetchRoles = async () => {
  const response = await api.get("/api/role/all");
  return response.data;
};

export const getAuthorizedNodes = async () => {
  const response = await api.get(
    "/api/node/menu"
  );

  return response.data;
};

export default api;