"use client";

import CommonAddPage from "@/app/components/CommonAddPage";
import api from "@/app/services/api";

const dropdown = (label, name, apiUrl, extra = {}) => ({
  label,
  name,
  type: "dropdown",
  api: apiUrl,
  payload: {
    page: 0,
    sizePerPage: 100,
    sortField: "identifier",
    sortDirection: "ASC",
  },
  optionLabel: "identifier",
  optionValue: "identifier",
  placeholder: `Select ${label}`,
  ...extra,
});

export default function RackAddPage() {
  const handleSubmit = async (data) => {
    try {
      console.log("Submitting:", data);

      const response = await api.post("/api/rack/add", {
        identifier: data.identifier,
        shelfs: Array.isArray(data.shelfs) ? data.shelfs : [],
        status: data.status === true || data.status === "true",
      });

      console.log("API RESPONSE:", response.data);

      if (response.data?.success === false) {
        alert(response.data.message);
        return false;
      }

      alert(" Rack added successfully");
      return true;

    } catch (error) {
      console.error("ERROR:", error);
      alert("Server error");
      return false;
    }
  };

  return (
    <CommonAddPage
      title="Add Rack"
      submitApi={handleSubmit}
      redirectRoute="/rack/list"

      initialValues={{
        identifier: "",
        shelfs: [],   
        status: true,
      }}

      fields={[
        {
          label: "Rack Name",
          name: "identifier",
          type: "text",
          required: true,
        },

        dropdown("Shelf", "shelfs", "/api/shelf/list", {
          multiple: true,   
        }),

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
