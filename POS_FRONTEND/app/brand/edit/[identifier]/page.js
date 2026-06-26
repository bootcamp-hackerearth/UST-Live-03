"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";

export default function EditBrandPage() {
  const fields = [
    { label: "Identifier", name: "identifier", type: "text", readOnly: true },
    { label: "description", name: "description", type: "text" },
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

  const fetchBrand = async (identifier) => {
    const res = await api.get("/api/brand/get", { params: { identifier } });
    return res.data;
  };

  return (
    <CommonEditPage
      title="Edit Brand"
      fetchApi={fetchBrand}
      updateApi="/api/brand/update"
      redirectRoute="/brand/list"
      fields={fields}
      identifierParam="identifier"
      initialValues={{ identifier: "", status: "true" }}
    />
  );
}