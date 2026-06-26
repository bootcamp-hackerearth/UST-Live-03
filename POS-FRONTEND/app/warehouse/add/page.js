"use client";

import CommonAddPage from "@/app/components/CommonAddPage";
import api from "@/app/services/api";

export default function AddWarehousePage() {
  const handleSubmit = async (data) => {
    try {
      const response = await api.post("/api/warehouse/add", {
        identifier: data.identifier,
        location: data.location,
        capacity: Number(data.capacity),
        status: data.status === true || data.status === "true",
      });

      if (response.data?.success === false) {
        alert(response.data.message);
        return false;
      }

      alert("Warehouse added successfully");
      return true;
    } catch (error) {
      console.error(error);
      alert("Server error");
      return false;
    }
  };

  return (
    <CommonAddPage
      title="Add Warehouse"
      submitApi={handleSubmit}
      redirectRoute="/warehouse/list"
      initialValues={{
        identifier: "",
        location: "",
        capacity: "",
        status: true,
      }}
      fields={[
        {
          label: "Warehouse Name",
          name: "identifier",
          type: "text",
          required: true,
        },
        {
          label: "Location",
          name: "location",
          type: "text",
          required: true,
        },
        {
          label: "Capacity",
          name: "capacity",
          type: "number",
          required: true,
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