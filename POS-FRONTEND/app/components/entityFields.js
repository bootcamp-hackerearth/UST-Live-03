export const DEFAULT_ENTITY_VALUES = {
  identifier: "",
  description: "",
  status: true,
};

export const STATUS_OPTIONS = [
  { label: "Active", value: true },
  { label: "Inactive", value: false },
];

export const getEntityFields = (entityName) => [
  {
    label: `${entityName} Name`,
    name: "identifier",
    type: "text",
    required: true,
  },
  {
    label: "Description",
    name: "description",
    type: "text",
  },
  {
    label: "Status",
    name: "status",
    type: "radio",
    options: STATUS_OPTIONS,
  },
];