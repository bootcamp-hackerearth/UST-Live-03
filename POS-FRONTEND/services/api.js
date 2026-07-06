import { getToken } from "../utils/auth";
import axios from "axios";

const api = axios.create({
  baseURL: "",
});

api.interceptors.request.use((config) => {

  const publicRoutes = [
    "/api/authenticate",
    "/api/user/register",
  ];

  if (publicRoutes.includes(config.url)) {
    return config;
  }

  const token = getToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,

  (error) => {

    if (
      error.response?.status === 401 ||
      error.response?.status === 403
    ) {

      localStorage.removeItem("token");

      if (
        globalThis.location?.pathname !== "/login"
      ) {
        globalThis.location.href = "/login";
      }
    }

    return Promise.reject(error);
  }
);

export const DEFAULT_PAGINATION = {
  page: 0,
  sizePerPage: 10,
  sortDirection: "ASC",
  sortField: "identifier",
};

export const listItems = async (model, paginationOverrides = {}) => {
  const response = await api.post(`/api/${model}/list`, {
    ...DEFAULT_PAGINATION,
    ...paginationOverrides,
  });
  return response.data;
};


export const addItem = async (model, data) => {
  const response = await api.post(`/api/${model}/add`, data);
  return response.data;
};


export const getItem = async (
  model,
  value
) => {

  const paramName =
    model === "user"
      ? "username"
      : "identifier";

  const response =
    await api.get(
      `/api/${model}/get`,
      {
        params: {
          [paramName]: value
        },
      }
    );

  return response.data;
};

export const updateItem = async (model, data) => {
  const response = await api.put(`/api/${model}/update`, data);
  return response.data;
};


export const deleteItem = async (model, identifier, extraData = {}) => {
  const body =
    model === "user"
      ? { username: identifier }
      : { identifier, ...extraData };

  const response = await api.delete(
    `/api/${model}/delete`,
    { data: body }
  );
  return response.data;
};

export const toggleItem = async (model, identifier, status) => {
  const response = await api.patch(`/api/${model}/toggle`, {
    identifier,
    status,
  });
  return response.data;
};

export const loginUser = async (username, password) => {
  const response = await api.post("/api/authenticate", { username, password });
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

export const getNodesForRoles = async () => {

  const response = await api.get("/api/node/getnodes");
  return response.data;
};

export const fetchActiveRoles = async () => {
  const response = await api.get("/api/role/active");
  return response.data;
};

export const fetchActiveProducts = async () => {
  const response = await api.get("/api/product/active");
  return response.data;
}

export const clearCart=async(cartId)=>{
  const response=await api.post("/api/cart/clear",{identifier:cartId});
  return response.data;
}

export const getOrderById = async (id) => {
  const response = await api.get(`/api/order/getById`, { params: { id } });
  return response.data;
};

export const fetchActiveItems = async (model) => {
  const response = await api.get(`/api/${model}/active`);
  return response.data;
};

export default api;
