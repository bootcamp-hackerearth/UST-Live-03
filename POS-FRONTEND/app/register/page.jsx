import RegisterForm from "./RegisterForm";
import api from "@/services/api";

export default async function Page() {
  let normalized = [];

  try {
    const res = await api.post("/role/list", {
      page: 0,
      sizePerPage: 50,
    });

    if (res.status === 200) {
      const data = res.data;

      let roles = [];

      if (Array.isArray(data)) {
        roles = data;
      } else if (Array.isArray(data?.dtoList)) {
        roles = data.dtoList;
      } else if (Array.isArray(data?.content)) {
        roles = data.content;
      }

      normalized = roles.map(
        (r) => r?.identifier || r?.name || r
      );
    }
  } catch (error) {
    console.error("Role fetch failed:", error);
  }

  return <RegisterForm roles={normalized} />;
}
