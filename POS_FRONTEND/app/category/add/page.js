"use client";
 
import CommonAddPage from "@/app/components/CommonAddPage";
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
 
export default function CategoryAddPage() {
  return (
    <CommonAddPage
      title="Add Category"
 
      // ✅ API
      submitApi={(data) => api.post("/api/category/add", data)}
 
      redirectRoute="/category/list"
 
      // ✅ INITIAL VALUES
      initialValues={{
        identifier: "",
        superCategory: "",
        status: true,
      }}
 
      fields={[
        {
          label: "Identifier",
          name: "identifier",
          type: "text",
        },
 
        // ✅ SUPER CATEGORY (self reference)
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