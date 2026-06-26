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

export default function AddStockPage() {

  const handleSubmit = async (data) => {
    try {
      const response = await api.post("/api/stock/add", {
        ...data,
        quantity: Number(data.quantity),
        minimumStock: Number(data.minimumStock),
      });

      console.log("STOCK RESPONSE:", response.data);

      const res = response.data;

      if (res.success === false) {
        alert(res.message || "Failed to add stock");
        return false; 
      }

      alert(res.message || "Stock added successfully");
      return true; 

    } catch (error) {
      console.error("ERROR:", error);
      alert("Server error");
      return false; 
    }
  };

  return (
    <CommonAddPage
      title="Add Stock"
      submitApi={handleSubmit}
      redirectRoute="/stock/list"
      submitButtonText="Save Stock"

      initialValues={{
        productIdentifier: "",
        warehouseIdentifier: "",
        quantity: "",
        minimumStock: "",
      }}

      fields={[
        dropdown(
          "Product",
          "productIdentifier",
          "/api/product/list"
        ),

        dropdown(
          "Warehouse",
          "warehouseIdentifier",
          "/api/warehouse/list"
        ),

        {
          name: "quantity",
          label: "Quantity",
          type: "number",
          required: true,
          placeholder: "Enter quantity",
          min: 0,
        },

        {
          name: "minimumStock",
          label: "Minimum Stock",
          type: "number",
          required: true,
          placeholder: "Enter minimum stock level",
          min: 0,
        },
      ]}
    />
  );
}