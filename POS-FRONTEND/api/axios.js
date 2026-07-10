import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

const isValidToken = (token) =>
  !!token && token !== "null" && token !== "undefined";

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  const openEndpoints = [
    "/authenticate",
    "/user/register",
    "/role/findByStatus",
  ];
  if (!openEndpoints.includes(config.url) && isValidToken(token)) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

const STATUS_ROUTES = {
  401: "/login",
  403: "/forbidden",
  404: "/not_found",
  500: "/error",
};

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const isAuthEndpoint = error.config?.url === "/authenticate";
    const status = error.response?.status;
    const token = localStorage.getItem("token");
    const hasToken = isValidToken(token);

    if (isAuthEndpoint) {
      return Promise.reject(error);
    }
    if (!hasToken) {
      localStorage.removeItem("token");
      globalThis.location.href = "/login";
      return Promise.reject(error);
    }

    if (status === 401) {
      localStorage.removeItem("token");
      globalThis.location.href = STATUS_ROUTES[401];
      return Promise.reject(error);
    }

    if (status === 403) {
      localStorage.removeItem("token");
      globalThis.location.href = STATUS_ROUTES[401];
      return Promise.reject(error);
    }

    if (status === 404) {
      globalThis.location.href = STATUS_ROUTES[404];
      return Promise.reject(error);
    }
    if (status === 500) {
      globalThis.location.href = STATUS_ROUTES[500];
      return Promise.reject(error);
    }
    if (!error.response) {
      globalThis.location.href = "/network-error";
      return Promise.reject(error);
    }
    return Promise.reject(error);
  }
);

export default api;