"use client";

import logger from "./logger";
import { STORAGE_KEYS, HTTP_STATUS, ERROR_MESSAGES, PATHS } from "@/config/constants";

export const BASE = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

export const getToken = () => logger.getStorageItem(STORAGE_KEYS.TOKEN) ?? null;

export const authHeaders = () => ({
  Authorization: `Bearer ${getToken()}`,
  "Content-Type": "application/json",
});

export const fetchWithAuth = async (url, options = {}) => {
  try {
    const fullUrl = `${BASE}${url}`;
    const method = options.method || "GET";
    
    const res = await fetch(fullUrl, {
      ...options,
      headers: options.headers
        ? { ...authHeaders(), ...options.headers }
        : authHeaders(),
    });

    if (res.status === HTTP_STATUS.UNAUTHORIZED) {
      logger.removeStorageItem(STORAGE_KEYS.TOKEN);
      logger.removeStorageItem(STORAGE_KEYS.USERNAME);
      
      if (globalThis.window?.location) {
        globalThis.window.location.href = PATHS.LOGIN;
      }
      throw new Error(ERROR_MESSAGES.UNAUTHORIZED);
    }

    if (!res.ok) {
      let body;
      try {
        body = await res.json();
      } catch {
        body = null;
      }
      
      const msg = body?.message || body || ERROR_MESSAGES.SERVER_ERROR;
      const errorMessage = typeof msg === "string" ? msg : ERROR_MESSAGES.SERVER_ERROR;
      
      logger.apiError(url, method, res.status, errorMessage);
      
      throw Object.assign(
        new Error(errorMessage),
        { status: res.status, body },
      );
    }

    logger.apiSuccess(url, method, res.status);
    
    const ct = res.headers.get("content-type") || "";
    if (ct.includes("application/json")) return res.json();
    return null;
  } catch (error) {
    throw error;
  }
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
  fetchWithAuth(url, { method: "POST", body: JSON.stringify(data) });

export const deleteRecord = (url) =>
  fetchWithAuth(url, { method: "POST", body: JSON.stringify({}) });

export const toggleRecord = (url) =>
  fetchWithAuth(url, { method: "POST", body: JSON.stringify({}) });
