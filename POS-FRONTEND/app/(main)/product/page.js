"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList";

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
  const [units, setUnits] = useState([]);
  const [brands, setBrands] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [viewProduct, setViewProduct] = useState(null);
  const sizePerPage = 5;

  const [newProduct, setNewProduct] = useState({
    identifier: "",
    supplierId: "",
    category: "",
    unit: "",
    brand: "",
  });

  const [editProduct, setEditProduct] = useState(null);

  const fetchProducts = async () => {
    try {
      setLoading(true);

      const res = await listItems("product", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      const data = res?.content || [];

      const normalized = data.map((p) => ({
        ...p,
        status: p.status === true || p.status === 1 || p.status === "1",
      }));

      setProducts(normalized);

      setTotalPages(
        res?.totalPages ||
        Math.ceil((res?.totalElements || data.length) / sizePerPage) ||
        1
      );
    } catch (err) {
      console.error("Failed to load products:", err);
      setError("Failed to load products");
    } finally {
      setLoading(false);
    }
  };

  const fetchCategory = async () => {
    const res = await getListItems("category");
    setCategories(res || []);
  };

  const fetchUnits = async () => {
    const res = await getListItems("unit");
    setUnits(res || []);
  };

  const fetchBrands = async () => {
    const res = await getListItems("brand");
    setBrands(res || []);
  };

  useEffect(() => {
    fetchProducts();
  }, [page, searchTerm]);

  useEffect(() => {
    fetchCategory();
    fetchUnits();
    fetchBrands();
  }, []);

  // ✅ TOGGLE STATUS FIX
  const handleToggleStatus = async (row) => {
    const identifier = row.identifier;

    setProducts((prev) =>
      prev.map((p) =>
        p.identifier === identifier
          ? { ...p, status: !p.status }
          : p
      )
    );

    try {
      await toggleItem("product", identifier);
    } catch {
      alert("Toggle failed");
      fetchProducts();
    }
  };

  const handleAdd = async () => {
    if (!newProduct.identifier?.trim()) {
      alert("Identifier is required");
      return;
    }

    if (!newProduct.supplierId?.trim()) {
      alert("Supplier ID is required");
      return;
    }

    if (!newProduct.category) {
      alert("Category is required");
      return;
    }

    if (!newProduct.unit) {
      alert("Unit is required");
      return;
    }

    if (!newProduct.brand) {
      alert("Brand is required");
      return;
    }

    await addItem("product", newProduct);

    setNewProduct({
      identifier: "",
      supplierId: "",
      category: "",
      unit: "",
      brand: "",
    });

    fetchProducts();
  };

  const handleDelete = async (identifier) => {
    await deleteItem("product", identifier);
    setProducts((prev) => prev.filter((p) => p.identifier !== identifier));
  };

  const columns = [
    {
      label: "Sl No",
      render: (row, index) => page * sizePerPage + index + 1,
    },
    { label: "Identifier", key: "identifier" },
    { label: "Supplier", key: "supplierId" },
    { label: "Category", key: "category" },
    { label: "Unit", key: "unit" },
    { label: "Brand", key: "brand" },

    // ✅ IMPORTANT CHANGE (NO CHECKBOX HERE)
    {
      label: "Status",
      key: "status",
    },
  ];

  const actions = [
    { label: "👁 View", onClick: (row) => setViewProduct(row) },
    { label: "✏️ Edit", onClick: (row) => setEditProduct(row) },
    { label: "🗑 Delete", onClick: (row) => handleDelete(row.identifier) },
  ];

  const categoryOptions = categories.map((c) => ({
    label: c.identifier,
    value: c.identifier,
  }));

  const unitOptions = units.map((u) => ({
    label: u.identifier,
    value: u.identifier,
  }));

  const brandOptions = brands.map((b) => ({
    label: b.identifier,
    value: b.identifier,
  }));

  const commonFields = [
    {
      name: "supplierId",
      label: "Supplier ID",
    },
    {
      name: "category",
      label: "Category",
      type: "select",
      options: categoryOptions,
    },
    {
      name: "unit",
      label: "Unit",
      type: "select",
      options: unitOptions,
    },
    {
      name: "brand",
      label: "Brand",
      type: "select",
      options: brandOptions,
    },
  ];

  const addFields = [
    { name: "identifier", label: "Identifier" },
    ...commonFields,
  ];

  const editFields = [
    {
      name: "identifier",
      label: "Identifier",
      disabled: true,
    },
    ...commonFields,
  ];

  return (
    <CommonList
      title="Products"
      data={products}
      columns={columns}
      loading={loading}
      error={error}
      page={page}
      setPage={setPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      newItem={newProduct}
      setNewItem={setNewProduct}
      onAdd={() => {
        setNewProduct({
          identifier: "",
          supplierId: "",
          category: "",
          unit: "",
          brand: "",
        });
      }}
      handleAdd={handleAdd}
      addFields={addFields}
      editItem={editProduct}
      setEditItem={setEditProduct}
      editFields={editFields}
      handleUpdate={async () => {
        await updateItem("product", editProduct);
        setEditProduct(null);
        fetchProducts();
      }}
      actions={actions}
      emptyMessage="No products found"
      onToggleStatus={handleToggleStatus}
      viewItem={viewProduct}
      setViewItem={setViewProduct}
    />
  );
};

export default ProductList;