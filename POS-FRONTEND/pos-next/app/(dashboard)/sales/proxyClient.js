import axios from "axios";

const getCookieOnClient = (name) => {
  if (typeof document === "undefined") return null;
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
  return null;
};

const proxyPost = async (endpoint, payload) => {
  try {
    const clientToken = getCookieOnClient("token");

    const response = await axios.post(
      "/auth/proxy",
      { endpoint, method: "POST", payload },
      { 
        headers: {
          "X-Fallback-Auth": clientToken ? `Bearer ${clientToken}` : ""
        },
        withCredentials: true 
      }
    );
    return response.data;
  } catch (error) {
    console.error(`[proxyPost] ${endpoint} failed:`, error?.response?.data || error.message);
    throw error;
  }
};

const proxyGet = async (endpoint) => {
  try {
    const clientToken = getCookieOnClient("token");

    const response = await axios.get("/auth/proxy", {
      params: { endpoint },
      headers: {
        "X-Fallback-Auth": clientToken ? `Bearer ${clientToken}` : ""
      },
      withCredentials: true,
    });
    return response.data;
  } catch (error) {
    console.error(`[proxyGet] ${endpoint} failed:`, error?.response?.data || error.message);
    throw error;
  }
};

export { proxyPost, proxyGet };