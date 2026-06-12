import { STATUS_FIELD } from "../lib/fieldUtils";

export const productFields = [
  { key: "productName", label: "Product Name", type: "text", placeholder: "Enter product name", required: true },
  { key: "brand", label: "Brand", type: "select", apiEndpoint: "/brand/findAllActive", required: true },
  { key: "model", label: "Model", type: "select", apiEndpoint: "/models/findAllActive", required: true },
  { key: "category", label: "Category", type: "multiselect", apiEndpoint: "/category/findActiveSubCategories", asArray: true, required: true },
  STATUS_FIELD,
];