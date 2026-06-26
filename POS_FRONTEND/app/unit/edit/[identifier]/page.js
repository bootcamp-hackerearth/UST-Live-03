"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";

export default function EditUnitPage() {
  const fields = [
    { label: "Unit Name", name: "identifier", type: "text", readOnly: true },
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

const fetchUnit = async (identifier) => {
  const cleanId = identifier?.trim();
  const res = await api.get(`/api/unit/get`, {
    params: { identifier: cleanId }
  });
  return res.data;
};
  return (
    <CommonEditPage
      title="Edit Unit"
      fetchApi={fetchUnit}
      updateApi="/api/unit/update"
      redirectRoute="/unit/list"
      fields={fields}
      identifierParam="identifier"
    />
  );
}