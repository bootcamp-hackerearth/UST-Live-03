"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
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

export default function RackEditPage() {

  return (
    <CommonEditPage
      title="Edit Rack"

      fetchApi={async (identifier) => {
        const res = await api.get("/api/rack/get", {
          params: { identifier },
        });

        const data = res.data?.data ?? res.data;

        let shelfs;
        if (Array.isArray(data.shelfs)) {
          shelfs = data.shelfs;
        } else if (data.shelfs) {
          shelfs = String(data.shelfs).split(",").map((s) => s.trim());
        } else {
          shelfs = [];
        }

        return {
          data: {
            ...data,
            shelfs,
            status: data.status ? "true" : "false", 
          },
        };
      }}
      updateApi={async (data) => {
        return await api.put("/api/rack/update", {
          ...data,

          shelfs: Array.isArray(data.shelfs)
            ? data.shelfs
            : [],

          status: data.status === true || data.status === "true",
        });
      }}

      redirectRoute="/rack/list"
      identifierParam="identifier"

      fields={[
        {
          label: "Rack Name",
          name: "identifier",
          type: "text",
          readOnly: true,
        },

        dropdown("Shelf", "shelfs", "/api/shelf/list", {
          multiple: true,
        }),

        {
          label: "Status",
          name: "status",
          type: "radio",
          options: [
            { label: "Active", value: "true" },
            { label: "Inactive", value: "false" },
          ],
        },
      ]}
    />
  );
}