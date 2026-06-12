"use client";
import CrudPage from "../../components/CrudPage";

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
        ],
      }}
    />
  );
}
