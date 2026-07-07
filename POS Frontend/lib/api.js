"use client";

import { ERROR_MESSAGES, HTTP_STATUS, PATHS, STORAGE_KEYS } from "@/config/constants";

export const BASE = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

const getStorageItem = (key) => {
  try {
    if (globalThis.window?.localStorage) {
      return globalThis.window.localStorage.getItem(key);
    }
  } catch {
    return null;
  }

  return null;
};

const removeStorageItem = (key) => {
  try {
    if (globalThis.window?.localStorage) {
      globalThis.window.localStorage.removeItem(key);
    }
  } catch {
    // no-op
  }
};

export const getToken = () => getStorageItem(STORAGE_KEYS.TOKEN) ?? null;

export const authHeaders = () => ({
  Authorization: `Bearer ${getToken()}`,
  "Content-Type": "application/json",
});

const clearAuthStorage = () => {
  removeStorageItem(STORAGE_KEYS.TOKEN);
  removeStorageItem(STORAGE_KEYS.USERNAME);
};

const redirectToLogin = () => {
  clearAuthStorage();

  if (globalThis.window?.location) {
    globalThis.window.location.replace(PATHS.LOGIN);
  }
};

const getRedirectPath = (status) => {
  switch (status) {
    case HTTP_STATUS.FORBIDDEN:
      return PATHS.FORBIDDEN;
    case HTTP_STATUS.NOT_FOUND:
      return PATHS.NOT_FOUND;
    case HTTP_STATUS.SERVER_ERROR:
      return PATHS.SERVER_ERROR;
    default:
      return null;
  }
};

const redirectToErrorPage = (status) => {
  if (!globalThis.window?.location) return;

  const currentPath = globalThis.window.location.pathname;
  const targetPath = getRedirectPath(status);

  if (!targetPath || currentPath === targetPath) return;

  globalThis.window.location.assign(targetPath);
};

const handleUnauthorized = () => {
  redirectToLogin();
  throw new Error(ERROR_MESSAGES.UNAUTHORIZED);
};

const handleResponseStatus = (status) => {
  if (status === HTTP_STATUS.UNAUTHORIZED) {
    handleUnauthorized();
  }

  if (status === HTTP_STATUS.FORBIDDEN) {
    if (!getToken()) {
      handleUnauthorized();
    }

    redirectToErrorPage(status);
    return;
  }

  if (status === HTTP_STATUS.NOT_FOUND || status === HTTP_STATUS.SERVER_ERROR) {
    redirectToErrorPage(status);
  }
};

const parseErrorBody = async (res) => {
  try {
    return await res.json();
  } catch {
    return null;
  }
};

export const fetchWithAuth = async (url, options = {}) => {
  const fullUrl = `${BASE}${url}`;

  const res = await fetch(fullUrl, {
    ...options,
    headers: options.headers
      ? { ...authHeaders(), ...options.headers }
      : authHeaders(),
  });

  handleResponseStatus(res.status);

  if (!res.ok) {
    const body = await parseErrorBody(res);
    const msg = body?.message || body || ERROR_MESSAGES.SERVER_ERROR;
    const errorMessage = typeof msg === "string" ? msg : ERROR_MESSAGES.SERVER_ERROR;

    throw Object.assign(new Error(errorMessage), { status: res.status, body });
  }

  const ct = res.headers.get("content-type") || "";
  if (ct.includes("application/json")) return res.json();
  return null;
};

export const listRecords = (url, page = 0, size = 10) =>
  fetchWithAuth(url, {
    method: "POST",
    body: JSON.stringify({ page, sizePerPage: size }),
  });

export const getRecord = (url) => fetchWithAuth(url);

export const saveRecord = (url, data) =>
  fetchWithAuth(url, { method: "POST", body: JSON.stringify(data) });

export const updateRecord = (url, data) =>
  fetchWithAuth(url, { method: "PUT", body: JSON.stringify(data) });

export const deleteRecord = (url) =>
  fetchWithAuth(url, { method: "DELETE", body: JSON.stringify({}) });

export const toggleRecord = (url) =>
  fetchWithAuth(url, { method: "POST", body: JSON.stringify({}) });