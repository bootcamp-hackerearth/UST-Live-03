"use client";

import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

// Public APIs that do not need JWT token
const PUBLIC_URLS = [
  "/authenticate",
  "/user/register",
  "/role/findActiveStatus",
];

// Request Interceptor
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");

    const isPublicApi = PUBLIC_URLS.some((url) =>
      config.url?.includes(url)
    );

    // Protected API without token
    if (!token && !isPublicApi) {
      localStorage.clear();
      globalThis.location.href = "/Login";
      return Promise.reject(
        new Error("No authentication token found")
      );
    }

    // Add JWT token to request
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor
axiosInstance.interceptors.response.use(
  (response) => response,

  (error) => {
    const status = error.response?.status;

    // Token expired or invalid
    if (status === 401 || status === 403) {
      localStorage.clear();
      globalThis.location.href = "/Login";
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;