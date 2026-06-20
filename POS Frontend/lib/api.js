"use client";

import logger from "./logger";
import { STORAGE_KEYS, HTTP_STATUS, ERROR_MESSAGES, PATHS } from "@/config/constants";

export const BASE = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

/**
 * Get authentication token from localStorage
 */
export const getToken = () => logger.getStorageItem(STORAGE_KEYS.TOKEN) ?? null;

/**
 * Get authentication headers with proper error handling
 */
export const authHeaders = () => ({
  Authorization: `Bearer ${getToken()}`,
  "Content-Type": "application/json",
});

/**
 * Fetch with authentication and error handling
 */
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
      logger.warn("Unauthorized access - clearing storage and redirecting", { url });
      logger.removeStorageItem(STORAGE_KEYS.TOKEN);
      logger.removeStorageItem(STORAGE_KEYS.USERNAME);
      
      if (globalThis.window !== undefined) {
        if (globalThis.window.location !== undefined) {
          globalThis.window.location.href = PATHS.LOGIN;
        }
      }
      throw new Error(ERROR_MESSAGES.UNAUTHORIZED);
    }

    if (!res.ok) {
      let body;
      try {
        body = await res.json();
      } catch (e) {
        logger.error("Failed to parse error response", e, `${method} ${url}`);
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
    logger.error(`API request failed: ${options.method || "GET"} ${url}`, error, "fetchWithAuth");
    throw error;
  }
};

/**
 * List records with pagination
 */
export const listRecords = (url, page = 0, size = 10) =>
  fetchWithAuth(url, {
    method: "POST",
    body: JSON.stringify({ page, sizePerPage: size }),
  });

/**
 * Get a single record
 */
export const getRecord = (url) => fetchWithAuth(url);

/**
 * Save a new record
 */
export const saveRecord = (url, data) =>
  fetchWithAuth(url, { method: "POST", body: JSON.stringify(data) });

/**
 * Update an existing record
 */
export const updateRecord = (url, data) =>
  fetchWithAuth(url, { method: "POST", body: JSON.stringify(data) });

/**
 * Delete a record
 */
export const deleteRecord = (url) =>
  fetchWithAuth(url, { method: "POST", body: JSON.stringify({}) });

/**
 * Toggle record status
 */
export const toggleRecord = (url) =>
  fetchWithAuth(url, { method: "POST", body: JSON.stringify({}) });
