

const createDropdown = (label, name, apiUrl, extra = {}) => ({
  label,
  name,
  type: "dropdown",
  api: apiUrl,
  payload: {
    page: 0,
    sizePerPage: 100,
    sortField: "identifier",
    sortDirection: "ASC",
  },
  optionLabel: "identifier",
  optionValue: "identifier",
  placeholder: `Select ${label}`,
  ...extra,
});

export const getProductFields = (isEdit = false) => [
  {
    label: "Identifier",
    name: "identifier",
    type: "text",
    readOnly: isEdit,
    ...(isEdit ? {} : { placeholder: "Enter product code" }),
  },

  createDropdown("Category", "category", "/api/category/list"),
  createDropdown("Brand", "brand", "/api/brand/list"),
  createDropdown("Model", "model", "/api/model/list"),
  createDropdown("Unit", "unit", "/api/unit/list"),

  {
    label: "Quantity",
    name: "quantity",
    type: "number",
  },

  {
    label: "Status",
    name: "status",
    type: "radio",
    options: [
      { label: "Active", value: true },
      { label: "Inactive", value: false },
    ],
  },
];
