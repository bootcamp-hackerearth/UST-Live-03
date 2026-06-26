// app/api/axios.js
"use client";

import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

const openEndpoints = [
  "/authenticate",
  "/user/register",
  "/role/getAllActive",
];

const isOpenEndpoint = (url = "") =>
  openEndpoints.some((endpoint) => url === endpoint || url.includes(endpoint));

api.interceptors.request.use(
  (config) => {
    const token =
      typeof globalThis === "undefined"
        ? null
        : globalThis.localStorage?.getItem("token");

    if (!isOpenEndpoint(config.url) && token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  // 2xx — pass through unchanged
  (response) => response,

  (error) => {
    const status     = error.response?.status;
    const requestUrl = error.config?.url || "";

    if (!error.response) {
      console.error("Network error - Backend may be unavailable");
      return Promise.reject(error);
    }

    if (status === 401 && !isOpenEndpoint(requestUrl)) {
      console.warn("Unauthorized - redirecting to login");

      if (typeof globalThis !== "undefined" && globalThis.localStorage) {
        globalThis.localStorage.removeItem("token");
        globalThis.localStorage.removeItem("tokenLoginTime");
        globalThis.localStorage.removeItem("username");

        if (globalThis.window) {
          globalThis.window.location.href = "/login";
        }
      }

      return Promise.reject(error);
    }

    if (status === 404) {
      if (typeof globalThis !== "undefined" && globalThis.window) {
        globalThis.window.location.assign("/not found");
      }
      return Promise.reject(error);
    }

    if (status >= 500) {
      const serverError = new Error(
        error.response?.data?.message || "A server error occurred."
      );
      serverError.status = status;
      serverError.cause  = error;
      return Promise.reject(serverError);
    }

    return Promise.reject(error);
  }
);

export default api;