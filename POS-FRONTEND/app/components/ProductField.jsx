export const productFields = [
  {
    name: "identifier",
    label: "Product Name",
    type: "text",
  },
  {
    name: "category",
    label: "Category",
    type: "multiDropdown",
    api: "/category/list-active",
  },
  {
    name: "models",
    label: "Model",
    type: "singleDropdown",
    api: "/models/list-active",
  },
  {
    name: "brand",
    label: "Brand",
    type: "singleDropdown",
    api: "/brand/list-active",
  },
  {
    name: "unit",
    label: "Unit",
    type: "singleDropdown",
    api: "/unit/list-active",
  },
];