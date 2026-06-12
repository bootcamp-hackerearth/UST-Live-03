"use client";
 
import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";
 
export default function RoleEditPage() {
  return (
    <CommonEditPage
      title="Edit Role"
 
      fetchApi={async (identifier) => {
        const res = await api.get("/api/role/get", {
          params: { identifier },
        });
 
        return res;
      }}
 
      updateApi={async (data) => {
        return await api.post("/api/role/update", data);
      }}
 
      redirectRoute="/role/list"
 
      identifierParam="identifier"
 
      fields={[
        {
          label: "Identifier",
          name: "identifier",
          type: "text",
          readOnly: true,
        },
 
        {
          label: "Description",
          name: "description",
          type: "text",
        },
      ]}
    />
  );
}