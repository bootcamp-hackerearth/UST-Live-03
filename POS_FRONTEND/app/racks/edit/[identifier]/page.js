"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";

export default function EditRackPage() {


  const fields = [
    { label: "Rack Name", name: "identifier", type: "text", readOnly: true },
   { 
      label: "Select Shelves", 
      name: "shelfs", 
      type: "dropdown",
      api: "/api/shelf/findallactive", 
      multiple: true,
      optionLabel: "identifier",
      optionValue: "identifier",
      placeholder: "Select shelves"
    },
    { 
      label: "Status", name: "status", type: "radio", 
      options: [{ label: "Active", value: true }, { label: "Inactive", value: false }] 
    }
  ];
   const fetchModel = async (identifier) => {
    const res = await api.get("/api/racks/get", { params: { identifier } });
    return res.data;
  };

  return (
    <CommonEditPage
      title="Edit Rack"
      fetchApi={fetchModel}
      updateApi="/api/racks/update"
      redirectRoute="/racks/list"
      fields={fields}
      identifierParam="identifier"
    initialValues={{ status: true, shelfs: [] }}
    />
  );
}