import axios from "axios";

const api = axios.create({
  baseURL: "/api",
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use(
  (config) => {
    if (typeof globalThis !== "undefined") {
      const token = globalThis.localStorage.getItem("token");

      if (token && config.url !== "/authenticate") {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }

    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (typeof globalThis !== "undefined") {
      if (error.response?.status === 401) {
        globalThis.localStorage.removeItem("token");
        globalThis.location.href = "/login";
      }
    }

    return Promise.reject(error);
  }
);

export default api;
