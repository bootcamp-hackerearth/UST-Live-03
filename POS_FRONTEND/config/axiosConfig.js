import axios from "axios";

const instance = axios.create({
  baseURL: "http://localhost:8080/api",
  withCredentials: true,
});

instance.interceptors.request.use((config) => {
  if (globalThis.window === undefined) {
    return config;
  }

  const token = localStorage.getItem("token");

  if (token && !config.url?.includes("/authenticate")) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export default instance;