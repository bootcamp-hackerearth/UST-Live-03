import { STATUS_FIELD } from "../lib/fieldUtils";

export const priceFields = [
  { key: "productName", label: "Product Name", type: "select", apiEndpoint: "/product/findAllActive", optionValue: "productName", optionLabel: "productName", required: true },
  { key: "costPrice", label: "Cost Price", type: "number", placeholder: "Enter cost price", required: true },
  { key: "sellingPrice", label: "Selling Price", type: "number", placeholder: "Enter selling price", required: true },
  { key: "mrp", label: "MRP", type: "number", placeholder: "Enter MRP", required: true },
  STATUS_FIELD,
];