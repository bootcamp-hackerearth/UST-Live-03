import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "/api",
  headers: {
    "Content-Type": "application/json",
  },
});
axiosInstance.interceptors.request.use(
  (config) => {
    if ( globalThis.window != "undefined") {
      const token = localStorage.getItem("token");

      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }

    return config;
  },
  (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          alert("Please login again.");
          break;

        case 403:
          alert("Access denied");
          break;

        case 404:
          alert(error.response.data.message);
          break;

        default:
          alert(error.response.data?.message || "Something went wrong");
      }
    } else {
      alert("Unable to connect to server");
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;
