export const user = {
  endpoint: "/api/user/list",
  addEndpoint: "/api/user/register",
  updateEndpoint: "/api/user/update",
  deleteEndpoint: "/api/user/delete",
  getEndpoint: "/api/user/get",
  deleteParam: "username",
  getParam: "username",
  entityName: "User",
  columns: [
    { field: "serialNumber", label: "S.No" },
    { field: "name", label: "Name" },
    { field: "username", label: "Email" },
    { field: "phoneNo", label: "Phone Number" },
    { field: "roles", label: "Roles" }
  ],
  formFields: [
    { name: "name", label: "User name", type: "text", required: true },
    { name: "username", label: "Email", type: "email", required: true, },
    { name: "phoneNo", label: "Phone Number", type: "tel", required: true },
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
    },
    { name: "password", label: "Password", type: "password", required: true, hideInEdit: true }
  ]
};