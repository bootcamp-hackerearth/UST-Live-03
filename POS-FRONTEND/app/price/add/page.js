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
  return (
    <CommonAddPage
      title="Add Price"

      submitApi={(data) => {
        console.log("Submitting Price:", data);

        return api.post("/api/price/add", {
          product: data.product,
          priceAmount: Number(data.priceAmount),
          type: data.type,
          identifier: undefined,
          status: data.status === true || data.status === "true",
        });
      }}

      redirectRoute="/price/list"

      initialValues={{
        product: "",
        priceAmount: "",
        type: "",
        status: true,
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