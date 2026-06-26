export const price = {
  endpoint: "/api/price/list",
  addEndpoint: "/api/price/add",
  updateEndpoint: "/api/price/update",
  deleteEndpoint: "/api/price/delete",
  getEndpoint: "/api/price/get",
  getParam: "identifier",
  entityName: "Price",
  columns: [
    { field: "serialNumber", label: "S.No" },
    { field: "identifier", label: "Identifier" },
    { field: "product", label: "Product" },
    { field: "priceAmount", label: "Price Amount" },
    { field: "priceType", label: "Price Type" }
  ],
  formFields: [
    { name: "product", label: "Product", type: "select", optionsEndpoint: "/api/product/active", required: true, editable: false },
    { name: "priceAmount", label: "Price Amount", type: "number", required: true },
    { name: "priceType", label: "Price Type", type: "textarea", required: true }
  ]
};