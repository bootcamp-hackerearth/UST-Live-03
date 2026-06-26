"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";

export default function PriceEditPage() {
  const fetchPrice = async (identifier) => {
    try {
      const response = await api.get("/api/price/get", {
        params: { identifier },
      });

      const data = response.data?.data ?? response.data;

      return {
        data: {
          identifier: data?.identifier || "",
          product: data?.product || "",
          priceAmount: data?.priceAmount || "",
          type: data?.type || "",
          createdBy: data?.createdBy || "",
          createdOn: data?.createdOn || "",
          modifiedBy: data?.modifiedBy || "",
          modifiedOn: data?.modifiedOn || "",
        },
      };
    } catch (err) {
      console.error("Fetch Price Error:", err);
      throw err;
    }
  };

  const handleUpdate = async (data) => {
    try {
      const response = await api.put("/api/price/update", {
        identifier: data.identifier,
        product: data.product,
        type: data.type,
        priceAmount: Number(data.priceAmount),
        createdBy: data.createdBy,
        createdOn: data.createdOn,
      });

      const res = response.data;

      if (res.success === false) {
        alert(res.message);
        return false;
      }

      alert(res.message || "Price updated successfully");
      return true;
    } catch (err) {
      console.error("Update Price Error:", err.response?.data || err);
      alert(
        err.response?.data?.message ||
        "Failed to update price"
      );
      return false;
    }
  };

  return (
    <CommonEditPage
      title="Edit Price"
      identifierParam="identifier"
      fetchApi={fetchPrice}
      updateApi={handleUpdate}
      redirectRoute="/price/list"
      submitButtonText="Update Price"
      fields={[
        {
          label: "Identifier",
          name: "identifier",
          type: "text",
          readOnly: true,
        },
        {
          label: "Product",
          name: "product",
          type: "text",
          readOnly: true,
        },
        {
          label: "Price",
          name: "priceAmount",
          type: "number",
        },
        {
          label: "Type",
          name: "type",
          type: "text",
          readOnly: true,
        },
      ]}
    />
  );
}