export const category = {
  endpoint: "/api/category/list",
  addEndpoint: "/api/category/add",
  updateEndpoint: "/api/category/update",
  deleteEndpoint: "/api/category/delete",
  getEndpoint: "/api/category/get",
  getParam: "identifier",
  columns: [
    { field: "serialNumber", label: "S.No" },
    { field: "identifier", label: "Category" },
    { field: "superCategory", label: "Super Category" }
  ],
  formFields: [
    { name: "identifier", label: "Category Name", type: "text", required: true, editable: false },
    {
      name: "superCategory", label: "Super Category", type: "select", optionsEndpoint: "/api/category/list", optionsMethod: "POST", 
      optionsPayload: {page: 0, sizePerPage: 100, sortField: "id", sortDirection: "ASC",}
    }
  ]
};