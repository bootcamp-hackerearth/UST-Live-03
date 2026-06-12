"use client";
 
import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";
 
// ✅ helper function
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
 
export default function ProductEditPage() {
  return (
    <CommonEditPage
      title="Edit Product"
 
      // ✅ FETCH EXISTING DATA (FIXED SAFE RESPONSE HANDLING)
      fetchApi={async (identifier) => {
        const res = await api.get("/api/product/get", {
          params: { identifier },
        });
        return res;
      }}
 
      // ✅ UPDATE API (NO CHANGE)
      updateApi={async (data) => {
        return await api.post("/api/product/update", data);
      }}
 
      // ✅ REDIRECT
      redirectRoute="/product/list"
 
      // ✅ IMPORTANT (matches dynamic route param name)
      identifierParam="identifier"
 
      fields={[
        // ✅ IDENTIFIER (READ ONLY)
        {
          label: "Identifier",
          name: "identifier",
          type: "text",
          readOnly: true,
        },
 
        // ✅ MULTI SELECT CATEGORY
        dropdown("Category", "category", "/api/category/list"),
        dropdown("Brand", "brand", "/api/brand/list"),
        dropdown("Model", "model", "/api/model/list"),
        dropdown("Unit", "unit", "/api/unit/list"),
 
        // ✅ NUMBER
        {
          label: "Quantity",
          name: "quantity",
          type: "number",
        },
 
        // ✅ RADIO
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