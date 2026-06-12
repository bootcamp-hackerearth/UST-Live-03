export const ENTITY_CONFIG = {

  user: {
    endpoint: "/api/user/list",
    addEndpoint: "/api/user/register",
    updateEndpoint: "/api/user/update",
    deleteEndpoint: "/api/user/delete",
    getEndpoint: "/api/user/get",
    deleteParam: "username",
    getParam: "username",
    entityName: "User",
    columns: [
      { field: "id", label: "ID" },
      { field: "name", label: "Name" },
      { field: "username", label: "Email" },
      { field: "phoneNo", label: "Phone Number" },
      { field: "roles", label: "Roles" }
    ],
    formFields: [
      { name: "name", label: "User name", type: "text", required: true},
      { name: "username", label: "Email", type: "email", required: true, editable:false },
      { name: "phoneNo", label: "Phone Number", type: "tel", required: true, },
      { name: "roles", label: "Roles", type: "multiselect", optionsEndpoint: "/api/role/list", optionsMethod: "POST", optionsPayload: {
          page: 0,
          sizePerPage: 100,
          sortField: "id",  
          sortDirection: "ASC",
        }, required: true,},
      { name: "password", label: "Password", type: "text", required: true, hideInEdit: true }
    ]
  },


  role: {
    endpoint: "/api/role/list",
    addEndpoint: "/api/role/add",
    updateEndpoint: "/api/role/update",
    deleteEndpoint: "/api/role/delete",
    getEndpoint: "/api/role/get",
    getParam: "identifier",
    entityName: "Role", 
    columns: [
      { field: "id", label: "ID" },
      { field: "identifier", label: "Role" },
      { field: "description", label: "Description" },
    ],
    formFields: [
      { name: "identifier", label: "Role Name", type: "text", required: true, editable:false },
      { name: "description", label: "Description", type: "textarea", required: true, },
    ]
  },


  node: {
    endpoint: "/api/node/list",
    addEndpoint: "/api/node/add",
    updateEndpoint: "/api/node/update",
    deleteEndpoint: "/api/node/delete",
    getEndpoint: "/api/node/get",
    getParam: "identifier",
    columns: [
      { field: "id", label: "ID" },
      { field: "identifier", label: "Node Name" },
      { field: "path", label: "Path" }
    ],
    formFields: [
      { name: "identifier", label: "Node Name", type: "text", required: true, editable:false },
      { name: "path", label: "Node Path", type: "text", required: true, },
      {
        name: "roles", label: "Roles", type: "multiselect", optionsEndpoint: "/api/role/list", optionsMethod: "POST", optionsPayload: {
          page: 0,
          sizePerPage: 100,
          sortField: "id",
          sortDirection: "ASC",
        }, required: true,
      },
    ]
  },


  unit: {
    endpoint: "/api/unit/list",
    toggleEndpoint: "/api/unit/toggle",
    addEndpoint: "/api/unit/add",
    updateEndpoint: "/api/unit/update",
    deleteEndpoint: "/api/unit/delete",
    getEndpoint: "/api/unit/get",
    getParam: "identifier",
    entityName: "Unit",
    columns: [
      { field: "id", label: "ID" },
      { field: "identifier", label: "Unit Name" },
      { field: "description", label: "Description" },
      { field: "status", label: "Status" },
    ],
    formFields: [
      { name: "identifier", label: "Unit Name", type: "text", required: true ,editable:false},
      { name: "description", label: "Description", type: "textarea", required: true },
      { name: "status", label: "Status", type: "switch", defaultValue: 1 }
    ]
  },


  product: {
    endpoint: "/api/product/list",
    addEndpoint: "/api/product/add",
    updateEndpoint: "/api/product/update",
    deleteEndpoint: "/api/product/delete",
    getEndpoint: "/api/product/get",
    getParam: "identifier",
    columns: [
      { field: "id", label: "ID" },
      { field: "identifier", label: "Identifier" },
      { field: "name", label: "Product Name" },
      { field: "brand", label: "Brand" },
      { field: "model", label: "Model" },
      { field: "category", label: "Category" },
      { field: "unit", label: "Unit" },
    ],
    formFields: [
      { name: "identifier", label: "Identifier", type: "text", required: true, editable:false},
      { name: "name", label: "Product Name", type: "text", required: true, editable:false },
      { name: "brand", label: "Brand", type: "select", optionsEndpoint: "/api/brand/active", required: true,},
      { name: "model", label: "Model", type: "select", optionsEndpoint: "/api/models/active", required: true, },
      { name: "category", label: "Category", type: "multiselect", optionsEndpoint: "/api/category/active", required: true, },
      { name: "unit", label: "Unit", type: "select", optionsEndpoint: "/api/unit/active", required: true, }
    ]
  },


  category: {
    endpoint: "/api/category/list",
    addEndpoint: "/api/category/add",
    updateEndpoint: "/api/category/update",
    deleteEndpoint: "/api/category/delete",
    getEndpoint: "/api/category/get",
    getParam: "identifier",
    columns: [
      { field: "id", label: "ID" },
      { field: "identifier", label: "Category" },
      { field: "superCategory", label: "Super Category" }
    ],
    formFields: [
      { name: "identifier", label: "Category Name", type: "text", required: true , editable:false},
      { name: "superCategory", label: "Super Category", type: "select", optionsEndpoint: "/api/category/list", optionsMethod: "POST", optionsPayload: {
          page: 0,
          sizePerPage: 100,
          sortField: "id",
          sortDirection: "ASC",
        },
      },
    ]
  },


  price: {
    endpoint: "/api/price/list",
    addEndpoint: "/api/price/add",
    updateEndpoint: "/api/price/update",
    deleteEndpoint: "/api/price/delete",
    getEndpoint: "/api/price/get",
    getParam: "identifier",
    entityName: "Price",
    columns: [
      { field: "id", label: "ID" },
      { field: "identifier", label: "Identifier" },
      { field: "product", label: "Product" },
      { field: "priceAmount", label: "Price Amount" },
      { field: "priceType", label: "Price Type" },
    ],
    formFields: [
      { name: "product", label: "Product", type: "select", optionsEndpoint: "/api/product/active", required: true, editable:false },
      { name: "priceAmount", label: "Price Amount", type: "textarea", required: true },
      { name: "priceType", label: "Price Type", type: "textarea", required: true },
    ]
  },


  brand: {
    endpoint: "/api/brand/list",
    toggleEndpoint: "/api/brand/toggle",
    addEndpoint: "/api/brand/add",
    updateEndpoint: "/api/brand/update",
    deleteEndpoint: "/api/brand/delete",
    getEndpoint: "/api/brand/get",
    getParam: "identifier",
    entityName: "Brand",
    columns: [
      { field: "id", label: "ID" },
      { field: "identifier", label: "Brand Name" },
      { field: "description", label: "Description" },
      { field: "status", label: "Status" }
    ],
    formFields: [
      { name: "identifier", label: "Brand Name", type: "text", required: true ,editable:false},
      { name: "description", label: "Description", type: "textarea", required: true },
      { name: "status", label: "Status", type: "switch", defaultValue: 1 }
    ]
  }
};