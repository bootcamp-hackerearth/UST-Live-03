import { buildEndpoints } from "./baseSchema";

export const product = {
  ...buildEndpoints("/api/product"),

  columns: [
    { field: "serialNumber", label: "S.No" },
    { field: "identifier", label: "Identifier" },
    { field: "name", label: "Product Name" },
    { field: "brand", label: "Brand" },
    { field: "model", label: "Model" },
    { field: "category", label: "Category" },
    { field: "unit", label: "Unit" },
    { field: "status", label: "Status" }
  ],

  formFields: [
    { name: "identifier", label: "Identifier", type: "text", required: true, editable: false },
    { name: "name", label: "Product Name", type: "text", required: true, editable: false },
    { name: "brand", label: "Brand", type: "select", optionsEndpoint: "/api/brand/active", required: true },
    { name: "model", label: "Model", type: "select", optionsEndpoint: "/api/models/active", required: true },
    { name: "category", label: "Category", type: "multiselect", optionsEndpoint: "/api/category/active", required: true },
    { name: "unit", label: "Unit", type: "select", optionsEndpoint: "/api/unit/active", required: true },
    { name: "status", label: "Status", type: "switch", defaultValue: 1 }
  ]
};