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

  const [brands, setBrands] = useState([]);
  const [units, setUnits] = useState([]);

  const [viewItem, setViewItem] = useState(null);
  
  // ================= ADD =================

  const [newProduct, setNewProduct] = useState({
    identifier: "",
    supplierId: "",
    brand: "",
    unit: "",
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
    fetchBrands();
    fetchUnits();
  }, []);

  // ================= FETCH BRAND =================

  const fetchBrands = async () => {
    try {
      const res = await listItems("brand", {
        page: 0,
        sizePerPage: 100,
        sortField: "identifier",
      });

      console.log("BRAND RESPONSE:", res);

      setBrands(res?.content || res || []);
    } catch (err) {
      console.error("Failed to load brands", err);
    }
  };

  // ================= FETCH UNITS =================
  const fetchUnits = async () => {
    try {
      const res = await listItems("unit", {
        page: 0,
        sizePerPage: 100,
        sortField: "identifier",
      });

      console.log("UNIT RESPONSE:", res);

      setUnits(res?.content || res || []);
    } catch (err) {
      console.error("Failed to load units", err);
    }
  };

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
          response.message ||
          "Product already exists"
        );
        return false;
      }

      setNewProduct({
        identifier: "",
        supplierId: "",
        brand: "",
        unit: "",
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

  const brandOptions = brands.map((brand) => ({
    label: brand.identifier,
    value: brand.identifier,
  }));

  const unitOptions = units.map((unit) => ({
    label: unit.identifier,
    value: unit.identifier,
  }));

  // ================= UPDATE =================

  const handleUpdate = async () => {
    try {
      console.log("Updating Product:", editProduct);

      await updateItem("product", editProduct);

      await fetchProducts();
      setEditProduct(null);
    } catch (err) {
      console.error("UPDATE ERROR:", err);
      console.error("Response:", err.response?.data);

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
      label: "Supplier ID",
      key: "supplierId",
    },
    {
      label: "Brand",
      key: "brand",
    },
    {
      label: "Unit",
      key: "unit",
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
            onChange={() =>
              handleToggleStatus(
                p.identifier
              )
            }
          />
          <span className="slider"></span>
        </label>
      ),
    },
  ];

  // ================= ACTIONS =================

  const actions = [
    {
      label: "View 👁️",
      onClick: (row) => setViewItem(row),
    },
    {
      label: "Edit ✏️",
      onClick: (row) =>
        setEditProduct(row),
    },
    {
      label: "Delete 🗑",
      onClick: (row) =>
        handleDelete(row.identifier),
    },
  ];

  // ================= DROPDOWNS =================

  const categoryOptions =
    categories.map((cat) => ({
      label: cat.identifier,
      value: cat.identifier,
    }));

  // ================= ADD FIELDS =================

  const addFields = [
    {
      name: "identifier",
      label: "Identifier",
      required: true,
    },
    {
      name: "supplierId",
      label: "Supplier ID",
      type: "number",
       required: true,
    },
    {
      name: "brand",
      label: "Brand",
      type: "select",
      options: brandOptions,
       required: true,
    },
    {
      name: "unit",
      label: "Unit",
      type: "select",
      options: unitOptions,
       required: true,
    },
    {
      name: "category",
      label: "Category",
      type: "select",
      options: categoryOptions,
       required: true,
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
      type: "number",
    },
    {
      name: "brand",
      label: "Brand",
      type: "select",
      options: brandOptions,
    },
    {
      name: "unit",
      label: "Unit",
      type: "select",
      options: unitOptions,
    },
    {
      name: "category",
      label: "Category",
      type: "select",
      options: categoryOptions,
    },
  ];

  console.log("Brands:", brands);
  console.log("Units:", units);

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
      onAdd={() => {
        setAddError("");
      }}
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
      viewItem={viewItem}
      setViewItem={setViewItem}
      emptyMessage="No products found"
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
    />
  );
};

export default ProductList;