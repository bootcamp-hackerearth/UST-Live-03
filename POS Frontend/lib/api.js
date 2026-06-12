"use client";

export const BASE = "http://localhost:8080";

export const getToken = () =>
  globalThis.window?.localStorage.getItem("token") ?? null;

export const authHeaders = () => ({
  Authorization: `Bearer ${getToken()}`,
  "Content-Type": "application/json",
});

export const fetchWithAuth = async (url, options = {}) => {
  const res = await fetch(`${BASE}${url}`, {
    ...options,
    headers: options.headers
      ? { ...authHeaders(), ...options.headers }
      : authHeaders(),
  });

  if (res.status === 401) {
    if (globalThis.window !== undefined) {
      globalThis.window.localStorage.removeItem("token");
      globalThis.window.localStorage.removeItem("username");
      globalThis.window.location.href = "/login";
    }
    throw new Error("Unauthorized");
  }

  if (!res.ok) {
    let body;
    try {
      body = await res.json();
    } catch {
      body = null;
    }
    const msg = body?.message || body;
    throw Object.assign(
      new Error(typeof msg === "string" && msg ? msg : `HTTP ${res.status}`),
      { status: res.status, body },
    );
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
  fetchWithAuth(url, { method: "POST", body: JSON.stringify(data) });
export const deleteRecord = (url) =>
  fetchWithAuth(url, { method: "POST", body: JSON.stringify({}) });
export const toggleRecord = (url) =>
  fetchWithAuth(url, { method: "POST", body: JSON.stringify({}) });
