import axios from "axios";

const api = axios.create({
  baseURL: "",
  timeout: 10000,
});

api.interceptors.request.use(
  (config) => {
    if (typeof globalThis !== "undefined") {
      const token = globalThis.localStorage?.getItem("token");

      console.log(" Token being sent:", token);
      console.log(" API CALL:", config.method?.toUpperCase(), config.url);

      if (
        token &&
        config?.url &&
        !config.url.includes("/api/authenticate")
      ) {
        config.headers = config.headers || {}; 
        config.headers.Authorization = `Bearer ${token}`;
      }
    }

    config.headers = config.headers || {};
    config.headers["Content-Type"] = "application/json";

    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;

    console.error(" API ERROR:", status, error.response?.data);

    if (status === 401) {
      alert("Session expired. Please login again.");

      globalThis.localStorage?.removeItem("token");

      if (typeof globalThis !== "undefined") {
        globalThis.location.href = "/login"; 
      }
    }

    if (status === 403) {
      console.warn(" Access Denied (403)");
    }

    return Promise.reject(error);
  }
);

export default api;
