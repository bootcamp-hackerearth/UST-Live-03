import { buildEndpoints } from "./baseSchema";

export const stock = {
  ...buildEndpoints("/api/stock"),

  entityName: "Stock",

  columns: [
    { field: "id", label: "ID" },
    { field: "identifier", label: "Stock Identifier" },
    { field: "product", label: "Product" },
    { field: "minimumStock", label: "Minimum Stock" },
    { field: "quantity", label: "Quantity" },
    { field: "stockStatus", label: "Stock Status" },
    { field: "warehouse", label: "Warehouse" },
  ],

  formFields: [
    { name: "product", label: "Product", type: "select", optionsEndpoint: "/api/product/active", required: true, editable: false },
    { name: "minimumStock", label: "Minimum Stock", type: "number", required: true },
    { name: "quantity", label: "Quantity", type: "number", required: true },
    { name: "warehouse", label: "Warehouse", type: "select", optionsMethod: "POST", optionsEndpoint: "/api/warehouse/list", editable: false },
  ]
};