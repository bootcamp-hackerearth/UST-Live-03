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
 
export default function CategoryEditPage() {
  return (
    <CommonEditPage
      title="Edit Category"
 
      fetchApi={(identifier) =>
        api.get("/api/category/get", {
          params: { identifier },
        })
      }
 
      updateApi={(data) =>
        api.post("/api/category/update", data)
      }
 
      redirectRoute="/category/list"
      identifierParam="identifier"
 
      fields={[
        {
          label: "Identifier",
          name: "identifier",
          type: "text",
          readOnly: true,
        },
 
        dropdown("Super Category", "superCategory", "/api/category/list"),
 
      ]}
    />
  );
}