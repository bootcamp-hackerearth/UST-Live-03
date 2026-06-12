import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  const url = config.url || "";

  const publicPaths = [
    "/authenticate",
    "/user/add",
    "/role/findAllActive",
  ];

  const isPublic = publicPaths.some((path) => url.includes(path));

  if (!token && !isPublic) {
    if (globalThis.window !== undefined) {
      globalThis.location.href = "/Login";
    }
    return Promise.reject(new Error("Authentication required"));
  }

  if (token && !isPublic) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && globalThis.window !== undefined) {
      globalThis.location.href = "/Login";
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;