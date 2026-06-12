"use client";
import CrudPage from "../../components/CrudPage";

export default function BrandsPage() {
  return (
    <CrudPage
      config={{
        title: "Brand Management",
        singularTitle: "Brand",
        listEndpoint: "/api/brands/list",
        getEndpoint: (id) => `/api/brands/${id}`,
        saveEndpoint: "/api/brands/save",
        updateEndpoint: (id) => `/api/brands/update/${id}`,
        deleteEndpoint: (id) => `/api/brands/delete/${id}`,
        toggleEndpoint: (id) => `/api/brands/toggle/${id}`,
        idKey: "identifier",
        fields: [
          {
            key: "identifier",
            label: "Brand Name",
            type: "text",
            required: true,
            readOnlyOnEdit: true,
          },
          { key: "description", label: "Description", type: "textarea" },
        ],
      }}
    />
  );
}
