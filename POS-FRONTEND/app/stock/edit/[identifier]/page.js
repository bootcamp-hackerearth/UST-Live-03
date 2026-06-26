"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";

export default function EditStockPage() {
const fetchStock = async (identifier) => {
  try {
    const res = await api.get("/api/stock/get", {
      params: { identifier }
    });

    console.log(" STOCK RESPONSE:", res.data);

    if (res.data?.success === false) {
      alert(res.data.message || "Failed to fetch stock");
      return {};
    }

    return res;   

  } catch (error) {
    console.error("Fetch error:", error);
    alert("Server error while fetching stock");
    return {};
  }
};

  const updateStock = async (data) => {
    try {
      const res = await api.put("/api/stock/update", {
        ...data,
        quantity: Number(data.quantity),
        minimumStock: Number(data.minimumStock),
      });

      const response = res.data;

      if (response?.success === false) {
        alert(response.message || "Update failed");
        return false;
      }

      alert(response.message || "Stock updated successfully");
      return true; 

    } catch (error) {
      console.error("Update error:", error);
      alert("Server error");
      return false;
    }
  };

  return (
    <CommonEditPage
      title="Edit Stock"
      fetchApi={fetchStock}
      updateApi={updateStock}
      redirectRoute="/stock/list"
      identifierParam="identifier"
      submitButtonText="Update Stock"

      fields={[
        {
          name: "identifier",
          label: "Stock Identifier",
          type: "text",
          readOnly: true,
        },

        {
          name: "productIdentifier",
          label: "Product",
          type: "text",
          readOnly: true,
        },

        {
          name: "warehouseIdentifier",
          label: "Warehouse",
          type: "text",
          readOnly: true,
        },

        {
          name: "quantity",
          label: "Quantity",
          type: "number",
          required: true,
        },

        {
          name: "minimumStock",
          label: "Minimum Stock",
          type: "number",
          required: true,
        },
      ]}
    />
  );
}
