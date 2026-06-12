"use client";
import CrudPage from "../../components/CrudPage";
import { fetchWithAuth } from "../../lib/api";

export default function PricesPage() {
  return (
    <CrudPage
      config={{
        title: "Price Management",
        singularTitle: "Price",
        listEndpoint: "/api/prices/list",
        getEndpoint: (id) => `/api/prices/${id}`,
        saveEndpoint: "/api/prices/save",
        updateEndpoint: (id) => `/api/prices/update/${id}`,
        deleteEndpoint: (id) => `/api/prices/delete/${id}`,
        idKey: "id",
        currencyFields: ["mrpPrice", "sellingPrice", "costPrice"],
        loadOptions: async () => {
          const data = await fetchWithAuth("/api/products/active");
          return {
            productId: data.map((p) => ({
              value: p.id,
              label: `${p.identifier} — ${p.productName}`,
            })),
          };
        },
        fields: [
          { key: "identifier", label: "SKU Code", hideInForm: true },
          { key: "productName", label: "Product Name", hideInForm: true },
          {
            key: "mrpPrice",
            label: "MRP Price (₹)",
            type: "number",
            min: "0",
            required: true,
          },
          {
            key: "sellingPrice",
            label: "Selling Price (₹)",
            type: "number",
            min: "0",
            required: true,
          },
          {
            key: "costPrice",
            label: "Cost Price (₹)",
            type: "number",
            min: "0",
            required: true,
          },
          {
            key: "productId",
            label: "Product",
            type: "select",
            required: true,
            placeholder: "-- Select Product --",
            options: [],
            hideInList: true,
          },
        ],
      }}
    />
  );
}
