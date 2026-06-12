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
 
export default function ProductAddPage() {
  return (
    <CommonAddPage
      title="Add Product"
 
      // ✅ FIXED
      submitApi={(data) => api.post("/api/product/add", data)}
 
      redirectRoute="/product/list"
 
      initialValues={{
        identifier: "",
        category: [],
        brand: "",
        model: "",
        unit: "",
        quantity: "",
        status: true,
      }}
 
      fields={[
        { label: "Identifier", name: "identifier", type: "text" },
 
        // ✅ MUST include /api
        dropdown("Category", "category", "/api/category/list", {
          multiple: true,
        }),
 
        dropdown("Brand", "brand", "/api/brand/list"),
        dropdown("Model", "model", "/api/model/list"),
        dropdown("Unit", "unit", "/api/unit/list"),
 
        { label: "Quantity", name: "quantity", type: "number" },
 
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
 