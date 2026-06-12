"use client";
import CrudPage from "../../components/CrudPage";
import { fetchWithAuth } from "../../lib/api";

const beforeDelete = async (record) => {
  if (record.superCategory) return null; // sub-categories can always be deleted
  const data = await fetchWithAuth("/api/categories/list", {
    method: "POST",
    body: JSON.stringify({ page: 0, sizePerPage: 200 }),
  });
  const list = data?.dtoList ?? data?.content ?? [];
  return list.some((c) => c.superCategory === record.identifier)
    ? `Cannot delete "${record.identifier}" — it has sub-categories. Delete or reassign them first.`
    : null;
};

export default function CategoriesPage() {
  return (
    <CrudPage
      config={{
        title: "Category Management",
        singularTitle: "Category",
        listEndpoint: "/api/categories/list",
        getEndpoint: (id) => `/api/categories/${id}`,
        saveEndpoint: "/api/categories/save",
        updateEndpoint: (id) => `/api/categories/update/${id}`,
        deleteEndpoint: (id) => `/api/categories/delete/${id}`,
        toggleEndpoint: (id) => `/api/categories/toggle/${id}`,
        idKey: "identifier",
        beforeDelete,
        showToggle: (r) => !!r.superCategory,
        loadOptions: async () => {
          const data = await fetchWithAuth("/api/categories/super-categories");
          return {
            superCategory: data.map((c) => ({
              value: c.identifier,
              label: c.identifier,
            })),
          };
        },
        fields: [
          {
            key: "identifier",
            label: "Category Name",
            type: "text",
            required: true,
            readOnlyOnEdit: true,
          },
          {
            key: "superCategory",
            label: "Super Category",
            type: "select",
            placeholder: "-- No Super Category --",
            options: [],
          },
        ],
      }}
    />
  );
}
