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
    const isAuthEndpoint = error.config?.url === "/authenticate";
    const status = error.response?.status;

    if (!isAuthEndpoint && (status === 401 || status === 403)) {
      const token = localStorage.getItem("token");
      if (!token) {
        globalThis.location.href = "/login";
        return Promise.reject(error);
      }
      if (status === 401) {
        localStorage.removeItem("token");
        globalThis.location.href = "/login";
        return Promise.reject(error);
      }
    }
    return Promise.reject(error);
  }
);
export default api;