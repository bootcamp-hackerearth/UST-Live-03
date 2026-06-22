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
  const url = config.url || "";
  const requestPath = url.startsWith("http") ? new URL(url).pathname : url;

  config.headers = config.headers || {};
  if (!openEndpoints.includes(requestPath) && token) {
    config.headers.Authorization = token.startsWith("Bearer ") ? token : `Bearer ${token}`;
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
 
      // Only redirect if token is actually missing/expired
      // NOT for permission errors on specific endpoints
      if (!token) {
        globalThis.location.href = "/login";
        return Promise.reject(error);
      }
 
      // If 401 specifically — token expired, force re-login
      if (status === 401) {
        localStorage.removeItem("token");
        globalThis.location.href = "/login";
        return Promise.reject(error);
      }
 
      // 403 = forbidden (role/permission issue) — DON'T redirect to login
      // just silently fail so the page still loads
    }
 
    return Promise.reject(error);
  }
);
export default api;