import axios from "axios";
 
const api = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 10000,
});
 
// ✅ REQUEST INTERCEPTOR
api.interceptors.request.use(
  (config) => {
    // ✅ FIX: use globalThis instead of window
    if (typeof globalThis !== "undefined") {
      const token = globalThis.localStorage?.getItem("token");
 
      console.log("✅ Token being sent:", token);
      console.log("🚀 API CALL:", config.method?.toUpperCase(), config.url);
 
      if (
        token &&
        config?.url &&
        !config.url.includes("/api/authenticate")
      ) {
        config.headers = config.headers || {}; // ensure headers
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
 
    // ✅ ensure headers exist
    config.headers = config.headers || {};
    config.headers["Content-Type"] = "application/json";
 
    return config;
  },
  (error) => Promise.reject(error)
);
 
// ✅ RESPONSE INTERCEPTOR
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
 
    console.error("❌ API ERROR:", status, error.response?.data);
 
    // ✅ 401 - Session expired
    if (status === 401) {
      alert("Session expired. Please login again.");
 
      // ✅ FIX: use globalThis
      globalThis.localStorage?.removeItem("token");
 
      if (typeof globalThis !== "undefined") {
        globalThis.location.href = "/login"; // ✅ FIX
      }
    }
 
    // ✅ 403 - forbidden
    if (status === 403) {
      console.warn("❌ Access Denied (403)");
    }
 
    return Promise.reject(error);
  }
);
 
export default api;