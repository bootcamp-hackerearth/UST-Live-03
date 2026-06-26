"use client";

import CommonAddPage from "@/app/components/CommonAddPage";
import api from "@/app/services/api";

export default function ModelAddPage() {
  const handleSubmit = async (data) => {
    try {
      const response = await api.post("/api/model/add", {
        identifier: data.identifier,
        status: data.status,
      });

      if (response.data?.success === false) {
        alert(response.data.message);
        return false;
      }

      alert("Model added successfully");
      return true;
    } catch (error) {
      console.error(error);
      alert("Server error");
      return false;
    }
  };

  return (
    <CommonAddPage
      title="Add Model"
      submitApi={handleSubmit}
      redirectRoute="/model/list"
      initialValues={{
        identifier: "",
        status: true,
      }}
      fields={[
        {
          label: "Model Name",
          name: "identifier",
          type: "text",
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