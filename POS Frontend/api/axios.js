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

const STATUS_ROUTES = {
  403: "/forbidden",
  404: "/not_found",
  500: "/error",
};

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const isAuthEndpoint = error.config?.url === "/authenticate";
    const status = error.response?.status;

    if (isAuthEndpoint) {
      return Promise.reject(error);
    }

    if (status === 401) {
      localStorage.removeItem("token");
      globalThis.location.href = "/login";
      return Promise.reject(error);
    }

    const skip = error.config?.skipErrorRedirect;
    const isSkipped = skip === true || (Array.isArray(skip) && skip.includes(status));

    if (!isSkipped && STATUS_ROUTES[status]) {
      globalThis.location.href = STATUS_ROUTES[status];
      return Promise.reject(error);
    }

    return Promise.reject(error);
  }
);

export default api;