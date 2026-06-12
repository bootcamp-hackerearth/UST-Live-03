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
