"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";

export default function EditWarehousePage() {
  const fields = [
    { label: "Warehouse Name", name: "identifier", type: "text", readOnly: true },
    { label: "Location", name: "location", type: "text" },
    { label: "Capacity", name: "capacity", type: "number" },
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

const fetchWarehouse = async (identifier) => {
  try {
    const res = await api.get("/api/warehouse/get", { params: { identifier } });
    return res.data;
  } catch (error) {
    if (error.response?.status === 404) {
      return null; 
    }
    throw error; 
  }
};

  return (
    <CommonEditPage
      title="Edit Warehouse"
      fetchApi={fetchWarehouse}
      updateApi="/api/warehouse/update"
      redirectRoute="/warehouse/list"
      fields={fields}
      identifierParam="identifier"
    />
  );
}