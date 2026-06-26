import api from "@/app/services/api";

export const entitySubmit = async (
  url,
  payload,
  successMessage
) => {
  try {
    const response = await api.post(url, payload);

    const res = response.data;

    if (res.success === false) {
      alert(res.message);
      return false;
    }

    alert(res.message || successMessage);
    return true;
  } catch (error) {
    console.error("ERROR:", error);
    alert("Server error");
    return false;
  }
};