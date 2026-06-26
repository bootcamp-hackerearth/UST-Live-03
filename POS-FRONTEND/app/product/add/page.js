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

  const handleSubmit = async (data) => {
    try {
      const response = await api.post("/api/product/add", {
        ...data,
        quantity: data.quantity ? Number(data.quantity) : 0,
        status: data.status === true || data.status === "true",
      });

      console.log("PRODUCT RESPONSE:", response.data);

      const res = response.data;

      if (res.success === false) {
        alert(res.message);
        return false;
      }

      alert(res.message || "Product added successfully");
      return true;

    } catch (error) {
      console.error("ERROR:", error);
      alert("Server error");
      return false;
    }
  };

  return (
    <CommonAddPage
      title="Add Product"

      submitApi={handleSubmit}

      redirectRoute="/product/list"

      initialValues={{
        identifier: "",
        category: "",
        brand: "",
        model: "",
        unit: "",
        quantity: "",
        status: true,
      }}

      fields={[

        {
          label: "Identifier *",
          name: "identifier",
          type: "text",
          required: true,
        },

        dropdown("Category", "category", "/api/category/list"),
        dropdown("Brand", "brand", "/api/brand/list"),
        dropdown("Model", "model", "/api/model/list"),
        dropdown("Unit", "unit", "/api/unit/list"),
        {
          label: "Status",
          name: "status",
          type: "radio",
          options: [
            { label: "Active", value: "true" },
            { label: "Inactive", value: "false" },
          ],
        },
      ]}
    />
  );
}