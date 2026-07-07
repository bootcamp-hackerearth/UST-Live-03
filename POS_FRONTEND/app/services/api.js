import axios from "axios";

export const showErrorModal = (data) => {
  globalThis.dispatchEvent(
    new CustomEvent("show-error", { detail: data })
  );
};

const api = axios.create({
  baseURL: "",
  timeout: 30000,
});

api.interceptors.request.use(
  (config) => {
    if (globalThis.window !== undefined) {
      const token = localStorage.getItem("token");
      if (token && config.url && !config.url.includes("/api/authenticate")) {
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
    const status = error.response?.status;
    const message = error.response?.data?.message || "An unexpected error occurred.";

    if (status === 401) {
      if (globalThis.window !== undefined) {
        localStorage.removeItem("token");
        globalThis.window.location.href = "/login";
      }
    } else if (status === 403 || status === 404 || status === 500) {
      showErrorModal({ status, message });
    }

    return Promise.reject(error);
  }
);

export default api;
