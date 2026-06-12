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

export default function PriceAddPage() {
  return (
    <CommonAddPage
      title="Add Price"

      submitApi={(data) =>
        api.post("/api/price/add", {
          ...data,

          identifier: undefined, 
        })
      }

      redirectRoute="/price/list"

      initialValues={{
        identifier: "", 
        product: "",
        priceAmount: "",
        type: "",
        status: true,
      }}

      fields={[
        
        dropdown("Product", "product", "/api/product/list"),

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
