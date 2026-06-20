"use client";
import CrudPage from "@/components/CrudPage";
import { fetchWithAuth } from "../../lib/api";

export default function NodesPage() {
  return (
    <CrudPage
      config={{
        title: "Node Management",
        singularTitle: "Node",
        listEndpoint: "/api/nodes/list",
        getEndpoint: (id) => `/api/nodes/${id}`,
        saveEndpoint: "/api/nodes/save",
        updateEndpoint: (id) => `/api/nodes/update/${id}`,
        deleteEndpoint: (id) => `/api/nodes/delete/${id}`,
        // No toggleEndpoint — nodes don't have a status toggle
        idKey: "identifier",
        loadOptions: async () => {
          const data = await fetchWithAuth("/api/role/list", {
            method: "POST",
            body: JSON.stringify({ page: 0, sizePerPage: 100 }),
          });
          const list = Array.isArray(data) ? data : (data?.dtoList ?? []);
          return {
            roles: list.map((r) => ({
              value: r.identifier,
              label: r.identifier,
            })),
          };
        },
        fields: [
          {
            key: "identifier",
            label: "Node Name",
            type: "text",
            required: true,
            readOnlyOnEdit: true,
          },
          { key: "path", label: "Path", type: "text", required: true },
          {
            key: "roles",
            label: "Roles",
            type: "select",
            multiple: true,
            options: [],
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
