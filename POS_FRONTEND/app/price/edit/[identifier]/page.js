"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";

export default function PriceEditPage() {
  return (
    <CommonEditPage
      title="Edit Price"

      fetchApi={async (identifier) => {
        const res = await api.get("/api/price/get", {
          params: { identifier },
        });
        return res;
      }}

      updateApi={async (data) => {
        return await api.post("/api/price/update", data);
      }}

      redirectRoute="/price/list"
      identifierParam="identifier"

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
          type: "dropdown",
          api: "/api/product/list",
          payload: {
            page: 0,
            sizePerPage: 100,
            sortField: "identifier",
            sortDirection: "ASC",
          },
          optionLabel: "identifier",
          optionValue: "identifier",
          placeholder: "Select Product",
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
          type: "dropdown",
          options: [
            { identifier: "MRP" },
            { identifier: "SELLING" },
          ],
          optionLabel: "identifier",
          optionValue: "identifier",
          placeholder: "Select Type",
          readOnly: true, 
        },
      ]}
    />
  );
}
