"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";

export default function BrandEditPage() {
  return (
    <CommonEditPage
      title="Edit Brand"
      fetchApi={async (identifier) => {
        const res = await api.get("/api/brand/get", {
          params: { identifier },
        });
        return res;
      }}
      updateApi={async (data) => {
        return await api.put("/api/brand/update", data);
      }}
      redirectRoute="/brand/list"
      identifierParam="identifier"
      fields={[
        {
          label: "Brand Name",
          name: "identifier",
          type: "text",
          readOnly: true,
        },
        {
          label: "Description",
          name: "description",
          type: "text",
        },
      ]}
    />
  );
}