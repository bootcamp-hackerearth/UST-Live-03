import axios from "axios";
import { getToken } from "../utils/auth";

const api = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use(
  (config) => {
    const token = getToken();

    console.log("TOKEN:", token);
    console.log("URL:", config.url);

    if (
      token &&
      !config.url.includes("/api/authenticate") &&
      !config.url.includes("/api/user/register") &&
      !config.url.includes("/api/register")
    ) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  }
);

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

const DEFAULT_PAGINATION = {
  page: 0,
  sizePerPage: 10,
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
  const response = await api.put(
    `/api/${model}/update`,
    data
  );

  return response.data;
};

export const deleteItem = async (model, value, key = "identifier") => {
  const response = await api.delete(`/api/${model}/delete`, {
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
  const response = await api.put(
    "/api/user/update",
    userData
  );

  return response.data;
};

export const fetchRoles = async () => {
  const response = await api.get("/api/role/list");
  return response.data;
};

export const getNodeRoles = async () => {
  const response = await api.get("/api/node/roles");
  return response.data;
};

// ================= ORDER =================

export const createOrder = async (
  cartId,
  paymentMethod
) => {
  const response = await api.post(
    "/api/order/create",
    null,
    {
      params: {
        cartId,
        paymentMethod,
      },
    }
  );

  return response.data;
};

export const updateOrderStatus = async (
  orderId,
  status
) => {
  const response = await api.put(
    "/api/order/updateStatus",
    null,
    {
      params: {
        orderId,
        status,
      },
    }
  );

  return response.data;
};

// ================= ORDER ITEMS =================

export const getOrderItems = async (
  orderIdentifier
) => {
  const response = await api.get(
    "/api/orderitem/getByOrder",
    {
      params: {
        orderIdentifier,
      },
    }
  );

  return response.data;
};
export default api;