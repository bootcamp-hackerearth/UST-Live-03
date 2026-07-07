import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "/api",
  headers: {
    "Content-Type": "application/json",
  },
});

function isTokenExpired(token) {
  try {
    const payload = JSON.parse(atob(token.split(".")[1].replaceAll("-", "+").replaceAll("_", "/")));
    return payload.exp * 1000 < Date.now();
  } catch {
    return true;
  }
}

function clearAuthAndRedirect() {
  localStorage.removeItem("token");
  localStorage.removeItem("username");
  globalThis.location.href = "/Login";
}

axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  const url = config.url || "";

  const publicPaths = [
    "/authenticate",
    "/user/add",
    "/role/findAllActive",
  ];

  const isPublic = publicPaths.some((path) => url.includes(path));

  if (!isPublic) {
    if (!token || isTokenExpired(token)) {
      if (globalThis.window !== undefined) {
        clearAuthAndRedirect();
      }
      return Promise.reject(new Error("Authentication required"));
    }
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && globalThis.window !== undefined) {
      clearAuthAndRedirect();
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;