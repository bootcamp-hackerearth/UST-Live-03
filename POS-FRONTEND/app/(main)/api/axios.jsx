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
const redirectToErrorPage = (errorData) => {
  sessionStorage.setItem("globalError", JSON.stringify(errorData));

  globalThis.location.href = "/error";
};

api.interceptors.response.use(
  (response) => response,

  (error) => {
    const status = error.response?.status;
    const message = error.response?.data?.message || "Something went wrong.";

    switch (status) {
      case 400:
      case 404:
      case 500:
        redirectToErrorPage(error.response.data);
        break;

      case 401:
        localStorage.removeItem("token");
        redirectToErrorPage(error.response.data);
        break;

      case 403:
        redirectToErrorPage(error.response.data);
        break;

      default:
        redirectToErrorPage({
          status: status || 500,
          message: message,
          path: globalThis.location.pathname,
          timestamp: new Date().toISOString(),
        });
    }

    return Promise.reject(error);
  },
);
export default api;
