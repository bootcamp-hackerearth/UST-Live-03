import axios from "axios";
import { getToken } from "../utils/auth";
 
const api = axios.create({
  baseURL: "http://3.26.0.50:8080",
  headers: {
    "Content-Type": "application/json",
  },
});
 
api.interceptors.request.use(
  (config) => {
    const token = getToken();
 
    if (
      token &&
      !config.url.includes("/api/authenticate") &&
      !config.url.includes("/api/user/register") &&
      !config.url.includes("/api/register")
    ) {
      config.headers.Authorization = `Bearer ${token}`;
    }
 
    return config;
  },
  (error) => Promise.reject(error)
);
 
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (
      error.response?.status === 401 ||
      error.response?.status === 403
    ) {
      localStorage.removeItem("token");
      globalThis.location.href = "/login";
    }

    return Promise.reject(error);
  }
);
 
const DEFAULT_PAGINATION = {
  page: 0,
  sizePerPage: 1,
  sortField: "identifier",
};
 
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

export const getListItems = async (model, params = {}) => {
  const response = await api.get(
    `/api/${model}/list`,
  );
  return response.data;
};
 
export const addItem = async (model, data) => {
  const response = await api.post(
    `/api/${model}/add`,
    data
  );
 
  return response.data;
};
 
export const getItem = async (model, identifier) => {
  const response = await api.get(
    `/api/${model}/get`,
    {
      params: { identifier },
    }
  );
 
  return response.data;
};
 
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
 
export const loginUser = async (username, password) => {
  const response = await api.post(
    "/api/authenticate",
    { username, password }
  );
 
  return response.data;
};
 
export const registerUser = async (userData) => {
  const response = await api.post(
    "/api/user/register",
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
 
export default api;
