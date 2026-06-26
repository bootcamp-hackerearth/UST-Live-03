"use client";

import CommonAddPage from "@/app/components/CommonAddPage";
import api from "@/app/services/api";

export default function AddBrandPage() {

  const handleSubmit = async (data) => {
  try {
    const response = await api.post("/api/brand/add", {
      identifier: data.identifier,
      description: data.description,
      status: data.status === true || data.status === "true",
    });

    console.log("BRAND RESPONSE:", response.data);

    if (!response.data?.success) {
      alert(response.data?.message || "Failed to add brand");
      return false;
    }

    alert(response.data.message || "Brand added successfully");
    return true;

  } catch (error) {
    console.error("BRAND ERROR:", error);

    alert(
      error.response?.data?.message ||
      error.message ||
      "Server Error"
    );

    return false;
  }
};

  return (
    <CommonAddPage
      title="Add Brand"
      submitApi={handleSubmit}
      redirectRoute="/brand/list"
      submitButtonText="Save Brand"
      initialValues={{
        identifier: "",
        description: "",
      }}
      fields={[
        {
          label: "Brand Name",
          name: "identifier",
          type: "text",
          required: true,
        },
        {
          label: "Description",
          name: "description",
          type: "text",
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