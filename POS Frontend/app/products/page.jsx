"use client";
import CrudPage from "@/components/CrudPage";
import { fetchWithAuth } from "@/lib/api";

export default function ProductsPage() {
  return (
    <CrudPage
      config={{
        title: "Product Management",
        singularTitle: "Product",
        listEndpoint: "/api/products/list",
        getEndpoint: (id) => `/api/products/${id}`,
        saveEndpoint: "/api/products/save",
        updateEndpoint: (id) => `/api/products/update/${id}`,
        deleteEndpoint: (id) => `/api/products/delete/${id}`,
        toggleEndpoint: (id) => `/api/products/toggle/${id}`,
        idKey: "identifier",
        loadOptions: async () => {
          const [cats, brands, models, units] = await Promise.all([
            fetchWithAuth("/api/categories/active"),
            fetchWithAuth("/api/brands/active"),
            fetchWithAuth("/api/models/active"),
            fetchWithAuth("/api/units/active"),
          ]);
          const toOpts = (arr) =>
            arr.map((x) => ({ value: x.identifier, label: x.identifier }));
          return {
            category: toOpts(cats),
            brand: toOpts(brands),
            model: toOpts(models),
            unit: toOpts(units),
          };
        },
        fields: [
          {
            key: "productName",
            label: "Product Name",
            type: "text",
            required: true,
          },
          {
            key: "identifier",
            label: "SKU Code",
            type: "text",
            required: true,
            readOnlyOnEdit: true,
          },
          {
            key: "category",
            label: "Category",
            type: "select",
            multiple: true,
            options: [],
          },
          {
            key: "brand",
            label: "Brand",
            type: "select",
            multiple: true,
            options: [],
          },
          {
            key: "model",
            label: "Model",
            type: "select",
            multiple: true,
            options: [],
          },
          {
            key: "unit",
            label: "Unit",
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
