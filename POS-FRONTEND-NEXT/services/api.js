import axios from "axios";
import { getToken } from "../utils/auth";

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
    const url = config.url || "";

    // Do NOT attach token for login/register APIs
    if (
      token &&
      !url.includes("/api/authenticate") &&
      !url.includes("/api/user/register") &&
      !url.includes("/api/register")
    ) {
      config.headers = config.headers || {};
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error),
);

// ================= GLOBAL ERROR HANDLING =================
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status;

    if (typeof globalThis !== "undefined") {
      switch (status) {
        case 401:
          localStorage.removeItem("token");
          globalThis.location.replace("/login");
          break;

        case 404:
          globalThis.location.replace("/not-found");
          break;

        case 500:
          globalThis.location.replace("/server-error");
          break;

        default:
          break;
      }
    }

    return Promise.reject(error);
  }
);
// ================= DEFAULT PAGINATION =================
const DEFAULT_PAGINATION = {
  page: 0,
  sizePerPage: 10,
  sortField: "identifier",
  search: "",
};

// ================= LIST =================
export const listItems = async (model, params = {}) => {
  const response = await api.post(`/api/${model}/list`, {
    ...DEFAULT_PAGINATION,
    ...params,
  });

  return response.data;
};

// ================= GET LIST =================
export const getListItems = async (model) => {
  const response = await api.get(`/api/${model}/list`);
  return response.data;
};

// ================= ADD =================
export const addItem = async (model, data) => {
  const response = await api.post(`/api/${model}/add`, data);
  return response.data;
};

// ================= GET =================
export const getItem = async (model, identifier) => {
  const response = await api.get(`/api/${model}/get`, {
    params: { identifier },
  });

  return response.data;
};

// ================= UPDATE =================
export const updateItem = async (model, data) => {
  const response = await api.put(`/api/${model}/update`, data);
  return response.data;
};

// ================= DELETE =================
export const deleteItem = async (model, value, key = "identifier") => {
  const response = await api.delete(`/api/${model}/delete`, {
    params: {
      [key]: value,
    },
  });

  return response.data;
};

// ================= TOGGLE STATUS =================
export const toggleItem = async (model, identifier) => {
  const response = await api.post(`/api/${model}/toggleStatus`, null, {
    params: { identifier },
  });

  return response.data;
};

// ================= AUTH =================
export const loginUser = async (username, password) => {
  const response = await api.post("/api/authenticate", {
    username,
    password,
  });

  return response.data;
};

export const registerUser = async (userData) => {
  const response = await api.post("/api/user/register", userData);

  return response.data;
};

export const getCurrentUser = async () => {
  const response = await api.get("/api/user/me");
  return response.data;
};

export const updateUser = async (userData) => {
  const response = await api.put("/api/user/update", userData);

  return response.data;
};

export const fetchRoles = async () => {
  const response = await api.get("/api/role/all");
  return response.data;
};

// ================= CART =================

export const createCart = async (data) => {
  const response = await api.post("/api/cart/add", data);
  return response.data;
};

export const getCart = async (identifier) => {
  const response = await api.get("/api/cart/get", {
    params: { identifier },
  });

  return response.data;
};

// ================= CART ENTRY =================

export const addCartEntry = async (data) => {
  const response = await api.post("/api/cartentry/add", data);

  return response.data;
};

export const getCartEntries = async (cartId) => {
  const response = await api.post("/api/cartentry/getByCartId", {
    cartId,
  });

  return response.data;
};

export const deleteCartEntry = async (identifier) => {
  const response = await api.delete("/api/cartentry/delete", {
    params: {
      identifier,
    },
  });

  return response.data;
};

export const updateCartEntry = async (data) => {
  const response = await api.put("/api/cartentry/update", data);

  return response.data;
};

// ================= ORDER =================

export const createOrder = async (cartId, paymentMethod) => {
  const response = await api.post("/api/order/add", null, {
    params: {
      cartId,
      paymentMethod,
    },
  });

  return response.data;
};
export default api;
