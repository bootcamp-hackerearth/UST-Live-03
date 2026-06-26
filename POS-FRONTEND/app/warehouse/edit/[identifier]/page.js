"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";

export default function EditWarehousePage() {
  const fetchWarehouse = async (identifier) => {
    const response = await api.get("/api/warehouse/get", {
      params: { identifier },
    });

    return response;
  };

  const updateWarehouse = async (data) => {
    try {
      console.log("UPDATE REQUEST:", data);

      const response = await api.put("/api/warehouse/update", {
        identifier: data.identifier,
        location: data.location,
        capacity: Number(data.capacity),
        status:
          data.status === true ||
          data.status === "true" ||
          data.status === "ACTIVE",
      });

      console.log("UPDATE RESPONSE:", response.data);

      alert("Warehouse updated successfully");
      return response;
    } catch (error) {
      console.error("UPDATE ERROR:", error);
      alert(
        error.response?.data?.message ||
          error.message ||
          "Failed to update warehouse"
      );
      return false;
    }
  };

  return (
    <CommonEditPage
      title="Edit Warehouse"
      fetchApi={fetchWarehouse}
      updateApi={updateWarehouse}
      redirectRoute="/warehouse/list"
      identifierParam="identifier"
      submitButtonText="Update Warehouse"
      fields={[
        {
          label: "Warehouse Name",
          name: "identifier",
          type: "text",
          readOnly: true,
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
            {
              label: "Active",
              value: true,
            },
            {
              label: "Inactive",
              value: false,
            },
          ],
        },
      ]}
    />
  );
}