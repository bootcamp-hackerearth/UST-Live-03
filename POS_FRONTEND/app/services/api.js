import axios from "axios";

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

    
if (status === 401) {
  if (globalThis.window !== undefined) {
    localStorage.removeItem("token");
    globalThis.window.location.href = "/login";
  }
}
 else if (status === 403) {
      console.warn("Access Denied (403)");
    }
    return Promise.reject(error);
  }
);

export default api;
