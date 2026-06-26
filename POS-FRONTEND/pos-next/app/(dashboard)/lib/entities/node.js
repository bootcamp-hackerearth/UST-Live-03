export const node = {
  endpoint: "/api/node/list",
  addEndpoint: "/api/node/add",
  updateEndpoint: "/api/node/update",
  deleteEndpoint: "/api/node/delete",
  getEndpoint: "/api/node/get",
  getParam: "identifier",
  columns: [
    { field: "serialNumber", label: "S.No" },
    { field: "identifier", label: "Node Name" },
    { field: "path", label: "Path" }
  ],
  formFields: [
    { name: "identifier", label: "Node Name", type: "text", required: true, editable: false },
    { name: "path", label: "Node Path", type: "text", required: true },
    {
      name: "roles",
      label: "Roles",
      type: "multiselect",
      optionsEndpoint: "/api/role/list",
      optionsMethod: "POST",
      optionsPayload: {
        page: 0,
        sizePerPage: 100,
        sortField: "id",
        sortDirection: "ASC",
      },
      required: true,
    }
  ]
};