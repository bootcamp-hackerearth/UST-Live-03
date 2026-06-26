"use client";
 
import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";
 
const dropdown = (label, name, apiUrl, extra = {}) => ({
  label,
  name,
  type: "dropdown",
  api: apiUrl,
  payload: {
    page: 0,
    sizePerPage: 100,
    sortField: "identifier",
    sortDirection: "ASC",
  },
  optionLabel: "identifier",
  optionValue: "identifier",
  placeholder: `Select ${label}`,
  ...extra,
});
 
export default function NodeEditPage() {
  return (
    <CommonEditPage
      title="Edit Node"
 
      fetchApi={async (identifier) => {
        const res = await api.get("/api/node/get", {
          params: { identifier },
        });
        return res;
      }}
 
     updateApi={async (data) => {
  return await api.put("/api/node/update", data); 
}}
 
      redirectRoute="/node/list"
 
      identifierParam="identifier"
 
      fields={[
        
        {
          label: "Identifier",
          name: "identifier",
          type: "text",
          readOnly: true,
        },
 
        {
          label: "Path",
          name: "path",
          type: "text",
          placeholder: "Enter node path",
        },
 
        {
          label: "Roles",
          name: "roles",
          type: "dropdown",
          api: "/api/role/list",
          payload: {
            page: 0,
            sizePerPage: 100,
            sortField: "identifier",
            sortDirection: "ASC",
          },
          optionLabel: "identifier",
          optionValue: "identifier",
          placeholder: "Select Roles",
          multiple: true,
        },
      ]}
    />
  );
}