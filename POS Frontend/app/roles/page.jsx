"use client";
import CrudPage from "@/components/CrudPage";

export default function RolesPage() {
  return (
    <CrudPage
      config={{
        title: "Role Management",
        singularTitle: "Role",
        listEndpoint: "/api/role/list",
        getEndpoint: (id) => `/api/role/${id}`,
        saveEndpoint: "/api/role/save",
        updateEndpoint: (id) => `/api/role/update/${id}`,
        deleteEndpoint: (id) => `/api/role/delete/${id}`,
        idKey: "identifier",
        fields: [
          { key: "id", label: "ID", hideInForm: true },
          {
            key: "identifier",
            label: "Role Name",
            type: "text",
            required: true,
            readOnlyOnEdit: true,
          },
          { key: "description", label: "Description", type: "textarea" },
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
