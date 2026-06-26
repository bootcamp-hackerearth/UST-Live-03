import axios from "axios";

// Prevent duplicate permission popups
let permissionAlertShown = false;

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(

  (response) => response,

  (error) => {

    if (error.response) {

      switch (error.response.status) {

        case 401:
          alert("Session expired. Please login again.");
          localStorage.clear();
          globalThis.location.href = "/login";
          break;

        case 403:

          if (!permissionAlertShown) {

            permissionAlertShown = true;

            alert("You don't have permission to perform this action.");

            // Allow future permission alerts after 1 second
            setTimeout(() => {
              permissionAlertShown = false;
            }, 1000);

          }

          break;

        case 404:
          alert(error.response.data?.message || "Resource not found.");
          break;

        case 500:
          alert(error.response.data?.message || "Something went wrong.");
          break;

        default:
          alert(error.response.data?.message || "Unexpected error.");

      }

    } else {

      alert("Unable to connect to the server.");

    }

    return Promise.reject(error);

  }

);

export default api;