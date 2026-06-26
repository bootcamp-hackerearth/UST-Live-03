"use client";

import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080/api",
  withCredentials: true, 
  headers: {
    "Content-Type": "application/json",
  },
});

const PUBLIC_URLS = [
  "/authenticate",
  "/user/register",
  "/role/findActiveStatus"
];

axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");

    const isPublicApi = PUBLIC_URLS.some((url) =>
      config.url?.includes(url)
    );

    if (!token && !isPublicApi) {
      localStorage.clear();
      globalThis.location.href = "/Login";
      return Promise.reject(
        new Error("No authentication token found")
      );
    }

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    const csrfToken = globalThis.document?.cookie
      ?.split("; ")
      ?.find((row) => row.startsWith("XSRF-TOKEN="))
      ?.split("=")[1];

    if (csrfToken) {
      config.headers["X-XSRF-TOKEN"] = decodeURIComponent(csrfToken);
    }

    return config;
  },
  (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
  (response) => response,

  (error) => {
    const status = error.response?.status;

    if (status === 401) {
      localStorage.clear();
      globalThis.location.href = "/Login";
    }

    if (status === 500) {
      console.warn("Global Interceptor: 500 Error detected. Redirecting to /500...");
      if (globalThis.window !== undefined) {
        globalThis.location.href = "/500";
      }
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;