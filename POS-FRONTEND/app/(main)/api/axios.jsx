import axios from 'axios';

const api = axios.create({
  baseURL: '',
  headers: {
            'Content-Type': 'application/json'
        }
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`; // Attaches the token
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});
api.interceptors.response.use(
  (response) => response,

  (error) => {
    const status = error.response?.status;
    const message = error.response?.data?.message || "Something went wrong.";

    switch (status) {
      case 400:
        alert(message);
        break;

      case 401:
        alert("Session expired. Please login again.");
        localStorage.removeItem("token");
        break;

      case 403:
        alert("You are not authorized to perform this action.");
        break;

      case 404:
        alert(message);
        break;

      case 500:
        alert(message);
        break;

      default:
        alert(message);
    }

    return Promise.reject(error);
  },
);
export default api;
