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

api.interceptors.request.use(
  (config) => {
    const token = typeof globalThis === "undefined" ? null : globalThis.localStorage?.getItem("token");

    const openEndpoints = [
      "/authenticate",
      "/user/register",
      "/role/findByStatus",
    ];

    const isOpenEndpoint = openEndpoints.some(
      (endpoint) => config.url === endpoint || config.url?.includes(endpoint)
    );

    if (!isOpenEndpoint && token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      console.warn("Unauthorized - redirecting to login");

      if (typeof globalThis !== "undefined" && globalThis.localStorage) {
        // Clear all state keys to avoid stale "ghost" data matching on login reload
        globalThis.localStorage.removeItem("token");
        globalThis.localStorage.removeItem("tokenLoginTime");
        globalThis.localStorage.removeItem("username");

        if (globalThis.window) {
          globalThis.window.location.href = "/login";
        }
      }
    }
    if (!error.response) {
      console.error("Network error - Backend may be unavailable");
    }
    return Promise.reject(error);
  }
);

export default api;