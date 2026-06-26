"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";

export default function EditUnitPage() {

  const fetchUnit = async (identifier) => {
  const response = await api.get("/api/unit/get", {
    params: { identifier },
  });

  const data = response.data?.data ?? response.data;

  return {
    data: {
      identifier: data.identifier || "",
      description: data.description || "",

      createdBy: data.createdBy,
      createdOn: data.createdOn,
      modifiedBy: data.modifiedBy,
      modifiedOn: data.modifiedOn,
    },
  };
};

  const handleUpdate = async (data) => {
    try {
      const response = await api.put("/api/unit/update", {
        identifier: data.identifier,
        description: data.description,
      });

      const res = response.data;

      if (res.success === false) {
        alert(res.message);
        return false;
      }

      alert(res.message || "Unit updated successfully");
      return true;
    } catch (err) {
      console.error(err);
      alert("Server error");
      return false;
    }
  };

  return (
    <CommonEditPage
      title="Edit Unit"
      identifierParam="identifier"
      fetchApi={fetchUnit}
      updateApi={handleUpdate}
      redirectRoute="/unit/list"
      submitButtonText="Update Unit"
      fields={[
        {
          label: "Unit Name",
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