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

export default function ProductEditPage() {
  return (
    <CommonEditPage
      title="Edit Product"
      fetchApi={async (identifier) => {
        const res = await api.get("/api/product/get", {
          params: { identifier },
        });
        return res;
      }}
      updateApi={async (data) => {
        return await api.post("/api/product/update", data);
      }}


      redirectRoute="/product/list"

      identifierParam="identifier"

      fields={[
        {
          label: "Identifier",
          name: "identifier",
          type: "text",
          readOnly: true,
        },

        dropdown("Category", "category", "/api/category/list"),
        dropdown("Brand", "brand", "/api/brand/list"),
        dropdown("Model", "model", "/api/model/list"),
        dropdown("Unit", "unit", "/api/unit/list"),

        {
          label: "Quantity",
          name: "quantity",
          type: "number",
        },

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