// app/api/axios.js
"use client";

import axios from "axios";

const api = axios.create({
  baseURL: "/api",
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

const isClient =
  typeof globalThis !== "undefined" &&
  globalThis.window !== undefined &&
  globalThis.localStorage !== undefined;

const clearAuthStorage = () => {
  if (!isClient) return;
  globalThis.localStorage.removeItem("token");
  globalThis.localStorage.removeItem("tokenLoginTime");
  globalThis.localStorage.removeItem("username");
};

const redirectTo = (path) => {
  if (!isClient) return;
  globalThis.window.location.assign(path);
};

api.interceptors.response.use(
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
      clearAuthStorage();
      if (isClient) {
        globalThis.window.location.href = "/login";
      }
      return Promise.reject(error);
    }

    if (status === 404) {
      redirectTo("/pos/not_found");
      return Promise.reject(error);
    }

    if (status === 403) {
      redirectTo("/pos/unauthorized");
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
