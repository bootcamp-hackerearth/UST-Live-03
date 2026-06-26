"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";

export default function EditShelfPage() {


  const fields = [
    { label: "Shelf Name", name: "identifier", type: "text", readOnly: true },
    { 
      label: "Status", 
      name: "status", 
      type: "radio", 
      options: [
        { label: "Active", value: true },
        { label: "Inactive", value: false }
      ] 
    }
  ];

  const fetchShelf = async (identifier) => {
    const res = await api.get("/api/shelf/get", { params: { identifier } });
    return res.data;
  };

  return (
    <CommonEditPage
      title="Edit Shelf"
      fetchApi={fetchShelf}
      updateApi="/api/shelf/update"
      redirectRoute="/shelf/list"
      fields={fields}
      identifierParam="identifier"
      
    />
  );
}