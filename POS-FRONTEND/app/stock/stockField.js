
export const stockFields = [
  {
    name: "productIdentifier",
    label: "Product",
    type: "select",
    api: "product",
    multiple: false,
    optionLabel: "name",
    optionValue: "name",
    required: true,
  },
  {
    name: "warehouseIdentifier",
    label: "Warehouse",
    type: "select",
    api: "warehouse",
    multiple: false,
    optionLabel: "identifier",
    optionValue: "identifier",
    required: true,
  },
  {
    name: "quantity",
    label: "Quantity Available",
    type: "number",
    required: true,
  },
  {
    name: "minimumStock",
    label: "Minimum Stock Level",
    type: "number",
    required: true,
  },
];