"use client";

import { useCallback } from "react";
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

  const fetchProduct = async (identifier) => {
  try {
    const response = await api.get("/api/product/get", {
      params: { identifier },
    });

    const data = response.data?.data ?? response.data;

    return {
      data: {
        identifier: data?.identifier || "",
        category: data?.category || "",
        brand: data?.brand || "",
        model: data?.model || "",
        unit: data?.unit || "",
        quantity: data?.quantity || "",
        status: data?.status ? "true" : "false",
        createdBy: data?.createdBy || "",
        createdOn: data?.createdOn || "",
        modifiedBy: data?.modifiedBy || "",
        modifiedOn: data?.modifiedOn || "",
      },
    };
  } catch (err) {
    console.error("Fetch error:", err);
    throw err;
  }
};

  const handleUpdate = async (data) => {
    try {
      const response = await api.put("/api/product/update", {
        ...data,
        quantity: data.quantity ? Number(data.quantity) : 0,
        status: data.status === true || data.status === "true",
      });

      const res = response.data;

      if (res.success === false) {
        alert(res.message);
        return false;
      }

      alert(res.message || "Product updated successfully");
      return true;

    } catch (err) {
      console.error(err);
      alert("Server error");
      return false;
    }
  };

  const handleChange = useCallback((data) => {
  }, []);

  return (
    <CommonEditPage
      title="Edit Product"
      identifierParam="identifier"
      fetchApi={fetchProduct}
      updateApi={handleUpdate}
      redirectRoute="/product/list"
      submitButtonText="Update Product"
      onChange={handleChange}

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