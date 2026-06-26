"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";

export default function EditShelfPage() {
  return (
    <CommonEditPage
      title="Edit Shelf"
      fetchApi={async (identifier) => {
        return await api.get("/api/shelf/get", {
          params: { identifier },
        });
      }}
      updateApi={async (data) => {
        return await api.put("/api/shelf/update", {
          ...data,
          status: data.status === true || data.status === "true",
        });
      }}
      redirectRoute="/shelf/list"
      identifierParam="identifier"
      submitButtonText="Update Shelf"
      fields={[
        {
          label: "Shelf Name",
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