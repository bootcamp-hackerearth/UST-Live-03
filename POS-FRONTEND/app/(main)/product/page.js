"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";

import {
  listItems,
  deleteItem,
  toggleItem,
  updateItem,
  addItem,
  getListItems,
} from "@/services/api";

const ProductList = () => {

  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [warehouses, setWarehouses] = useState([]);
  const [viewProduct, setviewProduct] = useState(null);

  const [message , setMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const sizePerPage = 5;

  // ================= ADD =================
  const [newProduct, setNewProduct] =
    useState({
      identifier: "",
      supplierId: "",
      warehouseName: "",
      category: "",
    });

  // ================= EDIT =================
  const [editProduct, setEditProduct] =
    useState(null);

    const fetchWarehouses = async () => {
  try {
    const res = await listItems("warehouse", {
      page: 0,
      sizePerPage: 100,
    });

    const data = res?.content || res || [];
    setWarehouses(data);
  } catch (err) {
    console.log(err);
  }
};

  // ================= FETCH PRODUCTS =================
  const fetchProducts = async () => {

    try {

      setLoading(true);

      setError("");

      const res = await listItems(
        "product",
        {
          page,
          sizePerPage,
          sortField: "id",
          search:searchTerm,
        }
      );

      const data =
        res?.content || [];

      // NORMALIZE STATUS
      const normalized =
        data.map((p) => ({
          ...p,

          status:
            p.status === true ||
            p.status === 1 ||
            p.status === "1",
        }));

      setProducts(normalized);

      setTotalPages(
        res?.totalPages ||
          Math.ceil(
            (
              res?.totalElements ||
              data.length
            ) / sizePerPage
          ) ||
          1
      );

    } catch (err) {

      console.error(err);

      setError(
        "Failed to load products"
      );

    } finally {

      setLoading(false);

    }
  };

  // ================= FETCH CATEGORY =================
  const fetchCategory = async () => {
    try {
      const response =
        await getListItems(
          "category"
        );
      setCategories(
        response || []
      );
    } catch (err) {
      console.log(err);
    }
  };
useEffect(() => {
  fetchProducts();
}, [page, searchTerm]);

useEffect(() => {
  fetchCategory();
  fetchWarehouses();
}, []);

  // ================= ADD =================
  const handleAddProduct =
    async () => {

      try {
        const res = await addItem(
          "product",
          newProduct
        );
        if(res?.success === false){
            setMessage(res.message);
            return false;
        }
        setMessage("Price Added Successfully");
        setNewProduct({
          identifier: "",
          supplierId: "",
          warehouseName: "",
          category: "",
        });

      await fetchProducts();

      } catch (err) {
  setMessage(
    err?.response?.data?.message ||
    err?.message ||
    "Add failed"
  );
}
    };

  // ================= UPDATE =================
  const handleUpdate =
    async () => {

      try {

        await updateItem(
          "product",
          editProduct
        );

        setProducts((prev) =>
          prev.map((p) =>
            p.identifier ===
            editProduct.identifier
              ? editProduct
              : p
          )
        );
        setEditProduct(null);
        setMessage("");
        await fetchProducts();
      } 
      catch (err) {
  console.error(err);

  setMessage(
    err?.response?.data?.message ||
    err?.message ||
    "Update failed"
  );
}
    };

  // ================= DELETE =================
  const handleDelete =
    async (identifier) => {

      const confirmDelete =
        globalThis.confirm(
          `Delete ${identifier}?`
        );

      if (!confirmDelete) return;

      try {

        await deleteItem(
          "product",
          identifier
        );

        setProducts((prev) =>
          prev.filter(
            (p) =>
              p.identifier !==
              identifier
          )
        );

      } catch (err) {
  console.error(err);

  setMessage(
    err?.response?.data?.message ||
    err?.message ||
    "Update failed"
  );
}
    };

  // ================= TOGGLE =================
  const handleToggleStatus =
    async (identifier) => {

      // SMOOTH UI UPDATE
      setProducts((prev) =>
        prev.map((p) =>
          p.identifier ===
          identifier
            ? {
                ...p,
                status: !p.status,
              }
            : p
        )
      );

      try {

        await toggleItem(
          "product",
          identifier
        );

      } catch (err) {
  console.error(err);

  setMessage(
    err?.response?.data?.message ||
    err?.message ||
    "Toggle failed"
  );

  // REFETCH IF FAILED
  await fetchProducts();
}
    };

  // ================= COLUMNS =================
  const columns = [
    {
      label: "ID",
      render: (row, index) =>
        page * sizePerPage + index + 1,
    },

    {
      label: "Identifier",
      key: "identifier",
    },

    {
      label: "Supplier",
      key: "supplierId",
    },

    {
      label: "Warehouse",
      key: "warehouseName",
    },

    {
      label: "Category",
      key: "category",
    },
      {
  label: "Status",
  render: (p) => (
    <label
      htmlFor={`status-${p.identifier}`}
      className="switch"
      aria-label={`Toggle status for ${p.identifier}`}
    >
      <input
        id={`status-${p.identifier}`}
        type="checkbox"
        checked={p.status}
        onChange={() =>
          handleToggleStatus(
            p.identifier
          )
        }
      />

      <span
        className="slider"
        aria-hidden="true"
      ></span>

      <span className="sr-only">
        Toggle status for {p.identifier}
      </span>
    </label>
  ),
},
  ];

  // ================= ACTIONS =================
  const actions = [
    {
      label: "👁️",
      onClick: (row) => setviewProduct(row),
    },
  {
    label: "✏️",
    onClick: (row) =>
      setEditProduct({
        ...row,

        warehouseName:
          typeof row.warehouseName === "object"
            ? row.warehouseName?.identifier
            : row.warehouseName || "",

        category:
          typeof row.category === "object"
            ? row.category?.identifier
            : row.category || "",
      }),
  },

  {
    label: "🗑",
    onClick: (row) =>
      handleDelete(row.identifier),
  },
];

  // ================= ADD FIELDS =================
  const addFields = [
    {
      name: "identifier",
      label: "Identifier",
    },

    {
      name: "supplierId",
      label: "Supplier ID",
    },

    {
  name: "warehouseName",
  label: "Warehouse",
  type: "select",
  options: warehouses.map((w) => ({
    label: w.identifier,
    value: w.identifier,
  })),
},

    {
      name: "category",
      label: "Category",

      type: "select",

      options:
        categories.map((cat) => ({
          label:
            cat.identifier,

          value:
            cat.identifier,
        })),
    },
  ];

  // ================= EDIT FIELDS =================
  const editFields = [
    
    {
      name: "identifier",

      label: "Identifier",

      disabled: true,
    },

    {
      name: "supplierId",

      label: "Supplier ID",
    },

    {
  name: "warehouseName",
  label: "Warehouse",
  type: "select",
  options: warehouses.map((w) => ({
  label: w.identifier,
  value: w.identifier,
  isDisabled: false,
})),
},

    {
      name: "category",

      label: "Category",

      type: "select",

      options:
        categories.map((cat) => ({
          label:
            cat.identifier,

          value:
            cat.identifier,
        })),
    },
  ];

  return (
    <>
    <CommonList
      title="Products"

      data={products}

      columns={columns}

      loading={loading}

      error={error}

      page={page}

      setPage={setPage}

      sizePerPage={sizePerPage}

      totalPages={totalPages}

      message={message}
      setMessage={setMessage}

      // ADD
      onAdd={() => {}}

      addButtonText="+ Add Product"

      newItem={newProduct}

      setNewItem={
        setNewProduct
      }

      handleAdd={
        handleAddProduct
      }

      addFields={addFields}

      // EDIT
      editItem={editProduct}

      setEditItem={
        setEditProduct
      }

      handleUpdate={
        handleUpdate
      }

      editFields={editFields}

      // ACTIONS
      actions={actions}

      searchTerm={searchTerm}

      setSearchTerm={setSearchTerm}

      emptyMessage="No products found"
    />
    {viewProduct && (
      <div className="modalOverlay">
        <div className="viewModal">
          <div className="modalHeader">
            <h3>Product Details</h3>

            <button
              className="closeBtn"
              onClick={() =>
                setviewProduct(null)
              }
            >
              ✕
            </button>
          </div>

          <div className="viewContent">
            <div className="viewRow">
              <span>Name</span>
              <strong>
                {viewProduct.identifier}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created By</span>
              <strong>
                {viewProduct.createdBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created On</span>
              <strong>
                {viewProduct.createdOn ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified By</span>
              <strong>
                {viewProduct.modifiedBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified On</span>
              <strong>
                {viewProduct.modifiedOn ||
                  "-"}
              </strong>
            </div>
          </div>
        </div>
      </div>
    )}
    </>
  );
};

export default ProductList;