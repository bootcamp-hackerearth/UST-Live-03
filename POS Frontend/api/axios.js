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
 
      if (!token) {
        globalThis.location.href = "/login";
        return Promise.reject(error);
      }
 
      if (status === 401) {
        localStorage.removeItem("token");
        globalThis.location.href = "/login";
        return Promise.reject(error);
      }

      const permissionError = new Error("Permission denied");
      permissionError.response = { status: 403 };
      return Promise.reject(permissionError);
    }
 
    return Promise.reject(error);
  }
);

export default api;