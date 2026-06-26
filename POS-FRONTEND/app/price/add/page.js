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
    status: true,
  },
  optionLabel: "identifier",
  optionValue: "identifier",
  placeholder: `Select ${label}`,
  ...extra,
});

export default function PriceAddPage() {
  const handleSubmit = async (data) => {
    try {
      const payload = {
        product:
          typeof data.product === "object"
            ? data.product.identifier
            : data.product,

        type:
          typeof data.type === "object"
            ? data.type.identifier
            : data.type,

        priceAmount: Number(data.priceAmount),
      };

      console.log("Submitting Payload:", payload);

      const response = await api.post(
        "/api/price/add",
        payload
      );

      const res = response.data;

      if (res.success === false) {
        alert(res.message);
        return false;
      }

      alert(res.message || "Price added successfully");
      return true;
    } catch (error) {
      console.error("PRICE SAVE ERROR:", error.response?.data || error);

      alert(
        error.response?.data?.message ||
          "Failed to save price"
      );

      return false;
    }
  };

  return (
    <CommonAddPage
      title="Add Price"
      submitApi={handleSubmit}
      redirectRoute="/price/list"
      submitButtonText="Save Price"
      initialValues={{
        product: "",
        priceAmount: "",
        type: "",
      }}
      fields={[
        dropdown(
          "Product",
          "product",
          "/api/product/findallactive"
        ),

        {
          label: "Price",
          name: "priceAmount",
          type: "number",
        },

        {
          label: "Price Type",
          name: "type",
          type: "dropdown",
          options: [
            { identifier: "MRP" },
            { identifier: "SELLING" },
          ],
          optionLabel: "identifier",
          optionValue: "identifier",
          placeholder: "Select Price Type",
        },
      ]}
    />
  );
}