import axios from "axios";
import { getToken } from "@/utils/auth";
 
const api = axios.create({
  baseURL: "/",
  headers: {
    "Content-Type": "application/json",
  },
});
 
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
  sortDirection: "ASC",
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

export const getAllItems = async (model) => {
  const response = await api.get(
    `/api/${model}/all`
  );

  return response.data;
};

export const getActiveShelves = async () => {
  const response = await api.get(
    "/api/shelf/active"
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

export const getSubCategories = async () => {
  const response = await api.get(
    "/api/category/subcategories"
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
    "/api/register",
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

export const addToCart = async (payload) => {
    const response = await api.post(
      "/api/cartentry/add",
      payload
    );

    return response.data;
  };

export default api;

export const clearCart = async (cartId) => {
  const response = await api.get(
    "/api/cartentry/clear",
    {
      params: { cartId },
    }
  );

  return response.data;
};

export const getCartItems = async (cartId) => {
  const response = await api.post(
    "/api/cartentry/getByCartId",
    {
      cartId,
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

export const getOrderItems = async (orderIdentifier) => {
  const res = await api.get(
    "/api/orderitem/getByOrder",
    {
      params: {
        orderIdentifier,
      },
    }
  );

  return res.data;
};
