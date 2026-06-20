"use client";
import CrudPage from "@/components/CrudPage";

export default function ModelsPage() {
  return (
    <CrudPage
      config={{
        title: "Models Management",
        singularTitle: "Model",
        listEndpoint: "/api/models/list",
        getEndpoint: (id) => `/api/models/${id}`,
        saveEndpoint: "/api/models/save",
        updateEndpoint: (id) => `/api/models/update/${id}`,
        deleteEndpoint: (id) => `/api/models/delete/${id}`,
        toggleEndpoint: (id) => `/api/models/toggle/${id}`,
        idKey: "identifier",
        fields: [
          {
            key: "identifier",
            label: "Model Name",
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
