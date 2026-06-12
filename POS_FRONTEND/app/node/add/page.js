"use client";
 
import CommonAddPage from "@/app/components/CommonAddPage";
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
 
export default function NodeAddPage() {
  return (
    <CommonAddPage
      title="Add Node"
 
      submitApi={(data) => api.post("/api/node/add", data)}
 
      redirectRoute="/node/list"
 
      initialValues={{
        identifier: "",
        path: "",
        roles: [],
        status: true,
      }}
 
      fields={[
      
        { label: "Identifier", name: "identifier", type: "text" },
 
        {
          label: "Path",
          name: "path",
          type: "text",
          placeholder: "Enter node path",
        },
 
        dropdown("Roles", "roles", "/api/role/list", {
          multiple: true, 
        }),
      ]}
    />
  );
}
 