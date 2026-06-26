export const getStandardColumns = (identifierLabel) => [
  { field: "serialNumber", label: "S.No" },
  { field: "identifier", label: identifierLabel },
  { field: "description", label: "Description" },
  { field: "status", label: "Status" }
];

export const getStandardFormFields = (identifierLabel) => [
  { name: "identifier", label: identifierLabel, type: "text", required: true, editable: false },
  { name: "description", label: "Description", type: "textarea", required: true },
  { name: "status", label: "Status", type: "switch", defaultValue: 1 }
];

export const buildEndpoints = (base) => ({
  endpoint: `${base}/list`,
  toggleEndpoint: `${base}/toggle`,
  addEndpoint: `${base}/add`,
  updateEndpoint: `${base}/update`,
  deleteEndpoint: `${base}/delete`,
  getEndpoint: `${base}/get`,
  getParam: "identifier"
});