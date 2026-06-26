"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";

export default function ModelEditPage() {
  return (
    <CommonEditPage
      title="Edit Model"
      fetchApi={async (identifier) => {
        return await api.get("/api/model/get", {
          params: { identifier },
        });
      }}
      updateApi={async (data) => {
        return await api.put("/api/model/update", data);
      }}
      redirectRoute="/model/list"
      identifierParam="identifier"
      fields={[
        {
          label: "Model Name",
          name: "identifier",
          type: "text",
          readOnly: true,
        },
        {
          label: "Status",
          name: "status",
          type: "radio",
          options: [
            { label: "Active", value: true },
            { label: "Inactive", value: false },
          ],
        },
      ]}
    />
  );
}