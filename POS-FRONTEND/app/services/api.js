import axios from "axios";

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 403) {
      globalThis.dispatchEvent(
        new CustomEvent("access-denied", {
          detail: {
            message:
              error.response?.data?.message ||
              "You do not have permission to access this resource.",
          },
        })
      );

      return Promise.resolve({ data: null });
    }

    return Promise.reject(error);
  }
);

export default api;