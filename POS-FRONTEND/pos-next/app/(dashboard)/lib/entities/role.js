export const role = {
  endpoint: "/api/role/list",
  addEndpoint: "/api/role/add",
  updateEndpoint: "/api/role/update",
  deleteEndpoint: "/api/role/delete",
  getEndpoint: "/api/role/get",
  getParam: "identifier",
  entityName: "Role",
  columns: [
    { field: "serialNumber", label: "S.No" },
    { field: "identifier", label: "Role" },
    { field: "description", label: "Description" }
  ],
  formFields: [
    { name: "identifier", label: "Role Name", type: "text", required: true, editable: false },
    { name: "description", label: "Description", type: "textarea", required: true }
  ]
};