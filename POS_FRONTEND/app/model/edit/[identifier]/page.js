"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";

export default function EditModelPage() {
  const fields = [
    { label: "Model Name", name: "identifier", type: "text", readOnly: true },
    { 
      label: "Status", 
      name: "status", 
      type: "radio", 
      options: [
        { label: "Active", value: "true" },
        { label: "Inactive", value: "false" }
      ] 
    }
    
  ];

  const fetchModel = async (identifier) => {
    const res = await api.get("/api/model/get", { params: { identifier } });
    return res.data;
  };

  return (
    <CommonEditPage
      title="Edit Model"
      fetchApi={fetchModel}
      updateApi="/api/model/update"
      redirectRoute="/model/list"
      fields={fields}
      identifierParam="identifier"
    />
  );
}