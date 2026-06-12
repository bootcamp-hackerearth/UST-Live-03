"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList";

import {
  listItems,
  deleteItem,
  toggleItem,
  updateItem,
  addItem,
} from "@/services/api";

const ProductList = () => {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const sizePerPage = 5;

 const [addError, setAddError] = useState("");

 const [searchTerm, setSearchTerm] = useState("");

  // ================= ADD =================

  const [newProduct, setNewProduct] = useState({
    identifier: "",
    supplierId: "",
    warehouseName: "",
    category: "",
  });

  // ================= EDIT =================

  const [editProduct, setEditProduct] = useState(null);

  // ================= FETCH PRODUCTS =================

  const fetchProducts = async () => {
    try {
      setLoading(true);

      setError("");

      const res = await listItems("product", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      const data = res?.content || [];

      const normalized = data.map((p) => ({
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
            (res?.totalElements || data.length) /
              sizePerPage
          ) ||
          1
      );
    } catch (err) {
      console.error(err);

      setError("Failed to load products");
    } finally {
      setLoading(false);
    }
  };

  // ================= FETCH CATEGORY =================

  const fetchCategory = async () => {
    try {
      const res = await listItems("category", {
        page: 0,
        sizePerPage: 100,
        sortField: "identifier",
      });

      console.log(
        "CATEGORY RESPONSE:",
        res
      );

      setCategories(
        res?.content || res || []
      );
    } catch (err) {
      console.error(
        "Failed to load categories",
        err
      );
    }
  };

  useEffect(() => {
    fetchProducts();
  }, [page, searchTerm]);

  useEffect(() => {
    fetchCategory();
  }, []);

  // ================= ADD =================
const handleAddProduct = async () => {
  try {
    setAddError("");

    const response = await addItem(
      "product",
      newProduct
    );

    if (response?.success === false) {
      setAddError(
        response.message || "Product already exists"
      );
      return false;
    }

    setNewProduct({
      identifier: "",
      supplierId: "",
      warehouseName: "",
      category: "",
    });

    await fetchProducts();
    return true;
  } catch (err) {
    console.error(err);

    setAddError(
      err.response?.data?.message ||
      "Add failed"
    );
    return false;
  }
};

  // ================= UPDATE =================

  const handleUpdate = async () => {
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
    } catch (err) {
      console.error(err);

      alert("Update failed");
    }
  };

  // ================= DELETE =================

  const handleDelete = async (
    identifier
  ) => {
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

      alert("Delete failed");
    }
  };

  // ================= TOGGLE =================

  const handleToggleStatus =
    async (identifier) => {
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

        alert("Toggle failed");

        fetchProducts();
      }
    };

  // ================= COLUMNS =================

  const columns = [
    {
      label: "Sl No",
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
        <label className="switch">
          <input
          type="checkbox"
          checked={p.status}
          aria-label={`Toggle status for ${p.identifier}`}
          onChange={() => handleToggleStatus(p.identifier)}
          />
          <span className="slider"></span>
          </label>
          ),
        },
      ];

  // ================= ACTIONS =================

  const actions = [
    {
      label: "✏️",
      onClick: (row) =>
        setEditProduct(row),
    },
    {
      label: "🗑",
      onClick: (row) =>
        handleDelete(
          row.identifier
        ),
    },
  ];

  // ================= DROPDOWNS =================

  const categoryOptions =
    categories.map((cat) => ({
      label: cat.identifier,
      value: cat.identifier,
    }));

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
      label: "Warehouse Name",
    },
    {
      name: "category",
      label: "Category",
      type: "select",
      options: categoryOptions,
    },
  ];

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
      label: "Warehouse Name",
    },
    {
      name: "category",
      label: "Category",
      type: "select",
      options: categoryOptions,
    },
  ];

  console.log(
    "Categories:",
    categories
  );

  return (
    <CommonList
      title="Products"
      data={products}
      columns={columns}
      loading={loading}
      error={error}
      addError={addError}
      page={page}
      setPage={setPage}
      sizePerPage={sizePerPage}
      totalPages={totalPages}
      onAdd={() => {setAddError("");}}
      addButtonText="+ Add Product"
      newItem={newProduct}
      setNewItem={setNewProduct}
      handleAdd={handleAddProduct}
      addFields={addFields}
      editItem={editProduct}
      setEditItem={setEditProduct}
      handleUpdate={handleUpdate}
      editFields={editFields}
      actions={actions}
      emptyMessage="No products found"
      searchTerm={searchTerm}
  setSearchTerm={setSearchTerm}
    />
  );
};

export default ProductList;