import { fetchWithAuth } from "@/lib/api";

const auditFields = [
  { key: "createdBy", label: "Created By", type: "text", hideInForm: true, hideInList: true },
  { key: "createdAt", label: "Created At", type: "text", hideInForm: true, hideInList: true },
  { key: "modifiedBy", label: "Modified By", type: "text", hideInForm: true, hideInList: true },
  { key: "modifiedAt", label: "Modified At", type: "text", hideInForm: true, hideInList: true },
];

const makeBaseConfig = (endpoint, singularTitle, additionalFields = []) => ({
  title: `${singularTitle} Management`,
  singularTitle,
  listEndpoint: `/api/${endpoint}/list`,
  getEndpoint: (id) => `/api/${endpoint}/${id}`,
  saveEndpoint: `/api/${endpoint}/save`,
  updateEndpoint: (id) => `/api/${endpoint}/update/${id}`,
  deleteEndpoint: (id) => `/api/${endpoint}/delete/${id}`,
  toggleEndpoint: (id) => `/api/${endpoint}/toggle/${id}`,
  toggleField: "active",
  idKey: "identifier",
  pageSize: 20,
  fields: [...additionalFields, ...auditFields],
});

export const crudConfigs = {
  brands: () => makeBaseConfig("brands", "Brand", [
    { key: "identifier", label: "Brand Name", type: "text", required: true, readOnlyOnEdit: true },
    { key: "description", label: "Description", type: "textarea" },
  ]),

  categories: () => {
    const beforeDelete = async (record) => {
      if (record.superCategory) return null;
      try {
        const data = await fetchWithAuth("/api/categories/list", {
          method: "POST",
          body: JSON.stringify({ page: 0, sizePerPage: 200 }),
        });
        const list = Array.isArray(data) ? data : (data?.dtoList ?? []);
        return list.some((c) => c.superCategory === record.identifier)
          ? `Cannot delete "${record.identifier}" — it has sub-categories. Delete or reassign them first.`
          : null;
      } catch {
        return null;
      }
    };

    const config = {
      title: "Category Management",
      singularTitle: "Category",
      listEndpoint: "/api/categories/list",
      getEndpoint: (id) => `/api/categories/${id}`,
      saveEndpoint: "/api/categories/save",
      updateEndpoint: (id) => `/api/categories/update/${id}`,
      deleteEndpoint: (id) => `/api/categories/delete/${id}`,
      toggleEndpoint: (id) => `/api/categories/toggle/${id}`,
      toggleField: "active",
      idKey: "identifier",
      pageSize: 20,
      beforeDelete,
      showToggle: (r) => !!r.superCategory,
      loadOptions: async () => {
        try {
          const data = await fetchWithAuth("/api/categories/super-categories");
          return {
            superCategory: data.map((c) => ({ value: c.identifier, label: c.identifier })),
          };
        } catch {
          return {};
        }
      },
      fields: [
        { key: "identifier", label: "Category Name", type: "text", required: true, readOnlyOnEdit: true },
        { key: "superCategory", label: "Super Category", type: "select", placeholder: "-- No Super Category --", options: [] },
        ...auditFields,
      ],
    };
    return config;
  },

  models: () => makeBaseConfig("models", "Model", [
    { key: "identifier", label: "Model Name", type: "text", required: true, readOnlyOnEdit: true },
  ]),

  nodes: () => {
    const config = makeBaseConfig("nodes", "Node", [
      { key: "identifier", label: "Node Name", type: "text", required: true, readOnlyOnEdit: true },
      { key: "path", label: "Path", type: "text", required: true },
      { key: "roles", label: "Roles", type: "select", multiple: true, options: [] },
    ]);
    config.toggleEndpoint = null;
    config.loadOptions = async () => {
      try {
        const data = await fetchWithAuth("/api/role/list", {
          method: "POST",
          body: JSON.stringify({ page: 0, sizePerPage: 100 }),
        });
        const list = Array.isArray(data) ? data : (data?.dtoList ?? []);
        return {
          roles: list.map((r) => ({ value: r.identifier, label: r.identifier })),
        };
      } catch {
        return {};
      }
    };
    return config;
  },

  prices: () => {
    const config = {
      title: "Price Management",
      singularTitle: "Price",
      listEndpoint: "/api/prices/list",
      getEndpoint: (id) => `/api/prices/${id}`,
      saveEndpoint: "/api/prices/save",
      updateEndpoint: (id) => `/api/prices/update/${id}`,
      deleteEndpoint: (id) => `/api/prices/delete/${id}`,
      idKey: "id",
      pageSize: 20,
      currencyFields: ["mrpPrice", "sellingPrice", "costPrice"],
      loadOptions: async () => {
        try {
          const data = await fetchWithAuth("/api/products/active");
          return {
            productId: data.map((p) => ({ value: p.id, label: `${p.identifier} — ${p.productName}` })),
          };
        } catch {
          return {};
        }
      },
      fields: [
        { key: "identifier", label: "SKU Code", hideInForm: true },
        { key: "productName", label: "Product Name", hideInForm: true },
        { key: "mrpPrice", label: "MRP Price (₹)", type: "number", min: "0", required: true },
        { key: "sellingPrice", label: "Selling Price (₹)", type: "number", min: "0", required: true },
        { key: "costPrice", label: "Cost Price (₹)", type: "number", min: "0", required: true },
        { key: "productId", label: "Product", type: "select", required: true, placeholder: "-- Select Product --", options: [], hideInList: true },
        ...auditFields,
      ],
    };
    return config;
  },

  racks: () => {
    const config = makeBaseConfig("racks", "Rack", [
      { key: "identifier", label: "Rack Name", type: "text", required: true, readOnlyOnEdit: true },
      { key: "shelfIdentifiers", label: "Shelves", type: "select", multiple: true, options: [], placeholder: "-- Select Shelves --" },
    ]);
    config.idKey = "id";
    config.pageSize = 2;
    config.toggleEndpoint = null;
    config.loadOptions = async () => {
      try {
        const data = await fetchWithAuth("/api/racks/shelves");
        return {
          shelfIdentifiers: (data ?? []).map((s) => ({ value: s.identifier, label: s.identifier })),
        };
      } catch {
        return {};
      }
    };
    config.fields = [{ key: "id", label: "ID", hideInForm: true }, ...config.fields];
    return config;
  },

  roles: () => makeBaseConfig("role", "Role", [
    { key: "identifier", label: "Role Name", type: "text", required: true, readOnlyOnEdit: true },
    { key: "description", label: "Description", type: "textarea" },
  ]),

  shelves: () => {
    const config = makeBaseConfig("shelves", "Shelf", [
      { key: "identifier", label: "Shelf Name", type: "text", required: true, readOnlyOnEdit: true },
    ]);
    config.idKey = "id";
    return config;
  },

  stocks: () => {
    const config = {
      title: "Stock Management",
      singularTitle: "Stock",
      listEndpoint: "/api/stocks/list",
      getEndpoint: (id) => `/api/stocks/search?productId=${id}&warehouseId=`,
      saveEndpoint: "/api/stocks/save",
      updateEndpoint: (id) => `/api/stocks/update-quantity/${id}`,
      deleteEndpoint: (id) => `/api/stocks/delete/${id}`,
      toggleEndpoint: (id) => `/api/stocks/toggle-status?id=${id}`,
      idKey: "id",
      pageSize: 20,
      loadOptions: async () => {
        try {
          const [products, warehouses] = await Promise.all([
            fetchWithAuth("/api/stocks/products"),
            fetchWithAuth("/api/stocks/warehouses"),
          ]);
          return {
            productId: (products ?? []).map((p) => ({ value: p.id, label: `${p.identifier} — ${p.productName}` })),
            warehouseId: (warehouses ?? []).map((w) => ({ value: w.id, label: w.name })),
          };
        } catch {
          return {};
        }
      },
      getRecord: async (record) => {
        return fetchWithAuth(`/api/stocks/search?productId=${record.productId}&warehouseId=${record.warehouseId}`);
      },
      customUpdateRecord: async (stockId, payload) => {
        return fetchWithAuth(`/api/stocks/update-quantity/${stockId}?quantity=${payload.quantity}`, {
          method: "PUT",
          body: JSON.stringify({}),
        });
      },
      fields: [
        { key: "id", label: "ID", hideInForm: true, hideInList: true },
        { key: "productName", label: "Product", hideInForm: true },
        { key: "warehouseName", label: "Warehouse", hideInForm: true },
        { key: "productId", label: "Product", type: "select", required: true, options: [], hideInList: true },
        { key: "warehouseId", label: "Warehouse", type: "select", required: true, options: [], hideInList: true },
        { key: "quantity", label: "Quantity", type: "number", required: true, min: "0" },
        ...auditFields,
      ],
    };
    return config;
  },

  units: () => makeBaseConfig("units", "Unit", [
    { key: "identifier", label: "Unit Name", type: "text", required: true, readOnlyOnEdit: true },
  ]),

  warehouses: () => makeBaseConfig("warehouses", "Warehouse", [
    { key: "identifier", label: "Warehouse Name", type: "text", required: true, readOnlyOnEdit: true },
    { key: "location", label: "Location", type: "text", required: true },
    { key: "manager", label: "Manager", type: "text", required: true },
]),

  users: () => {
    const config = {
      title: "User Management",
      singularTitle: "User",
      listEndpoint: "/api/user/list",
      getEndpoint: (u) => `/api/user/${u}`,
      saveEndpoint: "/api/security/register",
      updateEndpoint: (u) => `/api/user/update/${u}`,
      deleteEndpoint: (u) => `/api/user/delete/${u}`,
      idKey: "username",
      pageSize: 20,
      onDeleteSelf: () => {
        globalThis.window?.localStorage.removeItem("token");
        globalThis.window?.localStorage.removeItem("username");
        globalThis.window.location.href = "/login";
      },
      getCurrentUserId: () => globalThis.window?.localStorage.getItem("username") ?? null,
      loadOptions: async () => {
        try {
          const response = await fetchWithAuth("/api/role/list", {
            method: "POST",
            body: JSON.stringify({ page: 0, sizePerPage: 100 }),
          });
          const list = Array.isArray(response) ? response : (response?.dtoList ?? []);
          return {
            roles: list.map((role) => ({ value: role.identifier, label: role.identifier })),
          };
        } catch {
          return {};
        }
      },
      fields: [
        { key: "username", label: "Email", type: "email", required: true, readOnlyOnEdit: true },
        { key: "name", label: "Full Name", type: "text", required: true },
        { key: "phoneNo", label: "Phone", type: "tel", required: true, maxLength: 10 },
        { key: "password", label: "Password", type: "password", required: true, hideInList: true, hideOnEdit: true },
        { key: "roles", label: "Roles", type: "select", required: true, multiple: true, options: [] },
        ...auditFields,
      ],
    };
    return config;
  },
};

export const getProductsConfig = () => {
  const config = makeBaseConfig("products", "Product", [
    { key: "productName", label: "Product Name", type: "text", required: true },
    { key: "identifier", label: "SKU Code", type: "text", required: true, readOnlyOnEdit: true },
    { key: "category", label: "Category", type: "select", multiple: true, options: [] },
    { key: "brand", label: "Brand", type: "select", multiple: true, options: [] },
    { key: "model", label: "Model", type: "select", multiple: true, options: [] },
    { key: "unit", label: "Unit", type: "select", multiple: true, options: [] },
  ]);

  config.loadOptions = async () => {
    try {
      const [cats, brands, models, units] = await Promise.all([
        fetchWithAuth("/api/categories/active"),
        fetchWithAuth("/api/brands/active"),
        fetchWithAuth("/api/models/active"),
        fetchWithAuth("/api/units/active"),
      ]);
      const toOpts = (arr) =>
        arr.map((x) => ({ value: x.identifier, label: x.identifier }));
      return {
        category: toOpts(cats),
        brand: toOpts(brands),
        model: toOpts(models),
        unit: toOpts(units),
      };
    } catch {
      return {};
    }
  };

  return config;
};
