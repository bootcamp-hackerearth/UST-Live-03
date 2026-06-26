import { STATUS_FIELD } from "../lib/fieldUtils";

export const stockFields = [
  { key: "product", label: "Product", type: "select", apiEndpoint: "/product/findAllActive", optionValue: "identifier", optionLabel: "productName", required: true },
  { key: "warehouse", label: "Warehouse", type: "multiselect", apiEndpoint: "/warehouse/findAllActiveWarehouse", optionValue: "identifier", optionLabel: "identifier", asArray: true, required: true },
  { key: "quantity", label: "Quantity", type: "number", placeholder: "Enter quantity", required: true },
  STATUS_FIELD,
];