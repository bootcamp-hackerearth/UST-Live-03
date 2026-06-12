"use client";
 
import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";
 
// ✅ reusable dropdown
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
 
      // ✅ FETCH
      fetchApi={(identifier) =>
        api.get("/api/category/get", {
          params: { identifier },
        })
      }
 
      // ✅ UPDATE
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
 
        // ✅ SUPER CATEGORY
        dropdown("Super Category", "superCategory", "/api/category/list"),
 
        {
          label: "Status",
          name: "status",
          type: "radio",
          options: [
            { label: "Active", value: true },
            { label: "Inactive", value: false },
          ],
        },
      ]}
    />
  );
}