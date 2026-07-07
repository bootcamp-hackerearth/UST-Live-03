import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  const openEndpoints = [
    "/authenticate",
    "/user/register",
    "/role/findByStatus",
  ];
  if (!openEndpoints.includes(config.url) && token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const isAuthEndpoint = error.config?.url?.includes("/authenticate");
    if (!isAuthEndpoint && error.response?.status === 401) {
      localStorage.removeItem("token");
      globalThis.location.href = "/login";
    }
    if (!isAuthEndpoint && error.response?.status === 403) {
      globalThis.location.href = "/403";
    }
    if (!isAuthEndpoint && error.response?.status === 500) {
      globalThis.location.href = "/500";
    }
    return Promise.reject(error);
  }
);

export default api;