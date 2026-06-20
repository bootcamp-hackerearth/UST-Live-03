"use client";
import CrudPage from "@/components/CrudPage";

export default function UnitsPage() {
  return (
    <CrudPage
      config={{
        title: "Unit Management",
        singularTitle: "Unit",
        listEndpoint: "/api/units/list",
        getEndpoint: (id) => `/api/units/${id}`,
        saveEndpoint: "/api/units/save",
        updateEndpoint: (id) => `/api/units/update/${id}`,
        deleteEndpoint: (id) => `/api/units/delete/${id}`,
        toggleEndpoint: (id) => `/api/units/toggle/${id}`,
        idKey: "identifier",
        fields: [
          {
            key: "identifier",
            label: "Unit Name",
            type: "text",
            required: true,
            readOnlyOnEdit: true,
          },
          {
            key: "createdBy",
            label: "Created By",
            type: "text",
            hideInForm: true,
            hideInList: true,
          },
          {
            key: "createdAt",
            label: "Created At",
            type: "text",
            hideInForm: true,
            hideInList: true,
          },
          {
            key: "modifiedBy",
            label: "Modified By",
            type: "text",
            hideInForm: true,
            hideInList: true,
          },
          {
            key: "modifiedAt",
            label: "Modified At",
            type: "text",
            hideInForm: true,
            hideInList: true,
          },
        ],
      }}
    />
  );
}
