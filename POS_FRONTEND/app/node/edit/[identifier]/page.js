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
 
      // ✅ FETCH EXISTING NODE
      fetchApi={async (identifier) => {
        const res = await api.get("/api/node/get", {
          params: { identifier },
        });
        return res;
      }}
 
      // ✅ UPDATE NODE
      updateApi={async (data) => {
        return await api.post("/api/node/update", data);
      }}
 
      // ✅ REDIRECTION
      redirectRoute="/node/list"
 
      // ✅ PARAM NAME
      identifierParam="identifier"
 
      fields={[
        // ✅ IDENTIFIER
        {
          label: "Identifier",
          name: "identifier",
          type: "text",
          readOnly: true,
        },
 
        // ✅ PATH FIELD
        {
          label: "Path",
          name: "path",
          type: "text",
          placeholder: "Enter node path",
        },
 
        // ✅ ROLES MULTISELECT
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